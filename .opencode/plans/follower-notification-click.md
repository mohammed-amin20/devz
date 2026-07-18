# Follower Notification Click — Implementation Plan

## Goal
When tapping a "follower" notification (in-app or FCM push), navigate to the follower's profile instead of a question.

---

## 1. `NotificationsViewModel.kt` — Add FOLLOWER type, actorId to UiModel

```kotlin
// Add to enum
enum class NotificationType {
    ACCEPTED, UPVOTE, LIKE, ANSWER, FOLLOWER
}

// Add actorId to data class
data class NotificationUiModel(
    val id: String,
    val type: NotificationType,
    val actorName: String?,
    val message: String,
    val questionId: Int,
    val actorId: Int = 0,               // NEW
    val questionTitle: String,
    val timeAgo: String,
    val isRead: Boolean = false,
)

// In loadNotifications() map actorId:
NotificationUiModel(
    ...
    actorId = notification.actorId,     // NEW
    ...
)

// In mapTypeString add:
"follower" -> NotificationType.FOLLOWER
```

---

## 2. `NotificationsScreen.kt` — Change callback signature, add follower icon

```kotlin
// Change parameter:
onNotificationClick: (notification: NotificationUiModel) -> Unit = {},

// Update click handler:
onClick = {
    viewModel.onAction(NotificationsAction.MarkRead(notification.id))
    onNotificationClick(notification)       // pass full model
}

// In NotificationItem icon selector, add:
NotificationType.FOLLOWER -> Icons.Filled.Person

// In text builder, handle FOLLOWER (no questionTitle):
if (notification.type == NotificationType.FOLLOWER) {
    // Just show "actorName started following you"
} else {
    // Existing: "actorName message questionTitle"
}
```

---

## 3. `HomeScreen.kt` — Dispatch based on notification type

```kotlin
NotificationsScreen(
    onNotificationClick = { notification ->
        if (notification.type == NotificationType.FOLLOWER) {
            navigateToProfile(notification.actorId)
        } else {
            navigateToQuestionDetails(notification.questionId)
        }
    }
)
```

---

## 4. `FcmPushSender.kt` — Add optional actorId parameter

```kotlin
suspend fun sendPush(
    fcmToken: String,
    title: String,
    body: String,
    questionId: Int?,
    type: String,
    actorId: Int? = null,               // NEW
) = withContext(Dispatchers.IO) {
    // In data payload:
    putJsonObject("data") {
        put("type", type)
        if (type == "follower" && actorId != null) {
            put("actorId", actorId.toString())
        } else {
            put("questionId", questionId?.toString() ?: "")
        }
    }
}
```

---

## 5. `DevzFirebaseMessagingService.kt` — Parse actorId from data

```kotlin
override fun onMessageReceived(message: RemoteMessage) {
    ...
    val questionId = data["questionId"]?.toIntOrNull()
    val actorId = data["actorId"]?.toIntOrNull()   // NEW
    showNotification(title, body, questionId, actorId)
}

// Update showNotification signature:
private fun showNotification(title: String, body: String, questionId: Int?, actorId: Int?) {
    // Add actorId to intent extras
    actorId?.let { putExtra("actorId", it) }
}
```

---

## 6. `MainActivity.kt` — Extract pendingActorId

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val pendingQuestionId = intent?.getIntExtra("questionId", 0)?.takeIf { it > 0 }
    val pendingActorId = intent?.getIntExtra("actorId", 0)?.takeIf { it > 0 }   // NEW

    ...
    DevzNavHost(
        pendingQuestionId = pendingQuestionId,
        pendingActorId = pendingActorId,
        ...
    )
}
```

Also need to pass `actorId` in the re-notification flow in `showPendingNotification()`.

---

## 7. `DevzNavHost.kt` — Handle pendingActorId deep link

```kotlin
@Composable
fun DevzNavHost(
    modifier: Modifier = Modifier,
    pendingQuestionId: Int? = null,
    pendingActorId: Int? = null,          // NEW
) {
    ...
    var handledDeepLink by remember { mutableStateOf(false) }

    // Handle both deep links
    LaunchedEffect(pendingQuestionId, pendingActorId) {
        if (handledDeepLink) return@LaunchedEffect
        when {
            pendingActorId != null -> {
                handledDeepLink = true
                navController.navigate(Route.Profile(pendingActorId))
            }
            pendingQuestionId != null -> {
                handledDeepLink = true
                navController.navigate(Route.QuestionDetails(pendingQuestionId))
            }
        }
    }
}
```

---

## 8. `ProfileViewModel.kt` — Pass actorId to sendPush

```kotlin
// In toggleFollow(), after the notification insert, update sendPush call:
fcmPushSender.sendPush(
    fcmToken = recipient.fcmToken,
    title = "New follower",
    body = "${actor.fullName} started following you",
    questionId = 0,
    type = "follower",
    actorId = currentId,               // NEW
)
```

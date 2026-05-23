package com.mohamed.devz.feature.profile.presentation.view_profile.components

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.collections.emptyList


// ── ProfileUiState ────────────────────────────────────────────────────────────
data class ProfileUiModel(
    val fullName: String,
    val username: String,
    val points: String,
    val answerCount: Int,
    val questionCount: Int,
    val acceptedRate: String,
    val globalRank: String,
    val skills: List<String>
)

data class ProfileQuestionUiModel(
    val id: Int,
    val title: String,
    val timeAgo: String,
    val votes: Int,
    val answerCount: Int,
    val tags: List<String>
)

data class ProfileAnswerUiModel(
    val id: Int,
    val questionTitle: String,
    val preview: String,
    val likes: Int,
    val comments: Int,
    val timeAgo: String,
    val isAccepted: Boolean
)

data class ProfileUiState(
    val id: Int = 0,
    val profile: ProfileUiModel? = null,
    val myQuestions: List<ProfileQuestionUiModel> = listOf(
        ProfileQuestionUiModel(
            id = 1,
            title = "Implementing zero-copy serialization in Rust with rkyv for high-throughput messaging",
            timeAgo = "2 days ago",
            votes = 12,
            answerCount = 4,
            tags = listOf("rust", "serialization")
        ),
        ProfileQuestionUiModel(
            id = 2,
            title = "Optimizing hydration mismatch in Next.js 14 with localized suspense boundaries",
            timeAgo = "1 week ago",
            votes = 45,
            answerCount = 0,
            tags = listOf("nextjs", "performance")
        ),
        ProfileQuestionUiModel(
            id = 3,
            title = "How to implement clean architecture with Jetpack Compose and Hilt?",
            timeAgo = "3 days ago",
            votes = 30,
            answerCount = 7,
            tags = listOf("kotlin", "android", "hilt")
        ),
        ProfileQuestionUiModel(
            id = 4,
            title = "Best practices for managing coroutine scopes in a multi-module Android project",
            timeAgo = "2 weeks ago",
            votes = 18,
            answerCount = 2,
            tags = listOf("kotlin", "coroutines")
        ),
        ProfileQuestionUiModel(
            id = 5,
            title = "Firestore pagination with Flow and Room caching strategy",
            timeAgo = "1 month ago",
            votes = 67,
            answerCount = 11,
            tags = listOf("firebase", "room", "flow")
        )
    ),
    val myAnswers: List<ProfileAnswerUiModel> = listOf(
        ProfileAnswerUiModel(
            id = 1,
            questionTitle = "Optimizing Memory Management in High-Concurrency Rust Services",
            preview = "To truly optimize memory in this scenario, you should avoid the default global allocator. Consider using jemalloc or mimalloc as a drop-in replacement, which significantly reduces fragmentation in multi-threaded workloads.",
            likes = 1200,
            comments = 42,
            timeAgo = "2 days ago",
            isAccepted = true
        ),
        ProfileAnswerUiModel(
            id = 2,
            questionTitle = "Handling Complex State Propagation in Microfrontends",
            preview = "The core challenge here is event-bus saturation. I recommend a federated state model where each module owns its domain but broadcasts thin events rather than deep state snapshots. This maintains loose coupling while ensuring eventual consistency.",
            likes = 84,
            comments = 8,
            timeAgo = "1 week ago",
            isAccepted = false
        ),
        ProfileAnswerUiModel(
            id = 3,
            questionTitle = "Best Practices for Horizontal Scaling of WebSockets",
            preview = "Sticky sessions are only half the battle. You need a backplane for broadcasting — Redis Pub/Sub works well here. Each server instance subscribes to a shared channel and forwards messages to its locally connected clients.",
            likes = 2400,
            comments = 156,
            timeAgo = "Oct 12, 2023",
            isAccepted = true
        ),
        ProfileAnswerUiModel(
            id = 4,
            questionTitle = "How to implement clean architecture with Jetpack Compose and Hilt?",
            preview = "The key is keeping your Composables completely dumb. They should only receive UI state and dispatch actions. All business logic lives in the ViewModel, and the repository layer handles data sources independently.",
            likes = 340,
            comments = 19,
            timeAgo = "3 days ago",
            isAccepted = true
        ),
        ProfileAnswerUiModel(
            id = 5,
            questionTitle = "Firestore pagination with Flow and Room caching strategy",
            preview = "Use a RemoteMediator with Paging 3 library. It handles the coordination between Firestore as your remote source and Room as your local cache automatically, giving you offline support and seamless pagination.",
            likes = 56,
            comments = 5,
            timeAgo = "2 weeks ago",
            isAccepted = false
        )
    ),
    val isLoading: Boolean = false,
    val error: String? = null
)

// ── ProfileViewModel ──────────────────────────────────────────────────────────
@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()
}



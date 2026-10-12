package com.mohamed.devz.feature.profile.presentation.edit_profile

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.mohamed.devz.R
import com.mohamed.devz.feature.profile.presentation.edit_profile.components.GovernanceToggle
import com.mohamed.devz.feature.profile.presentation.edit_profile.components.SectionHeader
import com.mohamed.devz.feature.profile.presentation.edit_profile.components.SkillChip
import com.mohamed.devz.feature.profile.presentation.edit_profile.components.SocialField
import com.mohamed.devz.ui.components.DevzButton
import com.mohamed.devz.ui.components.DevzTextButton
import com.mohamed.devz.ui.theme.DevzTheme
import com.mohamed.devz.ui.theme.ThemeMode

@Composable
fun EditProfileScreen(
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val bytes = context.contentResolver.openInputStream(it)?.readBytes()
            if (bytes != null) {
                viewModel.onAction(EditProfileAction.PickImage(bytes))
            }
        }
    }

    EditProfileContent(
        state = uiState,
        onNavigateUp = navigateUp,
        onSave = { viewModel.onAction(EditProfileAction.Save(navigateUp)) },
        onPickImage = { imagePickerLauncher.launch("image/*") },
        onClearError = { viewModel.onAction(EditProfileAction.ClearError) },
        onFullNameChanged = { viewModel.onAction(EditProfileAction.FullNameChanged(it)) },
        onUsernameChanged = { viewModel.onAction(EditProfileAction.UsernameChanged(it)) },
        onBioChanged = { viewModel.onAction(EditProfileAction.BioChanged(it)) },
        onAddSkill = { viewModel.onAction(EditProfileAction.AddSkill) },
        onRemoveSkill = { viewModel.onAction(EditProfileAction.RemoveSkill(it)) },
        onShowSkillInput = { viewModel.onAction(EditProfileAction.ShowSkillInput) },
        onSkillInputChanged = { viewModel.onAction(EditProfileAction.SkillInputChanged(it)) },
        onGithubChanged = { viewModel.onAction(EditProfileAction.GithubChanged(it)) },
        onLinkedinChanged = { viewModel.onAction(EditProfileAction.LinkedinChanged(it)) },
        onWebsiteChanged = { viewModel.onAction(EditProfileAction.WebsiteChanged(it)) },
        onTogglePublicProfile = { viewModel.onAction(EditProfileAction.TogglePublicProfile) },
        onToggleDisplayEmail = { viewModel.onAction(EditProfileAction.ToggleDisplayEmail) },
        onDeactivateAccount = { viewModel.onAction(EditProfileAction.DeactivateAccount) },
        modifier = modifier,
    )
}

@Composable
fun EditProfileContent(
    state: EditProfileState,
    onNavigateUp: () -> Unit,
    onSave: () -> Unit,
    onPickImage: () -> Unit,
    onClearError: () -> Unit,
    onFullNameChanged: (String) -> Unit,
    onUsernameChanged: (String) -> Unit,
    onBioChanged: (String) -> Unit,
    onAddSkill: () -> Unit,
    onRemoveSkill: (String) -> Unit,
    onShowSkillInput: () -> Unit,
    onSkillInputChanged: (String) -> Unit,
    onGithubChanged: (String) -> Unit,
    onLinkedinChanged: (String) -> Unit,
    onWebsiteChanged: (String) -> Unit,
    onTogglePublicProfile: () -> Unit,
    onToggleDisplayEmail: () -> Unit,
    onDeactivateAccount: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = 16.dp)
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 0.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onNavigateUp,
                    modifier = Modifier.padding(0.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.profile_back_desc),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = stringResource(R.string.editprofile_title),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            }
            DevzTextButton(
                label = stringResource(R.string.editprofile_save),
                onClick = onSave,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Error banner ──────────────────────────────────────────────
        state.error?.let { error ->
            val errorMessage = error.asString()
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.errorContainer,
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontSize = 13.sp,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = onClearError,
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = stringResource(R.string.editprofile_dismiss_error),
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier
                                .size(16.dp)
                                .rotate(45f)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // ── Avatar picker ─────────────────────────────────────────────
        val localBitmap = state.localImageBytes?.let { bytes ->
            remember(bytes) {
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
            }
        }
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (localBitmap != null) {
                    Image(
                        bitmap = localBitmap,
                        contentDescription = null,
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else if (state.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = state.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(52.dp)
                    )
                }
                if (state.isUploadingImage) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp),
                            strokeWidth = 2.5.dp
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable(onClick = onPickImage),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.CameraAlt,
                    contentDescription = stringResource(R.string.editprofile_avatar_desc),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.editprofile_avatar_selection),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(32.dp))

        // ── Core Identity ─────────────────────────────────────────────
        SectionHeader(title = stringResource(R.string.editprofile_core_identity))
        Spacer(modifier = Modifier.height(16.dp))

        FieldLabel(stringResource(R.string.editprofile_full_name))
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = state.fullName,
            onValueChange = onFullNameChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    stringResource(R.string.editprofile_full_name_hint),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = devzTextFieldColors(),
            textStyle = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        FieldLabel(stringResource(R.string.editprofile_username))
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = state.username,
            onValueChange = onUsernameChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    stringResource(R.string.editprofile_username_hint),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = devzTextFieldColors(),
            textStyle = MaterialTheme.typography.bodyMedium,
            leadingIcon = {
                Text(
                    "@",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        FieldLabel(stringResource(R.string.editprofile_bio))
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = state.bio,
            onValueChange = onBioChanged,
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp),
            placeholder = {
                Text(
                    stringResource(R.string.editprofile_bio_hint),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            singleLine = false,
            shape = RoundedCornerShape(12.dp),
            colors = devzTextFieldColors(),
            textStyle = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ── Technical Stack ───────────────────────────────────────────
        SectionHeader(title = stringResource(R.string.editprofile_technical_stack))
        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(start = 2.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(14.dp)
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.skills.forEach { skill ->
                        SkillChip(
                            skill = skill,
                            onRemove = { onRemoveSkill(skill) }
                        )
                    }
                    Button(
                        onClick = onShowSkillInput,
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(
                            width = 0.5.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                stringResource(R.string.editprofile_add_interest),
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 12.sp,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }
                }

                if (state.showSkillInput) {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = state.skillInput,
                        onValueChange = onSkillInputChanged,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                stringResource(R.string.editprofile_skill_hint),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = onAddSkill) {
                                Icon(
                                    Icons.Rounded.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = devzTextFieldColors(),
                        textStyle = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ── Social Mesh ───────────────────────────────────────────────
        SectionHeader(title = stringResource(R.string.editprofile_social_mesh))
        Spacer(modifier = Modifier.height(16.dp))

        SocialField(
            icon = Icons.Filled.Code,
            value = state.github,
            onValueChange = onGithubChanged,
            placeholder = stringResource(R.string.editprofile_github_hint)
        )
        Spacer(modifier = Modifier.height(10.dp))
        SocialField(
            icon = Icons.Filled.Link,
            value = state.linkedin,
            onValueChange = onLinkedinChanged,
            placeholder = stringResource(R.string.editprofile_linkedin_hint)
        )
        Spacer(modifier = Modifier.height(10.dp))
        SocialField(
            icon = Icons.Filled.Language,
            value = state.website,
            onValueChange = onWebsiteChanged,
            placeholder = stringResource(R.string.editprofile_website_hint)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ── Governance ────────────────────────────────────────────────
        SectionHeader(title = stringResource(R.string.editprofile_governance))
        Spacer(modifier = Modifier.height(16.dp))

        GovernanceToggle(
            title = stringResource(R.string.editprofile_public_profile),
            subtitle = stringResource(R.string.editprofile_public_profile_desc),
            checked = state.isPublicProfile,
            onCheckedChange = { onTogglePublicProfile() }
        )
        Spacer(modifier = Modifier.height(10.dp))
        GovernanceToggle(
            title = stringResource(R.string.editprofile_display_email),
            subtitle = stringResource(R.string.editprofile_display_email_desc),
            checked = state.displayEmail,
            onCheckedChange = { onToggleDisplayEmail() }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ── Save button ───────────────────────────────────────────────
        DevzButton(
            label = stringResource(R.string.editprofile_save_changes),
            onClick = onSave,
            loading = state.isLoading,
        )

        Spacer(modifier = Modifier.height(16.dp))

        DevzTextButton(
            label = stringResource(R.string.editprofile_deactivate),
            onClick = onDeactivateAccount,
        )

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.5.sp,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun devzTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = Color.Transparent,
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    cursorColor = MaterialTheme.colorScheme.primary,
    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
)

@Preview(name = "Edit Profile — Dark")
@Composable
private fun PrevEditProfileDark() {
    DevzTheme(themeMode = ThemeMode.DARK) {
        EditProfileContent(
            state = EditProfileState(
                fullName = "John Doe",
                username = "johndoe",
                bio = "Android developer passionate about Compose.",
                skills = listOf("Kotlin", "Compose", "KMP"),
                github = "https://github.com/johndoe",
                linkedin = "https://linkedin.com/in/johndoe",
                website = "https://johndoe.dev",
            ),
            onNavigateUp = {},
            onSave = {},
            onPickImage = {},
            onClearError = {},
            onFullNameChanged = {},
            onUsernameChanged = {},
            onBioChanged = {},
            onAddSkill = {},
            onRemoveSkill = {},
            onShowSkillInput = {},
            onSkillInputChanged = {},
            onGithubChanged = {},
            onLinkedinChanged = {},
            onWebsiteChanged = {},
            onTogglePublicProfile = {},
            onToggleDisplayEmail = {},
            onDeactivateAccount = {},
        )
    }
}

@Preview(name = "Edit Profile — Light", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PrevEditProfileLight() {
    DevzTheme(themeMode = ThemeMode.LIGHT) {
        EditProfileContent(
            state = EditProfileState(
                fullName = "John Doe",
                username = "johndoe",
                bio = "Android developer passionate about Compose.",
                skills = listOf("Kotlin", "Compose", "KMP"),
            ),
            onNavigateUp = {},
            onSave = {},
            onPickImage = {},
            onClearError = {},
            onFullNameChanged = {},
            onUsernameChanged = {},
            onBioChanged = {},
            onAddSkill = {},
            onRemoveSkill = {},
            onShowSkillInput = {},
            onSkillInputChanged = {},
            onGithubChanged = {},
            onLinkedinChanged = {},
            onWebsiteChanged = {},
            onTogglePublicProfile = {},
            onToggleDisplayEmail = {},
            onDeactivateAccount = {},
        )
    }
}
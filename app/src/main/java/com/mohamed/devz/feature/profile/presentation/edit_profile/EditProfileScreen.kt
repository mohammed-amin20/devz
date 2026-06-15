package com.mohamed.devz.feature.profile.presentation.edit_profile

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.mohamed.devz.feature.profile.presentation.edit_profile.components.GovernanceToggle
import com.mohamed.devz.feature.profile.presentation.edit_profile.components.SectionHeader
import com.mohamed.devz.feature.profile.presentation.edit_profile.components.SkillChip
import com.mohamed.devz.feature.profile.presentation.edit_profile.components.SocialField
import com.mohamed.devz.ui.theme.CyanPrimary
import com.mohamed.devz.ui.theme.DevzCard
import com.mohamed.devz.ui.theme.DevzTheme
import com.mohamed.devz.ui.theme.LabelGray
import com.mohamed.devz.ui.theme.TextGray
import com.mohamed.devz.ui.theme.TextSubtle
import com.mohamed.devz.ui.theme.TextWhite
import androidx.compose.runtime.remember
import coil3.compose.AsyncImage

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF131313))
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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = navigateUp,
                    modifier = Modifier
                        .padding(0.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        null,
                        tint = CyanPrimary
                    )
                }
                Text(
                    text = "Edit Profile",
                    color = TextWhite,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            }
            TextButton(onClick = {
                viewModel.onAction(EditProfileAction.Save(navigateUp))
            }) {
                Text(
                    text = "Save",
                    color = CyanPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Error banner ──────────────────────────────────────────────
        uiState.error?.let { error ->
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
                        onClick = { viewModel.onAction(EditProfileAction.ClearError) },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = "Dismiss",
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.size(16.dp).rotate(45f)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // ── Avatar picker ─────────────────────────────────────────────
        val localBitmap = uiState.localImageBytes?.let { bytes ->
            remember(bytes) {
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
            }
        }
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(DevzCard)
                    .border(2.dp, CyanPrimary.copy(alpha = 0.5f), CircleShape),
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
                } else if (uiState.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = uiState.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Filled.Person,
                        null,
                        tint = CyanPrimary,
                        modifier = Modifier.size(52.dp)
                    )
                }
                // Loading overlay during upload
                if (uiState.isUploadingImage) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = CyanPrimary,
                            modifier = Modifier.size(28.dp),
                            strokeWidth = 2.5.dp
                        )
                    }
                }
            }
            // Camera button
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(CyanPrimary)
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.CameraAlt,
                    null,
                    tint = Color.Black,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "AVATAR SELECTION",
            color = TextGray,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(32.dp))

        // ── Core Identity ─────────────────────────────────────────────
        SectionHeader(title = "Core Identity")
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "FULL NAME",
            color = LabelGray,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.5.sp,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = uiState.fullName,
            onValueChange = { viewModel.onAction(EditProfileAction.FullNameChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    "Enter your full name",
                    color = TextSubtle,
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

        Text(
            text = "USERNAME",
            color = LabelGray,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.5.sp,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = uiState.username,
            onValueChange = { viewModel.onAction(EditProfileAction.UsernameChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    "Choose a username",
                    color = TextSubtle,
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
                    color = TextSubtle,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "BIO",
            color = LabelGray,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.5.sp,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = uiState.bio,
            onValueChange = { viewModel.onAction(EditProfileAction.BioChanged(it)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp),
            placeholder = {
                Text(
                    "Tell us about yourself",
                    color = TextSubtle,
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
        SectionHeader(title = "Technical Stack")
        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            shape = RoundedCornerShape(14.dp),
            color = CyanPrimary.copy(alpha = 0.3f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(start = 2.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF1C1B1B))
                    .padding(14.dp)
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.skills.forEach { skill ->
                        SkillChip(
                            skill = skill,
                            onRemove = {
                                viewModel.onAction(EditProfileAction.RemoveSkill(skill))
                            }
                        )
                    }
                    // Add interest button
                    Button(
                        onClick = { viewModel.onAction(EditProfileAction.ShowSkillInput) },
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(
                            width = 0.5.dp,
                            color = Color(0xFF44D8F1).copy(alpha = 0.5f)
                        ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF353534)
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Filled.Add,
                                null,
                                tint = Color(0xFF44D8F1),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                "Add Interest",
                                color = Color(0xFF44D8F1),
                                fontSize = 12.sp,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }
                }

                // Skill input
                if (uiState.showSkillInput) {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = uiState.skillInput,
                        onValueChange = { viewModel.onAction(EditProfileAction.SkillInputChanged(it)) },
                        modifier = Modifier
                            .fillMaxWidth(),
                        placeholder = {
                            Text(
                                "e.g. Kotlin, Rust",
                                color = TextSubtle,
                                fontSize = 13.sp,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { viewModel.onAction(EditProfileAction.AddSkill) }) {
                                Icon(
                                    Icons.Rounded.Check,
                                    null,
                                    tint = Color(0xFF44D8F1)
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
        SectionHeader(title = "Social Mesh")
        Spacer(modifier = Modifier.height(16.dp))

        SocialField(
            icon = Icons.Filled.Code,
            value = uiState.github,
            onValueChange = { viewModel.onAction(EditProfileAction.GithubChanged(it)) },
            placeholder = "github.com/your_username"
        )
        Spacer(modifier = Modifier.height(10.dp))
        SocialField(
            icon = Icons.Filled.Link,
            value = uiState.linkedin,
            onValueChange = { viewModel.onAction(EditProfileAction.LinkedinChanged(it)) },
            placeholder = "linkedin.com/in/your_name"
        )
        Spacer(modifier = Modifier.height(10.dp))
        SocialField(
            icon = Icons.Filled.Language,
            value = uiState.website,
            onValueChange = { viewModel.onAction(EditProfileAction.WebsiteChanged(it)) },
            placeholder = "your-website.dev"
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ── Governance ────────────────────────────────────────────────
        SectionHeader(title = "Governance")
        Spacer(modifier = Modifier.height(16.dp))

        GovernanceToggle(
            title = "Public Profile Visibility",
            subtitle = "Allow others to see your technical queries",
            checked = uiState.isPublicProfile,
            onCheckedChange = {
                viewModel.onAction(EditProfileAction.TogglePublicProfile)
            }
        )
        Spacer(modifier = Modifier.height(10.dp))
        GovernanceToggle(
            title = "Display Email Address",
            subtitle = "Visible to verified team members",
            checked = uiState.displayEmail,
            onCheckedChange = {
                viewModel.onAction(EditProfileAction.ToggleDisplayEmail)
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ── Save button ───────────────────────────────────────────────
        Button(
            onClick = { viewModel.onAction(EditProfileAction.Save(navigateUp)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = CyanPrimary
                ),
            shape = RoundedCornerShape(16.dp),
            enabled = !uiState.isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = CyanPrimary,
                contentColor = Color.Black
            )
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    color = Color.Black,
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    "Save Profile Changes",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00363E),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { viewModel.onAction(EditProfileAction.DeactivateAccount) }) {
            Text(
                text = "Deactivate Account",
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleLarge
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun devzTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color.Transparent,
    unfocusedBorderColor = Color.Transparent,
    focusedTextColor = TextWhite,
    unfocusedTextColor = TextWhite,
    cursorColor = CyanPrimary,
    focusedContainerColor = Color(0xFF2A2A2A),
    unfocusedContainerColor = Color(0xFF2A2A2A)
)

@Preview
@Composable
private fun PrevEditProfile() {
    DevzTheme {
        EditProfileScreen(
            navigateUp = {}
        )
    }
}






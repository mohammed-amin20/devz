package com.mohamed.devz.feature.profile.presentation.edit_profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier



import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.mohamed.devz.feature.authentication.presentation.components.signup_screen.presentation.AuthFieldLabel
import com.mohamed.devz.feature.authentication.presentation.components.signup_screen.presentation.DevzTextField
import com.mohamed.devz.feature.profile.presentation.edit_profile.components.GovernanceToggle
import com.mohamed.devz.feature.profile.presentation.edit_profile.components.SectionHeader
import com.mohamed.devz.feature.profile.presentation.edit_profile.components.SkillChip
import com.mohamed.devz.feature.profile.presentation.edit_profile.components.SocialField
import com.mohamed.devz.ui.theme.CyanPrimary
import com.mohamed.devz.ui.theme.DevzCard
import com.mohamed.devz.ui.theme.DevzInput
import com.mohamed.devz.ui.theme.TextGray
import com.mohamed.devz.ui.theme.TextSubtle
import com.mohamed.devz.ui.theme.TextWhite


@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

        Column(

            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF0D3333),
                            Color(0xFF0A1A1A),
                            Color(0xFF060D0D)
                        ),
                        center = Offset(0.5f, 0.45f),
                        radius = 1200f
                    )
                )
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 16.dp),
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
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TextWhite)
                }
                Text(
                    text = "Edit Profile",
                    color = TextWhite,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = {
                    viewModel.onAction(EditProfileAction.Save, onSaved)
                }) {
                    Text(
                        text = "Save",
                        color = CyanPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Avatar picker ─────────────────────────────────────────────
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(DevzCard)
                        .border(2.dp, CyanPrimary.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Person,
                        null,
                        tint = CyanPrimary,
                        modifier = Modifier.size(52.dp)
                    )
                }
                // Camera button
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(CyanPrimary)
                        .clickable { viewModel.onAction(EditProfileAction.PickAvatar) },
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
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(32.dp))

            // ── Core Identity ─────────────────────────────────────────────
            SectionHeader(
                title = "Core Identity",
                icon = Icons.Filled.Person
            )
            Spacer(modifier = Modifier.height(16.dp))

            AuthFieldLabel("FULL NAME")
            Spacer(modifier = Modifier.height(8.dp))
            DevzTextField(
                value = uiState.fullName,
                onValueChange = { viewModel.onAction(EditProfileAction.FullNameChanged(it)) },
                placeholder = "Alexander Vance"
            )
            Spacer(modifier = Modifier.height(16.dp))

            AuthFieldLabel("USERNAME")
            Spacer(modifier = Modifier.height(8.dp))
            DevzTextField(
                value = uiState.username,
                onValueChange = { viewModel.onAction(EditProfileAction.UsernameChanged(it)) },
                placeholder = "architect_dev",
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

            AuthFieldLabel("BIO")
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.bio,
                onValueChange = { viewModel.onAction(EditProfileAction.BioChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                placeholder = {
                    Text(
                        "Full-stack architect specializing in...",
                        color = TextSubtle,
                        fontSize = 13.sp
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanPrimary,
                    unfocusedBorderColor = Color(0xFF2A3A3A),
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    cursorColor = CyanPrimary,
                    focusedContainerColor = DevzInput,
                    unfocusedContainerColor = DevzInput
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ── Technical Stack ───────────────────────────────────────────
            SectionHeader(title = "Technical Stack", icon = Icons.Filled.Code)
            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = DevzCard,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
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
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFF0F1A1A))
                                .border(1.dp, Color(0xFF2A3A3A), RoundedCornerShape(20.dp))
                                .clickable { viewModel.onAction(EditProfileAction.ShowSkillInput) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Filled.Add, null, tint = TextGray, modifier = Modifier.size(14.dp))
                                Text("Add Interest", color = TextGray, fontSize = 12.sp)
                            }
                        }
                    }

                    // Skill input
                    if (uiState.showSkillInput) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = uiState.skillInput,
                            onValueChange = { viewModel.onAction(EditProfileAction.SkillInputChanged(it)) },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("e.g. Kotlin, Rust", color = TextSubtle, fontSize = 13.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            trailingIcon = {
                                IconButton(onClick = { viewModel.onAction(EditProfileAction.AddSkill) }) {
                                    Icon(Icons.Filled.Check, null, tint = CyanPrimary)
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanPrimary,
                                unfocusedBorderColor = Color(0xFF2A3A3A),
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                cursorColor = CyanPrimary,
                                focusedContainerColor = DevzInput,
                                unfocusedContainerColor = DevzInput
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Social Mesh ───────────────────────────────────────────────
            SectionHeader(title = "Social Mesh", icon = Icons.Filled.Link)
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
            SectionHeader(title = "Governance", icon = Icons.Filled.Shield)
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
                onClick = { viewModel.onAction(EditProfileAction.Save, onSaved) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(50),
                enabled = !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyanPrimary,
                    contentColor = Color.Black
                )
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                } else {
                    Text("Save Profile Changes", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Deactivate account ────────────────────────────────────────
            TextButton(onClick = { viewModel.onAction(EditProfileAction.DeactivateAccount) }) {
                Text(
                    text = "Deactivate Account",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }


@Preview
@Composable
private fun PrevEditProfile() {
    EditProfileScreen(
        onBack = {},
        onSaved = {}
    )
}






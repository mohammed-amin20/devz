package com.mohamed.devz.feature.company.presentation.edit_company_profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.mohamed.devz.ui.theme.CyanPrimary
import com.mohamed.devz.ui.theme.DevzCard
import com.mohamed.devz.ui.theme.LabelGray
import com.mohamed.devz.ui.theme.QError
import com.mohamed.devz.ui.theme.TextSubtle
import com.mohamed.devz.ui.theme.TextWhite

@Composable
fun EditCompanyProfileOverlay(
    visible: Boolean,
    onDismiss: () -> Unit,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditCompanyProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val bytes = context.contentResolver.openInputStream(it)?.readBytes()
            if (bytes != null) {
                viewModel.onAction(EditCompanyProfileAction.PickImage(bytes))
            }
        }
    }

    LaunchedEffect(visible) {
        if (visible) {
            viewModel.onAction(EditCompanyProfileAction.ClearError)
            viewModel.loadProfile()
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(300)),
        exit = fadeOut(tween(250)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { onDismiss() },
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.92f)
                    .padding(horizontal = 16.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {},
                shape = RoundedCornerShape(24.dp),
                color = DevzCard,
                tonalElevation = 8.dp,
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(start = 20.dp, end = 8.dp, top = 12.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Edit Company Profile",
                            color = TextWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge,
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color(0xFF8A8A8A))
                        }
                    }

                    when {
                        uiState.isLoading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator(color = CyanPrimary)
                            }
                        }

                        else -> {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .verticalScroll(rememberScrollState())
                                    .imePadding()
                                    .padding(horizontal = 20.dp),
                            ) {
                                uiState.error?.let { error ->
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        color = QError.copy(alpha = 0.15f),
                                    ) {
                                        Text(
                                            text = error.asString(),
                                            color = QError,
                                            fontSize = 13.sp,
                                            modifier = Modifier.padding(12.dp),
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                }

                                Box(contentAlignment = Alignment.BottomEnd) {
                                    Box(
                                        modifier = Modifier
                                            .size(96.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF1E1E1E))
                                            .border(2.dp, CyanPrimary.copy(alpha = 0.5f), CircleShape),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        if (uiState.logoUrl.isNotEmpty()) {
                                            AsyncImage(
                                                model = uiState.logoUrl,
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .size(96.dp)
                                                    .clip(CircleShape),
                                                contentScale = ContentScale.Crop,
                                            )
                                        } else {
                                            Icon(
                                                Icons.Filled.Business,
                                                contentDescription = null,
                                                tint = CyanPrimary,
                                                modifier = Modifier.size(52.dp),
                                            )
                                        }
                                        if (uiState.isUploadingImage) {
                                            Box(
                                                modifier = Modifier
                                                    .size(96.dp)
                                                    .clip(CircleShape)
                                                    .background(Color.Black.copy(alpha = 0.5f)),
                                                contentAlignment = Alignment.Center,
                                            ) {
                                                CircularProgressIndicator(
                                                    color = CyanPrimary,
                                                    modifier = Modifier.size(28.dp),
                                                    strokeWidth = 2.5.dp,
                                                )
                                            }
                                        }
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(30.dp)
                                            .clip(CircleShape)
                                            .background(CyanPrimary)
                                            .clickable { imagePickerLauncher.launch("image/*") },
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Icon(
                                            Icons.Filled.CameraAlt,
                                            contentDescription = null,
                                            tint = Color.Black,
                                            modifier = Modifier.size(16.dp),
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "COMPANY LOGO",
                                    color = LabelGray,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 2.sp,
                                )
                                Spacer(modifier = Modifier.height(24.dp))

                                FieldLabel("COMPANY NAME")
                                EditField(
                                    value = uiState.companyName,
                                    placeholder = "Enter company name",
                                    onValueChange = { viewModel.onAction(EditCompanyProfileAction.CompanyNameChanged(it)) },
                                    singleLine = true,
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                FieldLabel("INDUSTRY")
                                EditField(
                                    value = uiState.industry,
                                    placeholder = "e.g. Software, Fintech, Design",
                                    onValueChange = { viewModel.onAction(EditCompanyProfileAction.IndustryChanged(it)) },
                                    singleLine = true,
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                FieldLabel("LOCATION")
                                EditField(
                                    value = uiState.location,
                                    placeholder = "e.g. Cairo, Egypt",
                                    onValueChange = { viewModel.onAction(EditCompanyProfileAction.LocationChanged(it)) },
                                    singleLine = true,
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                FieldLabel("WEBSITE")
                                EditField(
                                    value = uiState.website,
                                    placeholder = "https://your-company.com",
                                    onValueChange = { viewModel.onAction(EditCompanyProfileAction.WebsiteChanged(it)) },
                                    singleLine = true,
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                FieldLabel("TWITTER / X")
                                EditField(
                                    value = uiState.twitterUrl,
                                    placeholder = "https://x.com/your-company",
                                    onValueChange = { viewModel.onAction(EditCompanyProfileAction.TwitterChanged(it)) },
                                    singleLine = true,
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                FieldLabel("DESCRIPTION")
                                EditField(
                                    value = uiState.description,
                                    placeholder = "Short description of the company",
                                    onValueChange = { viewModel.onAction(EditCompanyProfileAction.DescriptionChanged(it)) },
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                FieldLabel("BIO")
                                EditField(
                                    value = uiState.bio,
                                    placeholder = "Tell developers more about your company",
                                    onValueChange = { viewModel.onAction(EditCompanyProfileAction.BioChanged(it)) },
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Button(
                            onClick = onDismiss,
                            enabled = !uiState.isSaving,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2A2A2A),
                                contentColor = TextWhite,
                            ),
                        ) {
                            Text("Cancel", fontWeight = FontWeight.SemiBold)
                        }
                        Button(
                            onClick = { viewModel.onAction(EditCompanyProfileAction.Save(onSaved)) },
                            enabled = !uiState.isSaving,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyanPrimary,
                                contentColor = Color.Black,
                            ),
                        ) {
                            if (uiState.isSaving) {
                                CircularProgressIndicator(
                                    color = Color.Black,
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                )
                            } else {
                                Text("Save", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        color = LabelGray,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.5.sp,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun EditField(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = false,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                placeholder,
                color = TextSubtle,
                fontSize = 13.sp,
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        singleLine = singleLine,
        shape = RoundedCornerShape(12.dp),
        colors = editTextFieldColors(),
        textStyle = MaterialTheme.typography.bodyMedium,
    )
}

@Composable
private fun editTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color.Transparent,
    unfocusedBorderColor = Color.Transparent,
    focusedTextColor = TextWhite,
    unfocusedTextColor = TextWhite,
    cursorColor = CyanPrimary,
    focusedContainerColor = Color(0xFF2A2A2A),
    unfocusedContainerColor = Color(0xFF2A2A2A),
)

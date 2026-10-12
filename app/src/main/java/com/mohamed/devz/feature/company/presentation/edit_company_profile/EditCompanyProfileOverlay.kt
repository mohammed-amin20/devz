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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.mohamed.devz.R
import com.mohamed.devz.ui.theme.DevzTheme
import com.mohamed.devz.ui.theme.ThemeMode

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
                color = MaterialTheme.colorScheme.surfaceContainer,
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
                            text = stringResource(R.string.company_edit_title),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge,
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = stringResource(R.string.company_edit_close),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }

                    when {
                        uiState.isLoading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
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
                                        color = MaterialTheme.colorScheme.errorContainer,
                                    ) {
                                        Text(
                                            text = error.asString(),
                                            color = MaterialTheme.colorScheme.onErrorContainer,
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
                                            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                            .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), CircleShape),
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
                                                tint = MaterialTheme.colorScheme.primary,
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
                                                    color = MaterialTheme.colorScheme.primary,
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
                                            .background(MaterialTheme.colorScheme.primary)
                                            .clickable { imagePickerLauncher.launch("image/*") },
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Icon(
                                            Icons.Filled.CameraAlt,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(16.dp),
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                FieldLabel(stringResource(R.string.company_edit_logo))
                                Spacer(modifier = Modifier.height(24.dp))

                                FieldLabel(stringResource(R.string.company_edit_field_name))
                                EditField(
                                    value = uiState.companyName,
                                    placeholder = stringResource(R.string.company_edit_name_hint),
                                    onValueChange = { viewModel.onAction(EditCompanyProfileAction.CompanyNameChanged(it)) },
                                    singleLine = true,
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                FieldLabel(stringResource(R.string.company_edit_field_industry))
                                EditField(
                                    value = uiState.industry,
                                    placeholder = stringResource(R.string.company_edit_industry_hint),
                                    onValueChange = { viewModel.onAction(EditCompanyProfileAction.IndustryChanged(it)) },
                                    singleLine = true,
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                FieldLabel(stringResource(R.string.company_edit_field_location))
                                EditField(
                                    value = uiState.location,
                                    placeholder = stringResource(R.string.company_edit_location_hint),
                                    onValueChange = { viewModel.onAction(EditCompanyProfileAction.LocationChanged(it)) },
                                    singleLine = true,
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                FieldLabel(stringResource(R.string.company_edit_field_website))
                                EditField(
                                    value = uiState.website,
                                    placeholder = stringResource(R.string.company_edit_website_hint),
                                    onValueChange = { viewModel.onAction(EditCompanyProfileAction.WebsiteChanged(it)) },
                                    singleLine = true,
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                FieldLabel(stringResource(R.string.company_edit_field_twitter))
                                EditField(
                                    value = uiState.twitterUrl,
                                    placeholder = stringResource(R.string.company_edit_twitter_hint),
                                    onValueChange = { viewModel.onAction(EditCompanyProfileAction.TwitterChanged(it)) },
                                    singleLine = true,
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                FieldLabel(stringResource(R.string.company_edit_field_description))
                                EditField(
                                    value = uiState.description,
                                    placeholder = stringResource(R.string.company_edit_description_hint),
                                    onValueChange = { viewModel.onAction(EditCompanyProfileAction.DescriptionChanged(it)) },
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                FieldLabel(stringResource(R.string.company_edit_field_bio))
                                EditField(
                                    value = uiState.bio,
                                    placeholder = stringResource(R.string.company_edit_bio_hint),
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
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                contentColor = MaterialTheme.colorScheme.onSurface,
                            ),
                        ) {
                            Text(stringResource(R.string.company_edit_cancel), fontWeight = FontWeight.SemiBold)
                        }
                        Button(
                            onClick = { viewModel.onAction(EditCompanyProfileAction.Save(onSaved)) },
                            enabled = !uiState.isSaving,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                        ) {
                            if (uiState.isSaving) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                )
                            } else {
                                Text(stringResource(R.string.company_edit_save), fontWeight = FontWeight.SemiBold)
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
        text = text.uppercase(),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    cursorColor = MaterialTheme.colorScheme.primary,
    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
)

@Preview
@Composable
private fun PreviewEditCompanyProfileDark() {
    DevzTheme(themeMode = ThemeMode.DARK) {
        EditCompanyProfileOverlay(visible = true, onDismiss = {}, onSaved = {})
    }
}

@Preview
@Composable
private fun PreviewEditCompanyProfileLight() {
    DevzTheme(themeMode = ThemeMode.LIGHT) {
        EditCompanyProfileOverlay(visible = true, onDismiss = {}, onSaved = {})
    }
}

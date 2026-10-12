package com.mohamed.devz.feature.authentication.presentation.components.signup_screen.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.mohamed.devz.R
import com.mohamed.devz.ui.components.BrandHero
import com.mohamed.devz.ui.components.DevzButton
import com.mohamed.devz.ui.components.DevzFieldLabel
import com.mohamed.devz.ui.components.DevzTextField
import com.mohamed.devz.ui.theme.DevzTheme

@Composable
fun SignUpScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
    onCompanyRegisterSuccess: () -> Unit = onRegisterSuccess,
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val termsPrefix = stringResource(R.string.signup_terms_prefix)
    val termsTos = stringResource(R.string.signup_terms_tos)
    val termsAnd = stringResource(R.string.signup_terms_and)
    val termsPrivacy = stringResource(R.string.signup_terms_privacy)
    val termsSuffix = stringResource(R.string.signup_terms_suffix)

    BackHandler {
        onNavigateToLogin()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(
                top = 64.dp,
                bottom = 32.dp,
                start = 24.dp,
                end = 24.dp
            )
        ) {
            item {
                BrandHero(logoSize = 48.dp, cornerRadius = 16.dp, fontSize = 26.sp)
                Spacer(modifier = Modifier.height(36.dp))
            }

            item {
                Text(
                    text = stringResource(R.string.signup_title),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                Text(
                    text = stringResource(R.string.signup_subtitle),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(
                            if (uiState.isCompany) R.string.signup_mode_company
                            else R.string.signup_mode_developer
                        ),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Switch(
                        checked = uiState.isCompany,
                        onCheckedChange = { viewModel.onAction(SignUpAction.IsCompanyToggled(it)) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        ),
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Surface(
                    modifier = Modifier
                        .imePadding()
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        if (uiState.isCompany) {
                            DevzFieldLabel(text = stringResource(R.string.signup_label_company_name))
                            Spacer(modifier = Modifier.height(8.dp))
                            DevzTextField(
                                value = uiState.companyName,
                                onValueChange = { viewModel.onAction(SignUpAction.CompanyNameChanged(it)) },
                                placeholder = stringResource(R.string.signup_hint_company),
                                leadingIcon = Icons.Filled.Person,
                                isError = uiState.companyNameError != null,
                                errorMessage = uiState.companyNameError?.asString(),
                            )
                        } else {
                            DevzFieldLabel(text = stringResource(R.string.signup_label_full_name))
                            Spacer(modifier = Modifier.height(8.dp))
                            DevzTextField(
                                value = uiState.fullName,
                                onValueChange = { viewModel.onAction(SignUpAction.FullNameChanged(it)) },
                                placeholder = stringResource(R.string.signup_hint_full_name),
                                leadingIcon = Icons.Filled.Person,
                                isError = uiState.fullNameError != null,
                                errorMessage = uiState.fullNameError?.asString(),
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        DevzFieldLabel(text = stringResource(R.string.signup_label_username))
                        Spacer(modifier = Modifier.height(8.dp))
                        DevzTextField(
                            value = uiState.username,
                            onValueChange = { viewModel.onAction(SignUpAction.UsernameChanged(it)) },
                            placeholder = stringResource(R.string.signup_hint_username),
                            leadingIcon = Icons.Filled.AlternateEmail,
                            isError = uiState.usernameError != null,
                            errorMessage = uiState.usernameError?.asString(),
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        DevzFieldLabel(text = stringResource(R.string.signup_label_email))
                        Spacer(modifier = Modifier.height(8.dp))
                        DevzTextField(
                            value = uiState.email,
                            onValueChange = { viewModel.onAction(SignUpAction.EmailChanged(it)) },
                            placeholder = stringResource(R.string.signup_hint_email),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            leadingIcon = Icons.Filled.Email,
                            isError = uiState.emailError != null,
                            errorMessage = uiState.emailError?.asString(),
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        DevzFieldLabel(text = stringResource(R.string.signup_label_password))
                        Spacer(modifier = Modifier.height(8.dp))
                        DevzTextField(
                            value = uiState.password,
                            onValueChange = { viewModel.onAction(SignUpAction.PasswordChanged(it)) },
                            placeholder = stringResource(R.string.signup_hint_password),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            leadingIcon = Icons.Filled.Lock,
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Filled.VisibilityOff
                                        else Icons.Filled.Visibility,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None
                            else PasswordVisualTransformation(),
                            isError = uiState.passwordError != null,
                            errorMessage = uiState.passwordError?.asString(),
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        DevzFieldLabel(text = stringResource(R.string.signup_label_confirm_password))
                        Spacer(modifier = Modifier.height(8.dp))
                        DevzTextField(
                            value = uiState.confirmPassword,
                            onValueChange = {
                                viewModel.onAction(SignUpAction.ConfirmPasswordChanged(it))
                            },
                            placeholder = stringResource(R.string.signup_hint_password),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            leadingIcon = Icons.Filled.Lock,
                            trailingIcon = {
                                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                    Icon(
                                        imageVector = if (confirmPasswordVisible) Icons.Filled.VisibilityOff
                                        else Icons.Filled.Visibility,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            },
                            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None
                            else PasswordVisualTransformation(),
                            isError = uiState.confirmPasswordError != null,
                            errorMessage = uiState.confirmPasswordError?.asString(),
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = buildAnnotatedString {
                                withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)) {
                                    append(termsPrefix)
                                }
                                withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)) {
                                    append(termsTos)
                                }
                                withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)) {
                                    append(termsAnd)
                                }
                                withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)) {
                                    append(termsPrivacy)
                                }
                                withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)) {
                                    append(termsSuffix)
                                }
                            },
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        DevzButton(
                            label = stringResource(
                                if (uiState.isCompany) R.string.signup_button_company
                                else R.string.signup_button_developer
                            ),
                            onClick = {
                                viewModel.onAction(
                                    SignUpAction.RegisterClicked(
                                        onSuccess = onRegisterSuccess,
                                        onCompanySuccess = onCompanyRegisterSuccess,
                                    )
                                )
                            },
                            enabled = !uiState.isLoading,
                            loading = uiState.isLoading,
                        )
                    }
                }
            }

            item {
                uiState.error?.let {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = it.asString(),
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(28.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.signup_have_account),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                    )
                    TextButton(
                        onClick = onNavigateToLogin,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.signup_login),
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewSignUpScreen() {
    DevzTheme {
        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing
        ) { innerPadding ->
            SignUpScreen(
                onNavigateToLogin = {},
                onRegisterSuccess = {},

                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

package com.mohamed.devz.feature.authentication.presentation.components.signup_screen.presentation

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
import com.mohamed.devz.ui.theme.CyanPrimary
import com.mohamed.devz.ui.theme.DevzCard
import com.mohamed.devz.ui.theme.DevzInput
import com.mohamed.devz.ui.theme.LabelGray
import com.mohamed.devz.ui.theme.TextGray
import com.mohamed.devz.ui.theme.TextSubtle
import com.mohamed.devz.ui.theme.TextWhite
import com.mohamed.devz.R

@Composable
fun SignUpScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
//    viewModel: SignUpScreenViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
//    val uiState by viewModel.uiState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 26.sp)) {
                                append("dev")
                            }
                            withStyle(SpanStyle(color = CyanPrimary, fontWeight = FontWeight.Bold, fontSize = 26.sp)) {
                                append("Z")
                            }
                        }
                    )
                }
                Spacer(modifier = Modifier.height(36.dp))
            }

            item {
                Text(
                    text = "Join the Community",
                    color = TextWhite,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                Text(
                    text = "Create your account to start\narchitecting solutions.",
                    color = TextGray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(36.dp))
            }

            item {
                Surface(
                    modifier = Modifier
                        .imePadding()
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = DevzCard
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        AuthFieldLabel("FULL NAME")
                        Spacer(modifier = Modifier.height(8.dp))
                        DevzTextField(
                            value = /*uiState.fullName*/ "",
                            onValueChange = /*viewModel::onFullNameChange*/ {},
                            placeholder = "John Doe",
                            leadingIcon = { Icon(Icons.Filled.Person, null, tint = TextSubtle, modifier = Modifier.size(20.dp)) }
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        AuthFieldLabel("USERNAME")
                        Spacer(modifier = Modifier.height(8.dp))
                        DevzTextField(
                            value = /*uiState.username*/ "",
                            onValueChange = /*viewModel::onUsernameChange*/ {},
                            placeholder = "architect_dev",
                            leadingIcon = { Icon(Icons.Filled.AlternateEmail, null, tint = TextSubtle, modifier = Modifier.size(20.dp)) }
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        AuthFieldLabel("EMAIL ADDRESS")
                        Spacer(modifier = Modifier.height(8.dp))
                        DevzTextField(
                            value = /*uiState.email*/ "",
                            onValueChange = /*viewModel::onEmailChange*/ {},
                            placeholder = "architect@devz.io",
                            keyboardType = KeyboardType.Email,
                            leadingIcon = { Icon(Icons.Filled.Email, null, tint = TextSubtle, modifier = Modifier.size(20.dp)) }
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        AuthFieldLabel("PASSWORD")
                        Spacer(modifier = Modifier.height(8.dp))
                        DevzTextField(
                            value = /*uiState.password*/ "",
                            onValueChange = /*viewModel::onPasswordChange*/ {},
                            placeholder = "••••••••••••",
                            keyboardType = KeyboardType.Password,
                            isPassword = true,
                            passwordVisible = passwordVisible,
                            onTogglePassword = { passwordVisible = !passwordVisible },
                            leadingIcon = { Icon(Icons.Filled.Lock, null, tint = TextSubtle, modifier = Modifier.size(20.dp)) }
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        AuthFieldLabel("CONFIRM PASSWORD")
                        Spacer(modifier = Modifier.height(8.dp))
                        DevzTextField(
                            value = /*uiState.confirmPassword*/ "",
                            onValueChange = /*viewModel::onConfirmPasswordChange*/ {},
                            placeholder = "••••••••••••",
                            keyboardType = KeyboardType.Password,
                            isPassword = true,
                            passwordVisible = confirmPasswordVisible,
                            onTogglePassword = { confirmPasswordVisible = !confirmPasswordVisible },
                            leadingIcon = { Icon(Icons.Filled.Lock, null, tint = TextSubtle, modifier = Modifier.size(20.dp)) }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Terms
                        Text(
                            text = buildAnnotatedString {
                                withStyle(SpanStyle(color = TextGray, fontSize = 12.sp)) {
                                    append("By signing up, you agree to our ")
                                }
                                withStyle(SpanStyle(color = CyanPrimary, fontSize = 12.sp)) {
                                    append("Terms of Service")
                                }
                                withStyle(SpanStyle(color = TextGray, fontSize = 12.sp)) {
                                    append(" and ")
                                }
                                withStyle(SpanStyle(color = CyanPrimary, fontSize = 12.sp)) {
                                    append("Privacy Policy")
                                }
                                withStyle(SpanStyle(color = TextGray, fontSize = 12.sp)) {
                                    append(".")
                                }
                            },
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Sign Up button
                        Button(
                            onClick = { /*viewModel.register(onRegisterSuccess)*/ },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(50),
                            enabled = /*!uiState.isLoading*/ true,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyanPrimary,
                                contentColor = Color.Black
                            )
                        ) {
                            /*if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    color = Color.Black,
                                    modifier = Modifier.size(22.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {*/
                                Text(
                                    text = "Sign Up →",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
//                            }
                        }
                    }
                }
            }

            // Error
            /*uiState.error?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }*/

            item {
                Spacer(modifier = Modifier.height(28.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Already have an account? ", color = TextGray, fontSize = 14.sp)
                    TextButton(
                        onClick = onNavigateToLogin,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Log In", color = CyanPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

        }
    }
}

@Composable
fun AuthFieldLabel(text: String) {
    Text(
        text = text,
        color = LabelGray,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.5.sp
    )
}

@Composable
fun DevzTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePassword: (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, color = TextSubtle) },
        leadingIcon = leadingIcon,
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = { onTogglePassword?.invoke() }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = null,
                        tint = TextSubtle,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        } else null,
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
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
}

@Preview(showSystemUi = true)
@Composable
private fun Preview() {
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        SignUpScreen(
            onNavigateToLogin = {},
            onRegisterSuccess = {},
//        viewModel = SignUpScreenViewModel(),
            modifier = Modifier.padding(innerPadding)
        )
    }
}
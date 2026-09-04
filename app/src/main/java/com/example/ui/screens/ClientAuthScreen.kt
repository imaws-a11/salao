package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.SalonViewModel
import com.example.ui.theme.BentoBackground
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoBorderLight
import com.example.ui.theme.BentoOnPrimary
import com.example.ui.theme.BentoOnPrimaryContainer
import com.example.ui.theme.BentoPinkAccent
import com.example.ui.theme.BentoPrimary
import com.example.ui.theme.BentoPrimaryContainer
import com.example.ui.theme.BentoSurface
import com.example.ui.theme.BentoSurfaceVariant
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSecondary
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldLoyalty

@Composable
fun ClientAuthScreen(
    viewModel: SalonViewModel,
    modifier: Modifier = Modifier
) {
    val isAuthLoading by viewModel.isClientAuthLoading.collectAsState()
    val authError by viewModel.clientAuthErrorMessage.collectAsState()
    val loyaltyClients by viewModel.loyaltyClients.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Entrar, 1: Criar Conta
    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BentoBackground)
            .testTag("client_auth_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Hero with Salon Identity & Security Badge
            Surface(
                shape = CircleShape,
                color = BentoPrimaryContainer,
                modifier = Modifier.size(72.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = "GlowUp Studio Logo",
                        tint = BentoPrimary,
                        modifier = Modifier.size(38.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "GlowUp Studio",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = BentoTextPrimary
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Bento Segmented Control (Entrar vs Criar Conta)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                color = BentoSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorderLight)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selectedTab == 0) BentoSurface else Color.Transparent)
                            .clickable {
                                selectedTab = 0
                                viewModel.clearClientAuthError()
                            }
                            .padding(vertical = 10.dp)
                            .testTag("auth_tab_login"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Entrar",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 0) BentoPrimary else BentoTextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selectedTab == 1) BentoSurface else Color.Transparent)
                            .clickable {
                                selectedTab = 1
                                viewModel.clearClientAuthError()
                            }
                            .padding(vertical = 10.dp)
                            .testTag("auth_tab_register"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Criar Conta",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 1) BentoPrimary else BentoTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Error Alert Banner
            AnimatedVisibility(
                visible = authError != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFDECEA),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF5C6CB)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = authError.orEmpty(),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFC62828),
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { viewModel.clearClientAuthError() },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Fechar erro",
                                tint = Color(0xFFC62828),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Form container
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = BentoSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder.copy(alpha = 0.5f)),
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Crossfade(
                    targetState = selectedTab,
                    label = "AuthFormTransition",
                    modifier = Modifier.padding(20.dp)
                ) { tab ->
                    if (tab == 0) {
                        LoginForm(
                            isLoading = isAuthLoading,
                            onSubmit = { email, pass -> viewModel.loginClient(email, pass) },
                            onForgotPassword = { showForgotPasswordDialog = true },
                            onSwitchToRegister = {
                                selectedTab = 1
                                viewModel.clearClientAuthError()
                            }
                        )
                    } else {
                        RegisterForm(
                            isLoading = isAuthLoading,
                            onSubmit = { name, phone, email, pass ->
                                viewModel.registerClient(name, phone, email, pass)
                            },
                            onSwitchToLogin = {
                                selectedTab = 0
                                viewModel.clearClientAuthError()
                            }
                        )
                    }
                }
            }
        }
    }

    // Forgot Password Dialog
    if (showForgotPasswordDialog) {
        ForgotPasswordDialog(
            onDismiss = { showForgotPasswordDialog = false },
            onSubmit = { email ->
                viewModel.resetClientPassword(email)
                showForgotPasswordDialog = false
            }
        )
    }
}

private val authInputTextStyle = androidx.compose.ui.text.TextStyle(
    color = Color.Black,
    fontSize = 15.sp,
    fontWeight = FontWeight.Normal
)

@Composable
private fun authTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.Black,
    unfocusedTextColor = Color.Black,
    focusedBorderColor = BentoPrimary,
    unfocusedBorderColor = BentoBorderLight,
    cursorColor = Color.Black,
    focusedLabelColor = BentoPrimary,
    unfocusedLabelColor = BentoTextSecondary,
    focusedPlaceholderColor = BentoTextSecondary,
    unfocusedPlaceholderColor = BentoTextSecondary,
    focusedLeadingIconColor = BentoPrimary,
    unfocusedLeadingIconColor = BentoPrimary,
    focusedTrailingIconColor = BentoTextSecondary,
    unfocusedTrailingIconColor = BentoTextSecondary,
    errorTextColor = Color.Black
)

@Composable
private fun LoginForm(
    isLoading: Boolean,
    onSubmit: (String, String) -> Unit,
    onForgotPassword: () -> Unit,
    onSwitchToRegister: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    val isAdminEmail = email.trim().equals("lauraivini13@gmail.com", ignoreCase = true)

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Bem-vindo(a) de volta!",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = BentoTextPrimary
        )
        Text(
            text = "Informe seu e-mail e senha cadastrados para acessar o salão.",
            style = MaterialTheme.typography.bodySmall,
            color = BentoTextSecondary
        )

        // Email Field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-mail") },
            placeholder = { Text("exemplo@email.com") },
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = null, tint = BentoPrimary)
            },
            singleLine = true,
            textStyle = authInputTextStyle,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            colors = authTextFieldColors(),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_auth_email")
        )

        if (isAdminEmail) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = BentoPrimaryContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = BentoPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "E-mail de Administradora reconhecido (Laura Ivini)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = BentoPrimary
                    )
                }
            }
        }

        // Password Field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Senha") },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null, tint = BentoPrimary)
            },
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (isPasswordVisible) "Ocultar senha" else "Ver senha",
                        tint = BentoTextSecondary
                    )
                }
            },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            textStyle = authInputTextStyle,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    if (email.isNotBlank() && password.isNotBlank() && !isLoading) {
                        onSubmit(email, password)
                    }
                }
            ),
            colors = authTextFieldColors(),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_auth_password")
        )

        // Forgot password link
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = onForgotPassword,
                modifier = Modifier.testTag("btn_forgot_password")
            ) {
                Text(
                    text = "Esqueci minha senha",
                    style = MaterialTheme.typography.labelMedium,
                    color = BentoPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Submit Button
        Button(
            onClick = {
                focusManager.clearFocus()
                onSubmit(email, password)
            },
            enabled = email.isNotBlank() && password.isNotBlank() && !isLoading,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BentoPrimary,
                contentColor = BentoOnPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("btn_auth_submit")
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = BentoOnPrimary,
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isAdminEmail) "Entrar como Administradora" else "Entrar no GlowUp",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Switch to register
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ainda não tem conta?",
                style = MaterialTheme.typography.bodySmall,
                color = BentoTextSecondary
            )
            TextButton(onClick = onSwitchToRegister) {
                Text(
                    text = "Criar conta",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = BentoPrimary
                )
            }
        }
    }
}

@Composable
private fun RegisterForm(
    isLoading: Boolean,
    onSubmit: (String, String, String, String) -> Unit,
    onSwitchToLogin: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    val isPasswordMatching = password.isEmpty() || confirmPassword.isEmpty() || password == confirmPassword
    val isPasswordValid = password.length >= 6

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Crie sua conta no GlowUp",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = BentoTextPrimary
        )

        // VIP Welcome Bonus Badge
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = GoldLoyalty.copy(alpha = 0.15f),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldLoyalty.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFD4AC0D),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Ganhe 50 pontos de boas-vindas no Clube VIP após o cadastro!",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = BentoTextPrimary
                )
            }
        }

        // Name
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nome Completo") },
            placeholder = { Text("ex: Mariana Ferreira") },
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = null, tint = BentoPrimary)
            },
            singleLine = true,
            textStyle = authInputTextStyle,
            keyboardOptions = KeyboardOptions(imeAction = FocusDirection.Down.let { ImeAction.Next }),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            colors = authTextFieldColors(),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_auth_name")
        )

        // Phone / WhatsApp
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("WhatsApp / Celular") },
            placeholder = { Text("ex: (11) 98765-4321") },
            leadingIcon = {
                Icon(Icons.Default.Phone, contentDescription = null, tint = BentoPrimary)
            },
            singleLine = true,
            textStyle = authInputTextStyle,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            colors = authTextFieldColors(),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_auth_phone")
        )

        // Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-mail") },
            placeholder = { Text("seu.email@exemplo.com") },
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = null, tint = BentoPrimary)
            },
            singleLine = true,
            textStyle = authInputTextStyle,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            colors = authTextFieldColors(),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_auth_register_email")
        )

        // Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Senha (mínimo 6 dígitos)") },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null, tint = BentoPrimary)
            },
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (isPasswordVisible) "Ocultar" else "Ver",
                        tint = BentoTextSecondary
                    )
                }
            },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            textStyle = authInputTextStyle,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            colors = authTextFieldColors(),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_auth_register_password")
        )

        // Confirm Password
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar Senha") },
            leadingIcon = {
                Icon(Icons.Default.LockReset, contentDescription = null, tint = BentoPrimary)
            },
            isError = !isPasswordMatching,
            supportingText = {
                if (!isPasswordMatching) {
                    Text("As senhas não coincidem", color = MaterialTheme.colorScheme.error)
                }
            },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            textStyle = authInputTextStyle,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    if (name.isNotBlank() && email.isNotBlank() && isPasswordValid && isPasswordMatching && !isLoading) {
                        onSubmit(name, phone, email, password)
                    }
                }
            ),
            colors = authTextFieldColors(),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_auth_confirm_password")
        )

        // Register Button
        val isFormValid = name.isNotBlank() && phone.isNotBlank() && email.isNotBlank() && isPasswordValid && isPasswordMatching

        Button(
            onClick = {
                focusManager.clearFocus()
                onSubmit(name, phone, email, password)
            },
            enabled = isFormValid && !isLoading,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BentoPrimary,
                contentColor = BentoOnPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("btn_auth_register_submit")
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = BentoOnPrimary,
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Criar Minha Conta & Acessar",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Switch to login
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Já possui uma conta?",
                style = MaterialTheme.typography.bodySmall,
                color = BentoTextSecondary
            )
            TextButton(onClick = onSwitchToLogin) {
                Text(
                    text = "Fazer login",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = BentoPrimary
                )
            }
        }
    }
}

@Composable
private fun ForgotPasswordDialog(
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LockReset,
                    contentDescription = null,
                    tint = BentoPrimary
                )
                Text(
                    text = "Recuperar Senha",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Digite seu e-mail cadastrado. Enviaremos as instruções do Firebase Auth para redefinir sua senha com segurança.",
                    style = MaterialTheme.typography.bodySmall,
                    color = BentoTextSecondary
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("E-mail") },
                    placeholder = { Text("seu.email@exemplo.com") },
                    singleLine = true,
                    textStyle = authInputTextStyle,
                    colors = authTextFieldColors(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(email) },
                enabled = email.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BentoPrimary,
                    contentColor = BentoOnPrimary
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Enviar E-mail")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = BentoTextSecondary)
            }
        }
    )
}

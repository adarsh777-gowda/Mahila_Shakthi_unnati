package com.adarsh.mahilashaktiunnati.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adarsh.mahilashaktiunnati.R
import com.adarsh.mahilashaktiunnati.ui.theme.DesignSystem
import com.adarsh.mahilashaktiunnati.ui.theme.Gradients
import com.adarsh.mahilashaktiunnati.ui.theme.ComponentStyles
import com.adarsh.mahilashaktiunnati.viewmodel.AuthViewModel
import com.adarsh.mahilashaktiunnati.data.UserManager
import com.adarsh.mahilashaktiunnati.ui.components.LanguageSelector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalLoginScreen(
    context: android.content.Context,
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onLanguageChanged: () -> Unit = {}
) {
    var phone by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf("") }
    var isRegisteredNumber by remember { mutableStateOf(false) }
    var loginMethod by remember { mutableStateOf(LoginMethod.PHONE) }
    var password by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    
    // Initialize UserManager with demo users
    LaunchedEffect(Unit) {
        UserManager.initializeDemoUsers()
    }
    
    val status by viewModel.status.collectAsState()
    
    LaunchedEffect(status) {
        if (status is AuthViewModel.AuthStatus.LoggedIn) {
            onLoginSuccess()
        }
    }
    
    // Background gradient
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(Gradients.Background))
    ) {
        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = DesignSystem.Padding.screenHorizontal,
                    vertical = DesignSystem.Padding.screenVertical
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.lg)
        ) {
            // Language Selector
            LanguageSelector(
                context = context,
                onLanguageChanged = onLanguageChanged
            )
            
            // App Header
            LoginAppHeader()
            
            // Login Type Selection with Shadow
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(DesignSystem.Shapes.Large)
                    .shadow(
                        elevation = 8.dp,
                        shape = DesignSystem.Shapes.Large,
                        ambientColor = Color.Black.copy(alpha = 0.2f),
                        spotColor = Color.Black.copy(alpha = 0.4f)
                    ),
                colors = ComponentStyles.getCardColors(),
                elevation = ComponentStyles.getCardElevation()
            ) {
                LoginTypeSelector(
                    selectedMethod = loginMethod,
                    onMethodSelected = { loginMethod = it }
                )
            }
            
            // Login Form with Enhanced Shadow
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(DesignSystem.Shapes.Large)
                    .shadow(
                        elevation = 12.dp,
                        shape = DesignSystem.Shapes.Large,
                        ambientColor = Color.Black.copy(alpha = 0.25f),
                        spotColor = Color.Black.copy(alpha = 0.5f)
                    ),
                colors = ComponentStyles.getCardColors(),
                elevation = ComponentStyles.getCardElevation()
            ) {
                LoginForm(
                    loginMethod = loginMethod,
                    phone = phone,
                    onPhoneChange = { 
                        phone = it
                        phoneError = ""
                        isRegisteredNumber = UserManager.isUserRegistered(it)
                    },
                    phoneError = phoneError,
                    isRegisteredNumber = isRegisteredNumber,
                    password = password,
                    onPasswordChange = { 
                        password = it
                        passwordError = ""
                    },
                    passwordError = passwordError,
                    onLoginClick = {
                        when (loginMethod) {
                            LoginMethod.PHONE -> {
                                if (isRegisteredNumber) {
                                    onLoginSuccess()
                                } else {
                                    phoneError = context.getString(R.string.number_not_registered)
                                }
                            }
                            LoginMethod.PASSWORD -> {
                                if (phone.isNotEmpty() && password.isNotEmpty()) {
                                    if (UserManager.authenticateUser(phone, password)) {
                                        onLoginSuccess()
                                    } else {
                                        passwordError = context.getString(R.string.invalid_credentials)
                                    }
                                } else {
                                    passwordError = context.getString(R.string.enter_both_credentials)
                                }
                            }
                        }
                    },
                    status = status
                )
            }
            
            // Register Link
            RegisterLink(onNavigateToRegister = onNavigateToRegister)
            
            Spacer(modifier = Modifier.height(DesignSystem.Spacing.xxl))
        }
    }
}

@Composable
private fun LoginAppHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.sm)
    ) {
        // App Logo/Icon with Shadow
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(DesignSystem.Shapes.ExtraLarge)
                .background(Brush.horizontalGradient(Gradients.Primary))
                .shadow(
                    elevation = 16.dp,
                    shape = DesignSystem.Shapes.ExtraLarge,
                    ambientColor = DesignSystem.Colors.Primary.copy(alpha = 0.4f),
                    spotColor = DesignSystem.Colors.PrimaryVariant.copy(alpha = 0.6f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "💪",
                fontSize = 36.sp,
                color = DesignSystem.Colors.OnPrimary
            )
        }
        
        // App Name
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = DesignSystem.Typography.Heading3.sp,
                fontWeight = FontWeight.Bold
            ),
            color = DesignSystem.Colors.TextPrimary,
            textAlign = TextAlign.Center
        )
        
        // App Subtitle
        Text(
            text = stringResource(R.string.login_subtitle),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = DesignSystem.Typography.Body2.sp
            ),
            color = DesignSystem.Colors.TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = DesignSystem.Spacing.md)
        )
    }
}

@Composable
private fun LoginTypeSelector(
    selectedMethod: LoginMethod,
    onMethodSelected: (LoginMethod) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(DesignSystem.Shapes.Large),
        colors = ComponentStyles.getCardColors(),
        elevation = CardDefaults.cardElevation(DesignSystem.Elevation.Medium)
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = DesignSystem.Padding.cardHorizontal,
                vertical = DesignSystem.Padding.cardVertical
            ),
            verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.md)
        ) {
            Text(
                text = stringResource(R.string.login_method),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = DesignSystem.Colors.TextPrimary
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.sm)
            ) {
                FilterChip(
                    onClick = { onMethodSelected(LoginMethod.PHONE) },
                    label = { 
                        Text(
                            text = stringResource(R.string.phone_login),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    selected = selectedMethod == LoginMethod.PHONE,
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DesignSystem.Colors.Primary,
                        selectedLabelColor = DesignSystem.Colors.OnPrimary
                    )
                )
                
                FilterChip(
                    onClick = { onMethodSelected(LoginMethod.PASSWORD) },
                    label = { 
                        Text(
                            text = stringResource(R.string.password_login),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    selected = selectedMethod == LoginMethod.PASSWORD,
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DesignSystem.Colors.Primary,
                        selectedLabelColor = DesignSystem.Colors.OnPrimary
                    )
                )
            }
        }
    }
}

@Composable
private fun LoginForm(
    loginMethod: LoginMethod,
    phone: String,
    onPhoneChange: (String) -> Unit,
    phoneError: String,
    isRegisteredNumber: Boolean,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordError: String,
    onLoginClick: () -> Unit,
    status: AuthViewModel.AuthStatus
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(DesignSystem.Shapes.Large),
        colors = ComponentStyles.getCardColors(),
        elevation = CardDefaults.cardElevation(DesignSystem.Elevation.Medium)
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = DesignSystem.Padding.cardHorizontal,
                vertical = DesignSystem.Padding.cardVertical
            ),
            verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.md)
        ) {
            // Phone Number Field
            OutlinedTextField(
                value = phone,
                onValueChange = onPhoneChange,
                label = { Text(stringResource(R.string.member_phone)) },
                placeholder = { Text("+91XXXXXXXXXX", color = DesignSystem.Colors.TextHint) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                enabled = status !is AuthViewModel.AuthStatus.SendingOtp,
                isError = phoneError.isNotEmpty(),
                shape = DesignSystem.Shapes.Medium,
                colors = ComponentStyles.getInputFieldColors()
            )
            
            // Phone Error Message
            if (phoneError.isNotEmpty()) {
                Text(
                    text = phoneError,
                    color = DesignSystem.Colors.Error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = DesignSystem.Spacing.sm)
                )
            }
            
            // Registration Status
            if (phone.isNotEmpty() && !isRegisteredNumber) {
                Text(
                    text = stringResource(R.string.phone_not_registered),
                    color = DesignSystem.Colors.Warning,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = DesignSystem.Spacing.sm)
                )
            }
            
            // Password Field (only for password login)
            if (loginMethod == LoginMethod.PASSWORD) {
                OutlinedTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = { Text(stringResource(R.string.password)) },
                    placeholder = { Text(stringResource(R.string.password_hint), color = DesignSystem.Colors.TextHint) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    enabled = status !is AuthViewModel.AuthStatus.SendingOtp,
                    isError = passwordError.isNotEmpty(),
                    shape = DesignSystem.Shapes.Medium,
                    colors = ComponentStyles.getInputFieldColors()
                )
                
                // Password Error Message
                if (passwordError.isNotEmpty()) {
                    Text(
                        text = passwordError,
                        color = DesignSystem.Colors.Error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = DesignSystem.Spacing.sm)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(DesignSystem.Spacing.sm))
            
            // Login Button
            Button(
                onClick = onLoginClick,
                enabled = when (loginMethod) {
                    LoginMethod.PHONE -> status !is AuthViewModel.AuthStatus.SendingOtp && isRegisteredNumber
                    LoginMethod.PASSWORD -> status !is AuthViewModel.AuthStatus.SendingOtp && phone.isNotEmpty() && password.isNotEmpty()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(DesignSystem.Buttons.Height),
                colors = ComponentStyles.getPrimaryButtonColors(),
                shape = DesignSystem.Shapes.Medium
            ) {
                if (status is AuthViewModel.AuthStatus.SendingOtp) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(DesignSystem.Icons.Medium),
                        strokeWidth = 2.dp,
                        color = DesignSystem.Colors.OnPrimary
                    )
                    Spacer(modifier = Modifier.width(DesignSystem.Spacing.sm))
                    Text(
                        text = "OTP ಕಳುಹಿಸುತ್ತಿದೆ...",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Text(
                        text = when (loginMethod) {
                            LoginMethod.PHONE -> "OTP ಕಳುಹಿಸಿ"
                            LoginMethod.PASSWORD -> "ಲಾಗಿನ್ ಮಾಡಿ"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun RegisterLink(onNavigateToRegister: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.register_prompt),
            style = MaterialTheme.typography.bodyMedium,
            color = DesignSystem.Colors.TextSecondary
        )
        
        Spacer(modifier = Modifier.width(DesignSystem.Spacing.xs))
        
        TextButton(
            onClick = onNavigateToRegister,
            modifier = Modifier.padding(horizontal = DesignSystem.Spacing.sm)
        ) {
            Text(
                text = stringResource(R.string.register),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = DesignSystem.Colors.Primary
            )
        }
    }
}

enum class LoginMethod {
    PHONE,
    PASSWORD
}

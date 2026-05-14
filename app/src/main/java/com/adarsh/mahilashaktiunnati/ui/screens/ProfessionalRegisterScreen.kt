package com.adarsh.mahilashaktiunnati.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adarsh.mahilashaktiunnati.R
import com.adarsh.mahilashaktiunnati.ui.theme.DesignSystem
import com.adarsh.mahilashaktiunnati.ui.theme.Gradients
import com.adarsh.mahilashaktiunnati.ui.theme.ComponentStyles
import com.adarsh.mahilashaktiunnati.data.UserManager
import com.adarsh.mahilashaktiunnati.utils.LanguageManager
import com.adarsh.mahilashaktiunnati.ui.components.LanguageSelector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalRegisterScreen(
    context: android.content.Context,
    onRegisterSuccess: () -> Unit,
    onLoginSuccess: () -> Unit,
    onLanguageChanged: () -> Unit = {}
) {
    var phoneNumber by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }
    var isRegisteredNumber by remember { mutableStateOf(false) }
    var registrationMethod by remember { mutableStateOf(RegistrationMethod.OTP) }
    
    // Form validation states
    var phoneError by remember { mutableStateOf("") }
    var usernameError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }
    var otpError by remember { mutableStateOf("") }
    
    // Initialize UserManager with demo users
    LaunchedEffect(Unit) {
        UserManager.initializeDemoUsers()
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
            RegisterAppHeader()
            
            // Registration Method Selection with Shadow
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
                RegistrationMethodSelector(
                    selectedMethod = registrationMethod,
                    onMethodSelected = { registrationMethod = it }
                )
            }
            
            // Registration Form with Enhanced Shadow
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
                RegistrationForm(
                    registrationMethod = registrationMethod,
                    phoneNumber = phoneNumber,
                    onPhoneChange = { 
                        phoneNumber = it
                        phoneError = ""
                        isRegisteredNumber = UserManager.isUserRegistered(it)
                    },
                    phoneError = phoneError,
                    isRegisteredNumber = isRegisteredNumber,
                    username = username,
                    onUsernameChange = { 
                        username = it
                        usernameError = ""
                    },
                    usernameError = usernameError,
                    password = password,
                    onPasswordChange = { 
                        password = it
                        passwordError = ""
                    },
                    passwordError = passwordError,
                    confirmPassword = confirmPassword,
                    onConfirmPasswordChange = { 
                        confirmPassword = it
                        confirmPasswordError = ""
                    },
                    confirmPasswordError = confirmPasswordError,
                    otp = otp,
                    onOtpChange = { 
                        otp = it
                        otpError = ""
                    },
                    otpError = otpError,
                    isOtpSent = isOtpSent,
                    onRegisterClick = {
                        when (registrationMethod) {
                            RegistrationMethod.OTP -> {
                                if (!isOtpSent) {
                                    // Send OTP
                                    if (phoneNumber.length == 13 && username.isNotEmpty()) {
                                        isOtpSent = true
                                    } else {
                                        phoneError = stringResource(R.string.invalid_phone)
                                    }
                                } else {
                                    // Verify OTP and register
                                    if (otp.length == 6) {
                                        val newUser = com.adarsh.mahilashaktiunnati.data.User(
                                            phoneNumber = phoneNumber,
                                            username = username,
                                            password = "123456" // Default password
                                        )
                                        val registrationSuccess = UserManager.registerUser(newUser)
                                        if (registrationSuccess) {
                                            onRegisterSuccess()
                                        } else {
                                            otpError = stringResource(R.string.invalid_otp)
                                        }
                                    } else {
                                        otpError = stringResource(R.string.invalid_otp)
                                    }
                                }
                            }
                            RegistrationMethod.PASSWORD -> {
                                // Validate and register with password
                                var isValid = true
                                
                                if (phoneNumber.isEmpty() || phoneNumber.length != 13) {
                                    phoneError = stringResource(R.string.invalid_phone)
                                    isValid = false
                                }
                                
                                if (username.isEmpty()) {
                                    usernameError = stringResource(R.string.invalid_username)
                                    isValid = false
                                }
                                
                                if (password.isEmpty()) {
                                    passwordError = "ದಯವಿಟ್ಟು ಪಾಸ್ವರ್ಡ್ ನಮೂದಿಸಿ"
                                    isValid = false
                                }
                                
                                if (password != confirmPassword) {
                                    confirmPasswordError = stringResource(R.string.passwords_not_match)
                                    isValid = false
                                }
                                
                                if (isValid) {
                                    val newUser = com.adarsh.mahilashaktiunnati.data.User(
                                        phoneNumber = phoneNumber,
                                        username = username,
                                        password = password
                                    )
                                    val registrationSuccess = UserManager.registerUser(newUser)
                                    if (registrationSuccess) {
                                        onRegisterSuccess()
                                    } else {
                                        passwordError = "ಈ ಫೋನ್ ಸಂಖ್ಯೆ ಈಗಾಗಲೆ ನೋಂದಾಯಿಸಿದೆ"
                                    }
                                }
                            }
                        }
                    }
                )
            }
            
            // Login Link
            LoginLink(onLoginSuccess = onLoginSuccess)
            
            Spacer(modifier = Modifier.height(DesignSystem.Spacing.xxl))
        }
    }
}

@Composable
internal fun AppHeader() {
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
            text = "💪 ಮಹಿಳ ಶಕ್ತಿ ಸಂಘಟನ",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = DesignSystem.Typography.Heading3.sp,
                fontWeight = FontWeight.Bold
            ),
            color = DesignSystem.Colors.TextPrimary,
            textAlign = TextAlign.Center
        )
        
        // App Subtitle
        Text(
            text = "ನಮ್ಮಹಳಳರ ಸ್ವ-ಸಹಾ ಗುಂಡನಿನಲ್ಲಿ ಸಂಘಟನಕ್ಕಳಿ ಸೇರ್ವಿದಲ್ಲಿ",
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
private fun RegistrationMethodSelector(
    selectedMethod: RegistrationMethod,
    onMethodSelected: (RegistrationMethod) -> Unit
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
                text = "ನೋಂದಣಿ ವಿಧಾನ",
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
                    onClick = { onMethodSelected(RegistrationMethod.OTP) },
                    label = { 
                        Text(
                            text = "📱 OTP",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    selected = selectedMethod == RegistrationMethod.OTP,
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DesignSystem.Colors.Primary,
                        selectedLabelColor = DesignSystem.Colors.OnPrimary
                    )
                )
                
                FilterChip(
                    onClick = { onMethodSelected(RegistrationMethod.PASSWORD) },
                    label = { 
                        Text(
                            text = "🔐 ಪಾಸ್ವರ್ಡ್",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    selected = selectedMethod == RegistrationMethod.PASSWORD,
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
private fun RegistrationForm(
    registrationMethod: RegistrationMethod,
    phoneNumber: String,
    onPhoneChange: (String) -> Unit,
    phoneError: String,
    isRegisteredNumber: Boolean,
    username: String,
    onUsernameChange: (String) -> Unit,
    usernameError: String,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordError: String,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    confirmPasswordError: String,
    otp: String,
    onOtpChange: (String) -> Unit,
    otpError: String,
    isOtpSent: Boolean,
    onRegisterClick: () -> Unit
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
                value = phoneNumber,
                onValueChange = { 
                    phoneNumber = it
                    phoneError = ""
                    isRegisteredNumber = UserManager.isUserRegistered(it)
                },
                label = { Text(stringResource(R.string.member_phone)) },
                placeholder = { Text(stringResource(R.string.enter_phone_hint), color = DesignSystem.Colors.TextHint) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                enabled = !isOtpSent,
                isError = phoneError.isNotEmpty(),
                shape = DesignSystem.Shapes.Medium,
                colors = ComponentStyles.getInputFieldColors()
            )
            
            // Phone Error Message
            if (phoneError.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.invalid_phone),
                    color = DesignSystem.Colors.Error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = DesignSystem.Spacing.sm)
                )
            }
            
            // Username Field
            OutlinedTextField(
                value = username,
                onValueChange = onUsernameChange,
                label = { Text(stringResource(R.string.username)) },
                placeholder = { Text(stringResource(R.string.enter_username_hint), color = DesignSystem.Colors.TextHint) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                singleLine = true,
                enabled = !isOtpSent,
                isError = usernameError.isNotEmpty(),
                shape = DesignSystem.Shapes.Medium,
                colors = ComponentStyles.getInputFieldColors()
            )
            
            if (usernameError.isNotEmpty()) {
                Text(
                    text = usernameError,
                    color = DesignSystem.Colors.Error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = DesignSystem.Spacing.sm)
                )
            }
            
            // OTP Error Message
            if (otpError.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.invalid_otp),
                    color = DesignSystem.Colors.Error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = DesignSystem.Spacing.sm)
                )
            }
            
            // Password Fields (only for password registration)
            if (registrationMethod == RegistrationMethod.PASSWORD) {
                OutlinedTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = { Text(stringResource(R.string.password)) },
                    placeholder = { Text(stringResource(R.string.password_hint), color = DesignSystem.Colors.TextHint) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    enabled = !isOtpSent,
                    isError = passwordError.isNotEmpty(),
                    shape = DesignSystem.Shapes.Medium,
                    colors = ComponentStyles.getInputFieldColors()
                )
                
                // Password Error Message
                if (passwordError.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.passwords_not_match),
                        color = DesignSystem.Colors.Error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = DesignSystem.Spacing.sm)
                    )
                }
            }    
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = onConfirmPasswordChange,
                    label = { Text("ಪಾಸ್ವರ್ಡ್ ದೃಢೀಕರಿಸಿ") },
                    placeholder = { Text("ಪಾಸ್ವರ್ಡ್ ಮತ್ತೆ ಟೈಪ್ ಮಾಡಿ", color = DesignSystem.Colors.TextHint) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    isError = confirmPasswordError.isNotEmpty(),
                    shape = DesignSystem.Shapes.Medium,
                    colors = ComponentStyles.getInputFieldColors()
                )
                
                if (confirmPasswordError.isNotEmpty()) {
                    Text(
                        text = confirmPasswordError,
                        color = DesignSystem.Colors.Error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = DesignSystem.Spacing.sm)
            
            // OTP Field (only for OTP registration)
            if (registrationMethod == RegistrationMethod.OTP && isOtpSent) {
                OutlinedTextField(
                    value = otp,
                    onValueChange = onOtpChange,
                    label = { Text("OTP") },
                    placeholder = { Text("6-ಅಂಕಿಯ OTP", color = DesignSystem.Colors.TextHint) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = otpError.isNotEmpty(),
                    shape = DesignSystem.Shapes.Medium,
                    colors = ComponentStyles.getInputFieldColors()
                )
                
                if (otpError.isNotEmpty()) {
                    Text(
                        text = otpError,
                        color = DesignSystem.Colors.Error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = DesignSystem.Spacing.sm)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(DesignSystem.Spacing.sm))
            
            // Register Button
            Button(
                onClick = onRegisterClick,
                enabled = when (registrationMethod) {
                    RegistrationMethod.OTP -> {
                        if (!isOtpSent) {
                            phoneNumber.length == 13 && username.isNotEmpty()
                        } else {
                            otp.length == 6
                        }
                    }
                    RegistrationMethod.PASSWORD -> {
                        phoneNumber.isNotEmpty() && username.isNotEmpty() && 
                        password.isNotEmpty() && confirmPassword.isNotEmpty()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(DesignSystem.Buttons.Height),
                colors = ComponentStyles.getPrimaryButtonColors(),
                shape = DesignSystem.Shapes.Medium
            ) {
                Text(
                    text = when (registrationMethod) {
                        RegistrationMethod.OTP -> {
                            if (!isOtpSent) "OTP ಕಳುಹಿಸಿ" else "ನೋಂದಣಿ ಪೂರ್ಣಗೊಳಿಸಿ"
                        }
                        RegistrationMethod.PASSWORD -> "ನೋಂದಣಿ ಮಾಡಿ"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun LoginLink(onLoginSuccess: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "ಈಗಾಗಲೆ ಖಾತೆ ಇದೆಯೇ?",
            style = MaterialTheme.typography.bodyMedium,
            color = DesignSystem.Colors.TextSecondary
        )
        
        Spacer(modifier = Modifier.width(DesignSystem.Spacing.xs))
        
        TextButton(
            onClick = onLoginSuccess,
            modifier = Modifier.padding(horizontal = DesignSystem.Spacing.sm)
        ) {
            Text(
                text = "ಲಾಗಿನ್ ಮಾಡಿ",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = DesignSystem.Colors.Primary
            )
        }
    }
}

enum class RegistrationMethod {
    OTP,
    PASSWORD
}

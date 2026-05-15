package com.adarsh.mahilashaktiunnati.viewmodel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import com.adarsh.mahilashaktiunnati.BuildConfig
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private var verificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    private val _status = MutableStateFlow<AuthStatus>(AuthStatus.Idle)
    val status: StateFlow<AuthStatus> = _status.asStateFlow()

    init {
        if (BuildConfig.DEBUG) {
            auth.firebaseAuthSettings.forceRecaptchaFlowForTesting(true)
        }
    }

    sealed class AuthStatus {
        data object Idle : AuthStatus()
        data object SendingOtp : AuthStatus()
        data object OtpSent : AuthStatus()
        data object Verifying : AuthStatus()
        data object LoggedIn : AuthStatus()
        data class Error(val message: String) : AuthStatus()
    }

    fun sendOtp(phone: String, activity: Activity, forceResend: Boolean = false) {
        val normalized = phone.trim()
        
        if (!normalized.startsWith("+")) {
            _status.value = AuthStatus.Error("Country code required (e.g., +91)")
            return
        }

        _status.value = AuthStatus.SendingOtp
        Log.d("Auth", "Sending OTP to $normalized")
        
        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(normalized)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)

        if (forceResend && resendToken != null) {
            optionsBuilder.setForceResendingToken(resendToken!!)
        }

        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }

    fun verifyOtp(code: String) {
        val id = verificationId
        if (id == null) {
            _status.value = AuthStatus.Error("Session expired. Please click 'Send OTP' again.")
            return
        }
        
        if (code.length < 6) {
            _status.value = AuthStatus.Error("Enter 6-digit OTP")
            return
        }

        _status.value = AuthStatus.Verifying
        val credential = PhoneAuthProvider.getCredential(id, code.trim())
        signInWithPhoneAuthCredential(credential)
    }

    fun signOut() {
        auth.signOut()
        _status.value = AuthStatus.Idle
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _status.value = AuthStatus.LoggedIn
                } else {
                    val msg = task.exception?.localizedMessage ?: "Invalid OTP"
                    _status.value = AuthStatus.Error(msg)
                }
            }
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d("Auth", "Auto-verification successful")
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.e("Auth", "OTP Failed: ${e.message}")
            val message = when (e) {
                is FirebaseAuthInvalidCredentialsException -> "Invalid phone number format. Use +91 followed by 10 digits."
                is FirebaseTooManyRequestsException -> "OTP quota exceeded for now. Please wait and try again later."
                is FirebaseAuthMissingActivityForRecaptchaException -> "OTP verification needs an active screen. Please reopen this page and try again."
                is FirebaseAuthException -> when (e.errorCode) {
                    "ERROR_APP_NOT_AUTHORIZED" -> "App not authorized for Firebase Phone Auth. Add this app's SHA-1 and SHA-256 in Firebase Console, download the new google-services.json, then rebuild."
                    "ERROR_INVALID_APP_CREDENTIAL" -> "Firebase rejected this app credential. Add SHA-1/SHA-256 in Firebase Console and download the updated google-services.json."
                    "ERROR_SESSION_EXPIRED" -> "OTP session expired. Please send OTP again."
                    else -> e.localizedMessage ?: "Phone verification failed. Check Firebase Phone Auth setup."
                }
                else -> {
                    val rawMessage = e.message.orEmpty()
                    if (
                        rawMessage.contains("app-not-authorized", ignoreCase = true) ||
                        rawMessage.contains("not authorized", ignoreCase = true) ||
                        rawMessage.contains("invalid app credential", ignoreCase = true)
                    ) {
                        "App not authorized for Firebase Phone Auth. Add SHA-1/SHA-256 in Firebase Console, download the new google-services.json, then rebuild."
                    } else {
                        e.localizedMessage ?: "Verification failed. Check internet, Firebase Phone Auth, and SHA fingerprint setup."
                    }
                }
            }
            _status.value = AuthStatus.Error(message)
        }

        override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
            verificationId = id
            resendToken = token
            _status.value = AuthStatus.OtpSent
            Log.d("Auth", "OTP code sent")
        }
    }
}

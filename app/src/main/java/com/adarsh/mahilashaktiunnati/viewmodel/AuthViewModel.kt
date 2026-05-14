package com.adarsh.mahilashaktiunnati.viewmodel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
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
                is FirebaseAuthInvalidCredentialsException -> "Invalid phone number format."
                else -> {
                    if (e.message?.contains("app-not-authorized", ignoreCase = true) == true) {
                        "App not authorized. Ensure SHA-1/SHA-256 fingerprints are added in Firebase Console."
                    } else {
                        e.localizedMessage ?: "Verification failed. Check internet and Firebase settings."
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

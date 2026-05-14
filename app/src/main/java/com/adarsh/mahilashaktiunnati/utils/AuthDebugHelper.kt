package com.adarsh.mahilashaktiunnati.utils

import android.util.Log
import com.google.firebase.auth.FirebaseAuth

object AuthDebugHelper {
    
    fun checkFirebaseAuthSetup(): Boolean {
        return try {
            val auth = FirebaseAuth.getInstance()
            Log.d("AuthDebug", "Firebase Auth instance created successfully")
            Log.d("AuthDebug", "Current user: ${auth.currentUser?.uid ?: "None"}")
            
            // Check if phone authentication is available
            val phoneAuthAvailable = true // Firebase Auth includes phone auth by default
            Log.d("AuthDebug", "Phone authentication available: $phoneAuthAvailable")
            
            true
        } catch (e: Exception) {
            Log.e("AuthDebug", "Firebase Auth setup failed", e)
            false
        }
    }
    
    fun logAuthState() {
        val auth = FirebaseAuth.getInstance()
        Log.d("AuthDebug", "=== Auth State Check ===")
        Log.d("AuthDebug", "Current User: ${auth.currentUser?.uid ?: "Not logged in"}")
        Log.d("AuthDebug", "Is Anonymous: ${auth.currentUser?.isAnonymous ?: "N/A"}")
        Log.d("AuthDebug", "Providers: ${auth.currentUser?.providerData?.map { it.providerId } ?: emptyList()}")
    }
}

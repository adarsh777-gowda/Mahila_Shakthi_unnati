package com.adarsh.mahilashaktiunnati.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class User(
    val phoneNumber: String,
    val username: String,
    val password: String,
    val registeredAt: Long = System.currentTimeMillis()
)

object UserManager {
    private const val PREFS_NAME = "registered_users"
    private const val USERS_KEY = "users"

    private val registeredUsers = mutableListOf<User>()
    private var isLoaded = false
    private var appContext: Context? = null

    fun initialize(context: Context) {
        appContext = context.applicationContext
        loadUsers()
        initializeDemoUsers()
    }
    
    fun registerUser(user: User): Boolean {
        loadUsers()
        // Check if user already exists
        if (registeredUsers.any { it.phoneNumber == user.phoneNumber }) {
            return false
        }
        registeredUsers.add(user)
        saveUsers()
        return true
    }
    
    fun isUserRegistered(phoneNumber: String): Boolean {
        loadUsers()
        return registeredUsers.any { it.phoneNumber == phoneNumber }
    }
    
    fun authenticateUser(phoneNumber: String, password: String): Boolean {
        loadUsers()
        return registeredUsers.any { 
            it.phoneNumber == phoneNumber && it.password == password 
        }
    }
    
    fun getUser(phoneNumber: String): User? {
        loadUsers()
        return registeredUsers.find { it.phoneNumber == phoneNumber }
    }
    
    fun getAllRegisteredNumbers(): List<String> {
        loadUsers()
        return registeredUsers.map { it.phoneNumber }
    }
    
    // For demo purposes - add some default users
    fun initializeDemoUsers() {
        loadUsers()
        if (registeredUsers.isEmpty()) {
            registerUser(User("+919876543210", "demo1", "123456"))
            registerUser(User("+919876543211", "demo2", "123456"))
            registerUser(User("+919876543212", "demo3", "123456"))
        }
    }

    private fun loadUsers() {
        val context = appContext ?: return
        if (isLoaded) return

        val usersJson = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(USERS_KEY, null)
        registeredUsers.clear()

        if (!usersJson.isNullOrBlank()) {
            val usersArray = JSONArray(usersJson)
            for (index in 0 until usersArray.length()) {
                val userObject = usersArray.getJSONObject(index)
                registeredUsers.add(
                    User(
                        phoneNumber = userObject.getString("phoneNumber"),
                        username = userObject.getString("username"),
                        password = userObject.getString("password"),
                        registeredAt = userObject.optLong("registeredAt", System.currentTimeMillis())
                    )
                )
            }
        }

        isLoaded = true
    }

    private fun saveUsers() {
        val context = appContext ?: return
        val usersArray = JSONArray()

        registeredUsers.forEach { user ->
            usersArray.put(
                JSONObject()
                    .put("phoneNumber", user.phoneNumber)
                    .put("username", user.username)
                    .put("password", user.password)
                    .put("registeredAt", user.registeredAt)
            )
        }

        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(USERS_KEY, usersArray.toString())
            .apply()
    }
}

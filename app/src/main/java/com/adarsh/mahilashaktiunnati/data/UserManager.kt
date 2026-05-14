package com.adarsh.mahilashaktiunnati.data

data class User(
    val phoneNumber: String,
    val username: String,
    val password: String,
    val registeredAt: Long = System.currentTimeMillis()
)

object UserManager {
    private val registeredUsers = mutableListOf<User>()
    
    fun registerUser(user: User): Boolean {
        // Check if user already exists
        if (registeredUsers.any { it.phoneNumber == user.phoneNumber }) {
            return false
        }
        registeredUsers.add(user)
        return true
    }
    
    fun isUserRegistered(phoneNumber: String): Boolean {
        return registeredUsers.any { it.phoneNumber == phoneNumber }
    }
    
    fun authenticateUser(phoneNumber: String, password: String): Boolean {
        return registeredUsers.any { 
            it.phoneNumber == phoneNumber && it.password == password 
        }
    }
    
    fun getUser(phoneNumber: String): User? {
        return registeredUsers.find { it.phoneNumber == phoneNumber }
    }
    
    fun getAllRegisteredNumbers(): List<String> {
        return registeredUsers.map { it.phoneNumber }
    }
    
    // For demo purposes - add some default users
    fun initializeDemoUsers() {
        if (registeredUsers.isEmpty()) {
            registerUser(User("+919876543210", "demo1", "123456"))
            registerUser(User("+919876543211", "demo2", "123456"))
            registerUser(User("+919876543212", "demo3", "123456"))
        }
    }
}

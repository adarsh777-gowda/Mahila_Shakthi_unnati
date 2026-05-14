package com.adarsh.mahilashaktiunnati.utils

object ValidationUtils {
    
    fun validatePhoneNumber(phone: String): ValidationResult {
        val trimmed = phone.trim()
        
        when {
            trimmed.isEmpty() -> return ValidationResult(false, "Phone number is required")
            !trimmed.startsWith("+") -> return ValidationResult(false, "Phone number must include country code (e.g., +91)")
            trimmed.length < 8 -> return ValidationResult(false, "Phone number is too short")
            trimmed.length > 15 -> return ValidationResult(false, "Phone number is too long")
            !trimmed.substring(1).all { it.isDigit() } -> return ValidationResult(false, "Phone number contains invalid characters")
            else -> return ValidationResult(true)
        }
    }
    
    fun validateName(name: String): ValidationResult {
        val trimmed = name.trim()
        
        when {
            trimmed.isEmpty() -> return ValidationResult(false, "Name is required")
            trimmed.length < 2 -> return ValidationResult(false, "Name is too short")
            trimmed.length > 50 -> return ValidationResult(false, "Name is too long (max 50 characters)")
            !trimmed.all { it.isLetter() || it.isWhitespace() || it == '.' || it == '\'' } -> 
                return ValidationResult(false, "Name can only contain letters, spaces, dots, and apostrophes")
            else -> return ValidationResult(true)
        }
    }
    
    fun validateAmount(amount: String): ValidationResult {
        when {
            amount.isEmpty() -> return ValidationResult(false, "Amount is required")
            amount.toIntOrNull() == null -> return ValidationResult(false, "Invalid amount")
            amount.toInt() <= 0 -> return ValidationResult(false, "Amount must be greater than 0")
            amount.toInt() > 1000000 -> return ValidationResult(false, "Amount is too large (max 1,000,000)")
            else -> return ValidationResult(true)
        }
    }
    
    fun validateWeek(week: String): ValidationResult {
        val trimmed = week.trim()
        
        when {
            trimmed.isEmpty() -> return ValidationResult(false, "Week is required")
            trimmed.length > 20 -> return ValidationResult(false, "Week description is too long")
            else -> return ValidationResult(true)
        }
    }
    
    fun validateDate(date: String): ValidationResult {
        val trimmed = date.trim()
        
        when {
            trimmed.isEmpty() -> return ValidationResult(false, "Date is required")
            !isValidDateFormat(trimmed) -> return ValidationResult(false, "Invalid date format (use DD/MM/YYYY)")
            else -> return ValidationResult(true)
        }
    }
    
    fun validateOtp(otp: String): ValidationResult {
        val trimmed = otp.trim()
        
        when {
            trimmed.isEmpty() -> return ValidationResult(false, "OTP is required")
            trimmed.length < 4 -> return ValidationResult(false, "OTP is too short")
            trimmed.length > 6 -> return ValidationResult(false, "OTP is too long")
            !trimmed.all { it.isDigit() } -> return ValidationResult(false, "OTP can only contain digits")
            else -> return ValidationResult(true)
        }
    }
    
    private fun isValidDateFormat(date: String): Boolean {
        val regex = Regex("^\\d{2}/\\d{2}/\\d{4}$")
        if (!regex.matches(date)) return false
        
        val parts = date.split("/")
        if (parts.size != 3) return false
        
        return try {
            val day = parts[0].toInt()
            val month = parts[1].toInt()
            val year = parts[2].toInt()
            
            day in 1..31 && month in 1..12 && year in 2000..2100
        } catch (e: Exception) {
            false
        }
    }
}

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String = ""
)

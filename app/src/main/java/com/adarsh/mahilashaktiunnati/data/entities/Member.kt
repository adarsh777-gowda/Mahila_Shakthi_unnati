package com.adarsh.mahilashaktiunnati.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import androidx.room.ColumnInfo

@Entity(
    tableName = "members",
    indices = [
        Index(value = ["name"]),
        Index(value = ["phone"], unique = true),
        Index(value = ["user_id"])
    ]
)
data class Member(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    val name: String,
    
    val phone: String,
    
    @ColumnInfo(name = "photo_uri")
    val photoUri: String? = null,
    
    @ColumnInfo(name = "user_id")
    val userId: String = "",
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "join_date")
    val joinDate: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false,
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,
    
    val address: String? = null,
    
    @ColumnInfo(name = "aadhaar_number")
    val aadhaarNumber: String? = null,
    
    @ColumnInfo(name = "emergency_contact")
    val emergencyContact: String? = null
)

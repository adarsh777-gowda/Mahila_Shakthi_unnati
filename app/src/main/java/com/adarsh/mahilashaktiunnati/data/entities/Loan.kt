package com.adarsh.mahilashaktiunnati.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.ColumnInfo

@Entity(
    tableName = "loans",
    foreignKeys = [
        ForeignKey(
            entity = Member::class,
            parentColumns = ["id"],
            childColumns = ["member_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["member_id"]),
        Index(value = ["is_paid"]),
        Index(value = ["disbursement_date"]),
        Index(value = ["user_id"])
    ]
)
data class Loan(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "loan_id")
    val id: Int = 0,
    
    @ColumnInfo(name = "member_id")
    val memberId: Int,
    
    @ColumnInfo(name = "principal_amount")
    val principalAmount: Long,
    
    @ColumnInfo(name = "interest_rate")
    val interestRate: Double = 10.0, // Default 10% interest rate
    
    @ColumnInfo(name = "disbursement_date")
    val disbursementDate: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "due_date")
    val dueDate: Long,
    
    @ColumnInfo(name = "is_paid")
    val isPaid: Boolean = false,
    
    @ColumnInfo(name = "paid_amount")
    val paidAmount: Long = 0L,
    
    @ColumnInfo(name = "purpose")
    val purpose: String? = null,
    
    @ColumnInfo(name = "guarantor_name")
    val guarantorName: String? = null,
    
    @ColumnInfo(name = "guarantor_phone")
    val guarantorPhone: String? = null,

    @ColumnInfo(name = "user_id")
    val userId: String = "",

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false
)

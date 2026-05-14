package com.adarsh.mahilashaktiunnati.data.entities

import androidx.room.*

@Entity(
    tableName = "savings",
    foreignKeys = [
        ForeignKey(
            entity = Member::class,
            parentColumns = ["id"],
            childColumns = ["memberId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["memberId"])
    ]
)
data class Savings(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "memberId")
    val memberId: Int,

    @ColumnInfo(name = "amount")
    val amount: Double, // Matches Requirement 4.2 logic for totals

    @ColumnInfo(name = "week") // Matches Requirement 3.2 for weekly tracking
    val week: String = "",

    @ColumnInfo(name = "date")
    val date: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "status") // Requirement 3.2: Mark weekly "Paid" or "Pending"
    val status: String = "PENDING",

    @ColumnInfo(name = "userId") // Added for Firebase sync requirements
    val userId: String = "",

    @ColumnInfo(name = "createdAt")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updatedAt")
    val updatedAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "isDeleted")
    val isDeleted: Boolean = false
)
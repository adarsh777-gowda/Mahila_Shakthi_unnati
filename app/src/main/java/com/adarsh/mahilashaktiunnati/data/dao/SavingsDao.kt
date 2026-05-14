package com.adarsh.mahilashaktiunnati.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.adarsh.mahilashaktiunnati.data.entities.Savings

@Dao
interface SavingsDao {
    
    @Query("SELECT * FROM savings WHERE isDeleted = 0 ORDER BY date DESC")
    fun getAllSavings(): Flow<List<Savings>>
    
    @Query("SELECT * FROM savings WHERE memberId = :memberId AND isDeleted = 0 ORDER BY date DESC")
    fun getSavingsByMemberId(memberId: Int): Flow<List<Savings>>
    
    @Query("SELECT * FROM savings WHERE memberId = :memberId AND status = 'PAID' AND isDeleted = 0 ORDER BY date DESC")
    fun getPaidSavingsByMemberId(memberId: Int): Flow<List<Savings>>
    
    @Query("SELECT SUM(amount) FROM savings WHERE status = 'PAID' AND isDeleted = 0")
    fun getTotalPaidSavings(): Flow<Long?>
    
    @Query("SELECT SUM(amount) FROM savings WHERE memberId = :memberId AND status = 'PAID' AND isDeleted = 0")
    fun getTotalPaidSavingsByMemberId(memberId: Int): Flow<Long?>
    
    @Query("SELECT COUNT(*) FROM savings WHERE memberId = :memberId AND status = 'PAID' AND isDeleted = 0")
    fun getPaidSavingsCountByMemberId(memberId: Int): Flow<Int>
    
    @Query("SELECT * FROM savings WHERE id = :id AND isDeleted = 0")
    suspend fun getSavingsById(id: Int): Savings?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavings(savings: Savings): Long
    
    @Update
    suspend fun updateSavings(savings: Savings)
    
    @Query("UPDATE savings SET isDeleted = 1, updatedAt = :updatedAt WHERE id = :id")
    suspend fun softDeleteSavings(id: Int, updatedAt: Long)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMultipleSavings(savingsList: List<Savings>): List<Long>

    @Query("UPDATE savings SET status = :status WHERE id = :id")
    suspend fun updateSavingsStatus(id: Int, status: String)

    @Query("SELECT * FROM savings WHERE isDeleted = 0")
    suspend fun getAllSavingsOnce(): List<Savings>
}

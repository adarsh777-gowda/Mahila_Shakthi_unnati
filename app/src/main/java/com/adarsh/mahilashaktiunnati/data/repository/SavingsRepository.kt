package com.adarsh.mahilashaktiunnati.data.repository

import kotlinx.coroutines.flow.Flow
import com.adarsh.mahilashaktiunnati.data.entities.Savings
import com.adarsh.mahilashaktiunnati.data.dao.SavingsDao

class SavingsRepository(
    private val savingsDao: SavingsDao
) {
    fun savingsForMember(memberId: Int): Flow<List<Savings>> = savingsDao.getSavingsByMemberId(memberId)

    val totalSavings = savingsDao.getTotalPaidSavings()

    suspend fun getSavingOnce(id: Int): Savings? = savingsDao.getSavingsById(id)

    suspend fun insert(savings: Savings): Int = savingsDao.insertSavings(savings).toInt()

    suspend fun softDelete(id: Int, updatedAt: Long) = savingsDao.softDeleteSavings(id, updatedAt)

    suspend fun getAllOnce(): List<Savings> = savingsDao.getAllSavingsOnce()

    suspend fun upsertAll(savings: List<Savings>) = savingsDao.insertMultipleSavings(savings)
}


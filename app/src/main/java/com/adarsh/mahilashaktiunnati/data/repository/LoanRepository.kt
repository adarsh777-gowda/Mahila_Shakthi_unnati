package com.adarsh.mahilashaktiunnati.data.repository

import kotlinx.coroutines.flow.Flow
import com.adarsh.mahilashaktiunnati.data.entities.Loan
import com.adarsh.mahilashaktiunnati.data.dao.LoanDao

class LoanRepository(
    private val loanDao: LoanDao
) {
    fun loansForMember(memberId: Int): Flow<List<Loan>> = loanDao.getLoansByMemberId(memberId)

    val totalLoan = loanDao.getTotalActiveLoansAmount()

    suspend fun getLoanOnce(id: Int): Loan? = loanDao.getLoanById(id)

    suspend fun insert(loan: Loan): Int = loanDao.insertLoan(loan).toInt()

    suspend fun countPending(memberId: Int): Int = loanDao.getActiveLoanCountByMemberId(memberId)

    suspend fun markPaid(loanId: Int, updatedAt: Long) = loanDao.markLoanAsPaid(loanId)

    suspend fun softDelete(id: Int, updatedAt: Long) {
        val loan = loanDao.getLoanById(id) ?: return
        loanDao.deleteLoan(loan)
    }

    suspend fun getAllOnce(): List<Loan> = loanDao.getAllLoansOnce()

    suspend fun upsertAll(loans: List<Loan>) = loanDao.insertLoans(loans)
}


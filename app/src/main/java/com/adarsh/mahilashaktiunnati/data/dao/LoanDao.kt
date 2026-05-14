package com.adarsh.mahilashaktiunnati.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.adarsh.mahilashaktiunnati.data.entities.Loan

@Dao
interface LoanDao {
    
    @Query("SELECT * FROM loans ORDER BY disbursement_date DESC")
    fun getAllLoans(): Flow<List<Loan>>
    
    @Query("SELECT * FROM loans WHERE member_id = :memberId ORDER BY disbursement_date DESC")
    fun getLoansByMemberId(memberId: Int): Flow<List<Loan>>
    
    @Query("SELECT * FROM loans WHERE member_id = :memberId AND is_paid = 0 ORDER BY disbursement_date DESC")
    fun getActiveLoansByMemberId(memberId: Int): Flow<List<Loan>>
    
    @Query("SELECT * FROM loans WHERE is_paid = 0 ORDER BY due_date ASC")
    fun getAllActiveLoans(): Flow<List<Loan>>
    
    @Query("SELECT * FROM loans WHERE is_paid = 1 ORDER BY disbursement_date DESC")
    fun getAllPaidLoans(): Flow<List<Loan>>
    
    @Query("SELECT COUNT(*) FROM loans WHERE member_id = :memberId AND is_paid = 0")
    suspend fun getActiveLoanCountByMemberId(memberId: Int): Int
    
    @Query("SELECT EXISTS(SELECT 1 FROM loans WHERE member_id = :memberId AND is_paid = 0)")
    suspend fun hasActiveLoan(memberId: Int): Boolean
    
    @Query("SELECT SUM(principal_amount) FROM loans WHERE is_paid = 0")
    fun getTotalActiveLoansAmount(): Flow<Long?>
    
    @Query("SELECT SUM(principal_amount) FROM loans WHERE member_id = :memberId AND is_paid = 0")
    fun getActiveLoansAmountByMemberId(memberId: Int): Flow<Long?>
    
    @Query("SELECT * FROM loans WHERE due_date < :currentDate AND is_paid = 0")
    suspend fun getOverdueLoans(currentDate: Long): List<Loan>
    
    @Query("SELECT * FROM loans WHERE loan_id = :id")
    suspend fun getLoanById(id: Int): Loan?
    
    @Query("SELECT * FROM loans WHERE member_id = :memberId AND is_paid = 0 LIMIT 1")
    suspend fun getActiveLoanByMemberId(memberId: Int): Loan?
    
    @Insert
    suspend fun insertLoan(loan: Loan): Long
    
    @Update
    suspend fun updateLoan(loan: Loan)
    
    @Delete
    suspend fun deleteLoan(loan: Loan)
    
    @Query("DELETE FROM loans WHERE loan_id = :id")
    suspend fun deleteLoanById(id: Int)
    
    // Payment operations
    @Query("UPDATE loans SET is_paid = 1, paid_amount = principal_amount WHERE loan_id = :id")
    suspend fun markLoanAsPaid(id: Int)
    
    @Query("UPDATE loans SET paid_amount = paid_amount + :amount WHERE loan_id = :id")
    suspend fun updatePaidAmount(id: Int, amount: Long)
    
    @Query("UPDATE loans SET is_paid = 1, paid_amount = paid_amount + :amount WHERE loan_id = :id")
    suspend fun completeLoanPayment(id: Int, amount: Long)
    
    // Batch operations
    @Insert
    suspend fun insertLoans(loans: List<Loan>): List<Long>
    
    @Update
    suspend fun updateLoans(loans: List<Loan>)
    
    // Statistics
    @Query("SELECT COUNT(*) FROM loans WHERE is_paid = 0")
    fun getActiveLoansCount(): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM loans WHERE is_paid = 1")
    fun getPaidLoansCount(): Flow<Int>
    
    @Query("SELECT SUM(principal_amount) FROM loans")
    fun getTotalLoansAmount(): Flow<Long?>
    
    @Query("SELECT SUM(paid_amount) FROM loans WHERE is_paid = 1")
    fun getTotalPaidAmount(): Flow<Long?>

    @Query("SELECT * FROM loans")
    suspend fun getAllLoansOnce(): List<Loan>
}

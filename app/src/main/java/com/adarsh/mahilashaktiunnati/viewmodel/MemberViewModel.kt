package com.adarsh.mahilashaktiunnati.viewmodel

import android.content.Context
import android.net.Uri
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adarsh.mahilashaktiunnati.data.FirestoreSyncManager
import com.adarsh.mahilashaktiunnati.data.dao.LoanDao
import com.adarsh.mahilashaktiunnati.data.dao.MemberDao
import com.adarsh.mahilashaktiunnati.data.dao.SavingsDao
import com.adarsh.mahilashaktiunnati.data.entities.Loan
import com.adarsh.mahilashaktiunnati.data.entities.Member
import com.adarsh.mahilashaktiunnati.data.entities.Savings
import com.adarsh.mahilashaktiunnati.utils.DataExportImport
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MemberViewModel(
    private val memberDao: MemberDao,
    private val savingsDao: SavingsDao,
    private val loanDao: LoanDao,
    private val firestoreSyncManager: FirestoreSyncManager = FirestoreSyncManager()
) : ViewModel() {

    // Requirement: Instant Updates via Flow
    val members = memberDao.getAllMembers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalSavings = savingsDao.getTotalPaidSavings()
        .map { it ?: 0L }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val totalLoan = loanDao.getTotalActiveLoansAmount()
        .map { it ?: 0L }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    // Real-time Total Group Capital calculation
    val totalGroupCapital: StateFlow<Long> = savingsDao.getTotalPaidSavings()
        .map { amount -> amount ?: 0L }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    private val _syncStatus = MutableStateFlow<SyncStatus>(SyncStatus.Idle)
    val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()

    sealed class SyncStatus {
        data object Idle : SyncStatus()
        data object Syncing : SyncStatus()
        data object Success : SyncStatus()
        data class Error(val message: String) : SyncStatus()
    }

    fun consumeMessage() { _uiMessage.value = null }

    // Success Criteria: Prevent loan if unpaid exists
    fun addLoan(memberId: Int, amount: Long, disbursementDate: Long, dueDate: Long) {
        viewModelScope.launch {
            val hasActiveLoan = loanDao.hasActiveLoan(memberId)
            if (hasActiveLoan) {
                _uiMessage.value = "Cannot add loan: Member has an existing unpaid loan."
                return@launch
            }

            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            val currentTime = System.currentTimeMillis()
            loanDao.insertLoan(
                Loan(
                    memberId = memberId,
                    principalAmount = amount,
                    disbursementDate = disbursementDate,
                    dueDate = dueDate,
                    isPaid = false,
                    userId = userId,
                    createdAt = currentTime,
                    updatedAt = currentTime,
                    isDeleted = false
                )
            )
            _uiMessage.value = "Loan added successfully"
        }
    }

    // Success Criteria: WhatsApp Export (Clean Text)
    fun shareWhatsAppSummary(context: Context, member: Member) {
        viewModelScope.launch {
            val mSavingsFlow = savingsDao.getTotalPaidSavingsByMemberId(member.id)
            val mSavings = mSavingsFlow.firstOrNull() ?: 0L
            val summaryText = """
                *Mahila Shakti Unnati - Summary*
                Member: ${member.name}
                Total Savings: ₹$mSavings
                Generated via Digital Ledger
            """.trimIndent()

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, summaryText)
            }
            context.startActivity(Intent.createChooser(intent, "Share Report"))
        }
    }

    fun addSavings(
        memberId: Int,
        amount: Long,
        week: String,
        status: String,
        date: Long = System.currentTimeMillis()
    ) {
        viewModelScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            val currentTime = System.currentTimeMillis()
            savingsDao.insertSavings(
                Savings(
                    memberId = memberId, 
                    amount = amount.toDouble(), 
                    week = week,
                    date = date, 
                    status = status.uppercase(),
                    userId = userId, 
                    createdAt = currentTime, 
                    updatedAt = currentTime, 
                    isDeleted = false
                )
            )
            _uiMessage.value = "Savings added"
        }
    }

    // --- Sync & Helper Functions ---
    fun syncNow() {
        _syncStatus.value = SyncStatus.Syncing
        viewModelScope.launch {
            try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                val membersList = members.value.map { it.copy(userId = userId) }
                
                // Assuming sync manager will handle the entities
                firestoreSyncManager.syncMembersToFirestore(membersList)
                
                _syncStatus.value = SyncStatus.Success
                _uiMessage.value = "Cloud Sync Success"
            } catch (e: Exception) {
                _syncStatus.value = SyncStatus.Error(e.message ?: "Sync Error")
            }
        }
    }

    // Completed Import Logic
    suspend fun importLoansFromCsv(context: Context, uri: Uri): Result<Int> {
        return try {
            val result = DataExportImport.importLoansFromCsv(context, uri)
            if (result.isSuccess) {
                val data = result.getOrNull() ?: emptyList()
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                data.forEach { loanImport ->
                    // Manual mapping to Loan entity if needed
                    val loan = Loan(
                        memberId = loanImport.memberId,
                        principalAmount = loanImport.amount.toLong(),
                        dueDate = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000), // Default 30 days
                        userId = userId,
                        createdAt = System.currentTimeMillis()
                    )
                    loanDao.insertLoan(loan)
                }
                _uiMessage.value = "Imported ${data.size} loans"
                Result.success(data.size)
            } else Result.failure(Exception("Import failed"))
        } catch (e: Exception) { Result.failure(e) }
    }
    
    fun markLoanPaid(loanId: Int) {
        viewModelScope.launch {
            try {
                loanDao.markLoanAsPaid(loanId)
                _uiMessage.value = "Loan marked as paid"
            } catch (e: Exception) {
                _uiMessage.value = "Error marking loan as paid: ${e.message}"
            }
        }
    }
    
    fun calculateInterest(amount: Long): Long {
        return (amount * 0.1).toLong() // 10% interest
    }
    
    fun calculateTotal(amount: Long): Long {
        return amount + calculateInterest(amount)
    }
    
    fun getMember(memberId: Int) = flow {
        emit(memberDao.getMemberByIdOnce(memberId))
    }
    fun getSavingsForMember(memberId: Int) = savingsDao.getSavingsByMemberId(memberId)
    fun getLoansForMember(memberId: Int) = loanDao.getLoansByMemberId(memberId)
    
    fun updateMember(member: Member) {
        viewModelScope.launch {
            try {
                memberDao.updateMember(member)
                _uiMessage.value = "Member updated successfully"
            } catch (e: Exception) {
                _uiMessage.value = "Error updating member: ${e.message}"
            }
        }
    }
    
    fun deleteMember(member: Member) {
        viewModelScope.launch {
            try {
                memberDao.deleteMember(member)
                _uiMessage.value = "Member deleted successfully"
            } catch (e: Exception) {
                _uiMessage.value = "Error deleting member: ${e.message}"
            }
        }
    }
    
    fun deleteSaving(saving: Savings) {
        viewModelScope.launch {
            try {
                savingsDao.softDeleteSavings(saving.id, System.currentTimeMillis())
                _uiMessage.value = "Savings deleted successfully"
            } catch (e: Exception) {
                _uiMessage.value = "Error deleting savings: ${e.message}"
            }
        }
    }
    
    fun deleteLoan(loan: Loan) {
        viewModelScope.launch {
            try {
                loanDao.deleteLoan(loan)
                _uiMessage.value = "Loan deleted successfully"
            } catch (e: Exception) {
                _uiMessage.value = "Error deleting loan: ${e.message}"
            }
        }
    }
    
    fun addMember(name: String, phone: String, photoUri: String? = null) {
        viewModelScope.launch {
            try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                val currentTime = System.currentTimeMillis()
                val member = Member(
                    name = name,
                    phone = phone,
                    photoUri = photoUri,
                    userId = userId,
                    createdAt = currentTime,
                    joinDate = currentTime,
                    isActive = true
                )
                memberDao.insertMember(member)
                _uiMessage.value = "Member added successfully"
            } catch (e: Exception) {
                _uiMessage.value = "Error adding member: ${e.message}"
            }
        }
    }
    
    fun syncFromFirestore() {
        viewModelScope.launch {
            try {
                _syncStatus.value = SyncStatus.Syncing
                val membersResult = firestoreSyncManager.fetchMembersFromFirestore()
                if (membersResult.isSuccess) {
                    val members = membersResult.getOrNull() ?: emptyList()
                    members.forEach { member ->
                        memberDao.insertMember(member)
                    }
                    _syncStatus.value = SyncStatus.Success
                    _uiMessage.value = "Sync completed successfully"
                } else {
                    _syncStatus.value = SyncStatus.Error("Failed to sync members")
                }
            } catch (e: Exception) {
                _syncStatus.value = SyncStatus.Error(e.message ?: "Sync error")
                _uiMessage.value = "Error syncing from Firestore: ${e.message}"
            }
        }
    }
}

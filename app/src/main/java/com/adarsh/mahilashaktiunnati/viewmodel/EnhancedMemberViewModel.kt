package com.adarsh.mahilashaktiunnati.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adarsh.mahilashaktiunnati.data.repository.MemberRepository
import com.adarsh.mahilashaktiunnati.data.entities.Member
import com.adarsh.mahilashaktiunnati.data.entities.Savings
import com.adarsh.mahilashaktiunnati.data.entities.Loan
import com.adarsh.mahilashaktiunnati.data.repository.RepositoryResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

data class MemberUiState(
    val members: List<Member> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val totalSavings: Long = 0L,
    val activeLoansCount: Int = 0,
    val searchQuery: String = ""
)

data class MemberDetailUiState(
    val member: Member? = null,
    val savingsEntries: List<Savings> = emptyList(),
    val loans: List<Loan> = emptyList(),
    val memberTotalSavings: Long = 0L,
    val hasActiveLoan: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

class EnhancedMemberViewModel(
    private val memberRepository: MemberRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MemberUiState())
    val uiState: StateFlow<MemberUiState> = _uiState.asStateFlow()
    
    private val _memberDetailState = MutableStateFlow(MemberDetailUiState())
    val memberDetailState: StateFlow<MemberDetailUiState> = _memberDetailState.asStateFlow()
    
    private val _selectedMemberId = MutableStateFlow<Int?>(null)
    
    init {
        observeMembers()
        observeTotalSavings()
        observeActiveLoansCount()
    }
    
    private fun loadMembers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            memberRepository.members.collect { members ->
                _uiState.value = _uiState.value.copy(
                    members = members,
                    isLoading = false,
                    error = null
                )
            }
        }
    }
    
    private fun observeTotalSavings() {
        viewModelScope.launch {
            memberRepository.totalPaidSavingsAmount.collect { totalSavings ->
                _uiState.value = _uiState.value.copy(totalSavings = totalSavings)
                _totalSavings.value = totalSavings
            }
        }
    }
    
    private fun observeActiveLoansCount() {
        viewModelScope.launch {
            memberRepository.activeLoansCount.collect { count ->
                _uiState.value = _uiState.value.copy(activeLoansCount = count)
                _totalLoan.value = count.toLong()
            }
        }
    }
    
    private fun observeMembers() {
        viewModelScope.launch {
            memberRepository.members.collect { members ->
                _uiState.value = _uiState.value.copy(members = members)
                _members.value = members
            }
        }
    }
    
    fun selectMember(memberId: Int) {
        _selectedMemberId.value = memberId
        loadMemberDetails(memberId)
    }
    
    private fun loadMemberDetails(memberId: Int) {
        viewModelScope.launch {
            _memberDetailState.value = _memberDetailState.value.copy(isLoading = true)
            
            try {
                val member = memberRepository.getMemberOnce(memberId)
                if (member != null) {
                    _memberDetailState.value = _memberDetailState.value.copy(
                        member = member,
                        isLoading = false,
                        error = null
                    )
                    
                    // Load savings and loans
                    memberRepository.getSavingsByMemberId(memberId).collect { savings ->
                        _memberDetailState.value = _memberDetailState.value.copy(savingsEntries = savings)
                    }
                    
                    memberRepository.getLoansByMemberId(memberId).collect { loans ->
                        _memberDetailState.value = _memberDetailState.value.copy(
                            loans = loans,
                            hasActiveLoan = loans.any { !it.isPaid }
                        )
                    }
                    
                    memberRepository.getTotalPaidSavingsByMemberId(memberId).collect { totalSavings ->
                        _memberDetailState.value = _memberDetailState.value.copy(memberTotalSavings = totalSavings)
                    }
                } else {
                    _memberDetailState.value = _memberDetailState.value.copy(
                        error = "Member not found",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _memberDetailState.value = _memberDetailState.value.copy(
                    error = "Failed to load member details: ${e.message}",
                    isLoading = false
                )
            }
        }
    }
    
    fun addMember(member: Member) {
        viewModelScope.launch {
            when (val result = memberRepository.insertMember(member)) {
                is RepositoryResult.Success -> {
                    // Member added successfully
                    _uiState.value = _uiState.value.copy(error = null)
                }
                is RepositoryResult.Error -> {
                    _uiState.value = _uiState.value.copy(error = result.message)
                }
            }
        }
    }
    
    fun addSavings(memberId: Int, amount: Long, date: Long = System.currentTimeMillis()) {
        viewModelScope.launch {
            val savings = Savings(
                memberId = memberId,
                amount = amount.toDouble(),
                date = date,
                status = "PENDING"
            )
            
            // Map the repository call correctly. The repository might be using 'addSavings' which might take a different type.
            // Let's ensure consistency.
            when (val result = memberRepository.addSavings(savings)) {
                is RepositoryResult.Success -> {
                    _uiState.value = _uiState.value.copy(error = null)
                    _memberDetailState.value = _memberDetailState.value.copy(error = null)
                }
                is RepositoryResult.Error -> {
                    _uiState.value = _uiState.value.copy(error = result.message)
                    _memberDetailState.value = _memberDetailState.value.copy(error = result.message)
                }
            }
        }
    }
    
    fun toggleSavingsStatus(savingsId: Int, currentStatus: String) {
        viewModelScope.launch {
            val newStatus = if (currentStatus == "PAID") "PENDING" else "PAID"
            
            when (val result = memberRepository.updateSavingsStatus(savingsId, newStatus)) {
                is RepositoryResult.Success -> {
                    // Status updated successfully
                    _memberDetailState.value = _memberDetailState.value.copy(error = null)
                }
                is RepositoryResult.Error -> {
                    _memberDetailState.value = _memberDetailState.value.copy(error = result.message)
                }
            }
        }
    }
    
    fun requestNewLoan(memberId: Int, principalAmount: Long, interestRate: Double = 10.0, dueDate: Long): RepositoryResult<Long> {
        val loan = Loan(
            memberId = memberId,
            principalAmount = principalAmount,
            interestRate = interestRate,
            dueDate = dueDate,
            purpose = "Personal Loan"
        )
        
        // Execute synchronously for immediate result
        return kotlinx.coroutines.runBlocking {
            memberRepository.requestNewLoan(memberId, loan)
        }
    }
    
    fun searchMembers(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        
        if (query.isBlank()) {
            loadMembers()
        } else {
            viewModelScope.launch {
                memberRepository.searchMembers(query).collect { members ->
                    _uiState.value = _uiState.value.copy(members = members)
                }
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
        _memberDetailState.value = _memberDetailState.value.copy(error = null)
    }
    
    // For compatibility with existing MemberViewModel interface
    private val _members = MutableStateFlow<List<Member>>(emptyList())
    val members: StateFlow<List<Member>> = _members.asStateFlow()
    
    private val _totalSavings = MutableStateFlow<Long>(0L)
    val totalSavings: StateFlow<Long> = _totalSavings.asStateFlow()
    
    private val _totalLoan = MutableStateFlow<Long>(0L)
    val totalLoan: StateFlow<Long> = _totalLoan.asStateFlow()
}

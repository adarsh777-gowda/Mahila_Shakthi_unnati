package com.adarsh.mahilashaktiunnati.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.adarsh.mahilashaktiunnati.data.dao.MemberDao
import com.adarsh.mahilashaktiunnati.data.dao.SavingsDao
import com.adarsh.mahilashaktiunnati.data.dao.LoanDao

class MemberViewModelFactory(
    private val memberDao: MemberDao,
    private val savingsDao: SavingsDao,
    private val loanDao: LoanDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MemberViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MemberViewModel(
                memberDao = memberDao,
                savingsDao = savingsDao,
                loanDao = loanDao
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

package com.adarsh.mahilashaktiunnati.di

import android.content.Context
import androidx.room.Room
import com.adarsh.mahilashaktiunnati.data.database.AppDatabase
import com.adarsh.mahilashaktiunnati.data.dao.MemberDao
import com.adarsh.mahilashaktiunnati.data.dao.SavingsDao
import com.adarsh.mahilashaktiunnati.data.dao.LoanDao
import com.adarsh.mahilashaktiunnati.data.repository.MemberRepository
import com.adarsh.mahilashaktiunnati.viewmodel.EnhancedMemberViewModel

// Simple dependency provider for projects not using Hilt
object DependencyProvider {
    
    private var database: AppDatabase? = null
    private var memberRepository: MemberRepository? = null
    private var enhancedMemberViewModel: EnhancedMemberViewModel? = null
    
    fun getDatabase(context: Context): AppDatabase {
        return database ?: AppDatabase.getDatabase(context).also { database = it }
    }
    
    fun getMemberRepository(context: Context): MemberRepository {
        return memberRepository ?: MemberRepository(
            getDatabase(context).memberDao(),
            getDatabase(context).savingsDao(),
            getDatabase(context).loanDao()
        ).also { memberRepository = it }
    }
    
    fun getEnhancedMemberViewModel(context: Context): EnhancedMemberViewModel {
        return enhancedMemberViewModel ?: EnhancedMemberViewModel(
            getMemberRepository(context)
        ).also { enhancedMemberViewModel = it }
    }
    
    // Clear dependencies for testing
    fun clearDependencies() {
        database = null
        memberRepository = null
        enhancedMemberViewModel = null
    }
}

package com.adarsh.mahilashaktiunnati.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.adarsh.mahilashaktiunnati.data.entities.Member
import com.adarsh.mahilashaktiunnati.data.relations.MemberWithSavingsAndLoans
import com.adarsh.mahilashaktiunnati.data.relations.MemberWithSavings
import com.adarsh.mahilashaktiunnati.data.relations.MemberWithActiveLoan

@Dao
interface MemberDao {

    @Query("SELECT * FROM members ORDER BY name ASC")
    fun getAllMembers(): Flow<List<Member>>

    @Query("SELECT * FROM members WHERE is_active = 1 ORDER BY name ASC")
    fun getActiveMembers(): Flow<List<Member>>

    @Query("SELECT * FROM members WHERE id = :id")
    suspend fun getMemberByIdOnce(id: Int): Member?

    @Query("SELECT * FROM members WHERE phone = :phone")
    suspend fun getMemberByPhone(phone: String): Member?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: Member): Long

    @Update
    suspend fun updateMember(member: Member)

    @Delete
    suspend fun deleteMember(member: Member)

    @Transaction
    @Query("SELECT * FROM members WHERE id = :memberId")
    suspend fun getMemberWithSavingsAndLoans(memberId: Int): MemberWithSavingsAndLoans?

    @Transaction
    @Query("SELECT * FROM members WHERE id = :memberId")
    suspend fun getMemberWithSavings(memberId: Int): MemberWithSavings?

    @Transaction
    @Query("SELECT * FROM members WHERE id = :memberId")
    suspend fun getMemberWithActiveLoan(memberId: Int): MemberWithActiveLoan?

    @Transaction
    @Query("SELECT * FROM members")
    fun getAllMembersWithSavingsAndLoans(): Flow<List<MemberWithSavingsAndLoans>>

    @Query("SELECT COUNT(*) FROM loans WHERE member_id = :memberId AND is_paid = 0")
    fun getUnpaidLoansCount(memberId: Int): Flow<Int>

    @Query("SELECT COUNT(*) FROM loans WHERE member_id = :memberId AND is_paid = 0")
    suspend fun countPendingLoans(memberId: Int): Int

    @Query("SELECT EXISTS(SELECT 1 FROM members WHERE phone = :phone AND id != :excludeId)")
    suspend fun isPhoneExists(phone: String, excludeId: Int = 0): Boolean
    
    @Query("SELECT COUNT(*) FROM members WHERE is_active = 1")
    fun getActiveMembersCount(): Flow<Int>
}

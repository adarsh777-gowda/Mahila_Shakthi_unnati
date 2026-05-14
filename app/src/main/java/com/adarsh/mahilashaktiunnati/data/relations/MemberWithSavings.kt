package com.adarsh.mahilashaktiunnati.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.adarsh.mahilashaktiunnati.data.entities.Member
import com.adarsh.mahilashaktiunnati.data.entities.Savings
import com.adarsh.mahilashaktiunnati.data.entities.Loan

data class MemberWithSavings(
    @Embedded val member: Member,
    
    @Relation(
        parentColumn = "id",
        entityColumn = "memberId"
    )
    val savings: List<Savings>
)

data class MemberWithSavingsAndLoans(
    @Embedded val member: Member,
    
    @Relation(
        parentColumn = "id",
        entityColumn = "memberId"
    )
    val savings: List<Savings>,
    
    @Relation(
        parentColumn = "id",
        entityColumn = "member_id"
    )
    val loans: List<Loan>
)

data class MemberWithActiveLoan(
    @Embedded val member: Member,
    
    @Relation(
        parentColumn = "id",
        entityColumn = "member_id"
    )
    val activeLoans: List<Loan>
) {
    fun hasActiveLoan(): Boolean {
        return activeLoans.any { !it.isPaid }
    }
    
    fun getUnpaidLoan(): Loan? {
        return activeLoans.find { !it.isPaid }
    }
}

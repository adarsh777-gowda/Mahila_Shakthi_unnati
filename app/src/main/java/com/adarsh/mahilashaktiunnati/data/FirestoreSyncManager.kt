package com.adarsh.mahilashaktiunnati.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.launch
import com.adarsh.mahilashaktiunnati.data.entities.Member
import com.adarsh.mahilashaktiunnati.data.entities.Savings
import com.adarsh.mahilashaktiunnati.data.entities.Loan

class FirestoreSyncManager {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    companion object {
        private const val USERS_COLLECTION = "users"
        private const val MEMBERS_COLLECTION = "members"
        private const val SAVINGS_COLLECTION = "savings"
        private const val LOANS_COLLECTION = "loans"
    }
    
    private fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
    
    suspend fun syncMembersToFirestore(members: List<Member>): Result<Unit> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
            
            val snapshot = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(MEMBERS_COLLECTION)
                .get()
                .await()
                
            for (doc in snapshot.documents) {
                if (!members.any { it.id.toString() == doc.id }) {
                    doc.reference.delete().await()
                }
            }
            
            members.forEach { member ->
                val memberData = mapOf(
                    "name" to member.name,
                    "phone" to member.phone,
                    "photoUri" to member.photoUri,
                    "userId" to member.userId,
                    "createdAt" to member.createdAt,
                    "updatedAt" to member.joinDate,
                    "isDeleted" to member.isDeleted
                )
                
                firestore.collection(USERS_COLLECTION)
                    .document(userId)
                    .collection(MEMBERS_COLLECTION)
                    .document(member.id.toString())
                    .set(memberData)
                    .await()
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun syncSavingsToFirestore(savings: List<Savings>): Result<Unit> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
            
            val snapshot = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(SAVINGS_COLLECTION)
                .get()
                .await()
                
            for (doc in snapshot.documents) {
                if (!savings.any { it.id.toString() == doc.id }) {
                    doc.reference.delete().await()
                }
            }
            
            savings.forEach { saving ->
                val savingData = mapOf(
                    "memberId" to saving.memberId,
                    "amount" to saving.amount,
                    "date" to saving.date,
                    "status" to saving.status,
                    "createdAt" to saving.date,
                    "updatedAt" to saving.date,
                    "isDeleted" to saving.isDeleted
                )
                
                firestore.collection(USERS_COLLECTION)
                    .document(userId)
                    .collection(SAVINGS_COLLECTION)
                    .document(saving.id.toString())
                    .set(savingData)
                    .await()
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun syncLoansToFirestore(loans: List<Loan>): Result<Unit> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
            
            val snapshot = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(LOANS_COLLECTION)
                .get()
                .await()
                
            for (doc in snapshot.documents) {
                if (!loans.any { it.id.toString() == doc.id }) {
                    doc.reference.delete().await()
                }
            }
            
            loans.forEach { loan ->
                val loanData = mapOf(
                    "memberId" to loan.memberId,
                    "principalAmount" to loan.principalAmount,
                    "disbursementDate" to loan.disbursementDate,
                    "isPaid" to loan.isPaid,
                    "createdAt" to loan.disbursementDate,
                    "updatedAt" to loan.dueDate,
                    "isDeleted" to false
                )
                
                firestore.collection(USERS_COLLECTION)
                    .document(userId)
                    .collection(LOANS_COLLECTION)
                    .document(loan.id.toString())
                    .set(loanData)
                    .await()
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun fetchMembersFromFirestore(): Result<List<Member>> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
            
            val documents = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(MEMBERS_COLLECTION)
                .whereEqualTo("isDeleted", false)
                .get()
                .await()
            
            val members = documents.mapNotNull { doc ->
                try {
                    Member(
                        id = doc.id.toIntOrNull() ?: 0,
                        name = doc.getString("name") ?: "",
                        phone = doc.getString("phone") ?: "",
                        photoUri = doc.getString("photoUri"),
                        userId = doc.getString("userId") ?: "",
                        createdAt = doc.getLong("createdAt") ?: 0L,
                        joinDate = doc.getLong("updatedAt") ?: 0L,
                        isActive = doc.getBoolean("isDeleted") != true
                    )
                } catch (e: Exception) {
                    null
                }
            }
            
            Result.success(members)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun fetchSavingsFromFirestore(): Result<List<Savings>> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
            
            val documents = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(SAVINGS_COLLECTION)
                .whereEqualTo("isDeleted", false)
                .get()
                .await()
            
            val savings = documents.mapNotNull { doc ->
                try {
                    Savings(
                        id = doc.id.toIntOrNull() ?: 0,
                        memberId = doc.getLong("memberId")?.toInt() ?: 0,
                        amount = doc.getDouble("amount") ?: 0.0,
                        date = doc.getLong("date") ?: 0L,
                        status = doc.getString("status") ?: "",
                        isDeleted = doc.getBoolean("isDeleted") ?: false
                    )
                } catch (e: Exception) {
                    null
                }
            }
            
            Result.success(savings)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun fetchLoansFromFirestore(): Result<List<Loan>> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))
            
            val documents = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(LOANS_COLLECTION)
                .whereEqualTo("isDeleted", false)
                .get()
                .await()
            
            val loans = documents.mapNotNull { doc ->
                try {
                    Loan(
                        id = doc.id.toIntOrNull() ?: 0,
                        memberId = doc.getLong("memberId")?.toInt() ?: 0,
                        principalAmount = doc.getLong("principalAmount") ?: 0L,
                        disbursementDate = doc.getLong("disbursementDate") ?: 0L,
                        isPaid = doc.getBoolean("isPaid") ?: false,
                        dueDate = doc.getLong("updatedAt") ?: 0L
                    )
                } catch (e: Exception) {
                    null
                }
            }
            
            Result.success(loans)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun listenToMembersUpdates(): Flow<Result<List<Member>>> = callbackFlow {
        val userId = getCurrentUserId()
        if (userId == null) {
            trySend(Result.failure(Exception("User not logged in")))
            close()
            return@callbackFlow
        }
        
        val listenerRegistration = firestore.collection(USERS_COLLECTION)
            .document(userId)
            .collection(MEMBERS_COLLECTION)
            .whereEqualTo("isDeleted", false)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }
                
                val members = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Member(
                            id = doc.id.toIntOrNull() ?: 0,
                            name = doc.getString("name") ?: "",
                            phone = doc.getString("phone") ?: "",
                            photoUri = doc.getString("photoUri"),
                            userId = doc.getString("userId") ?: "",
                            createdAt = doc.getLong("createdAt") ?: 0L,
                            joinDate = doc.getLong("updatedAt") ?: 0L,
                            isActive = doc.getBoolean("isDeleted") != true
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                
                trySend(Result.success(members))
            }
            
        awaitClose { listenerRegistration.remove() }
    }
}

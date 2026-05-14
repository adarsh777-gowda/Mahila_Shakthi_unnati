package com.adarsh.mahilashaktiunnati.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context
import com.adarsh.mahilashaktiunnati.data.entities.Member
import com.adarsh.mahilashaktiunnati.data.entities.Savings
import com.adarsh.mahilashaktiunnati.data.entities.Loan
import com.adarsh.mahilashaktiunnati.data.dao.MemberDao
import com.adarsh.mahilashaktiunnati.data.dao.SavingsDao
import com.adarsh.mahilashaktiunnati.data.dao.LoanDao

@Database(
    entities = [Member::class, Savings::class, Loan::class],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun memberDao(): MemberDao
    abstract fun savingsDao(): SavingsDao
    abstract fun loanDao(): LoanDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mahila_shakti_database"
                )
                .addMigrations(
                    MIGRATION_1_2,
                    MIGRATION_2_3,
                    MIGRATION_3_4,
                    MIGRATION_4_5,
                    MIGRATION_5_6
                )
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }
        
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE members ADD COLUMN member_phone TEXT NOT NULL DEFAULT ''")
                database.execSQL("CREATE INDEX INDEX_members_member_phone ON members(member_phone)")
            }
        }
        
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE members ADD COLUMN emergency_contact TEXT")
                database.execSQL("ALTER TABLE members ADD COLUMN address TEXT")
                database.execSQL("ALTER TABLE members ADD COLUMN aadhaar_number TEXT")
                database.execSQL("CREATE INDEX INDEX_members_emergency_contact ON members(emergency_contact)")
                database.execSQL("CREATE INDEX INDEX_members_aadhaar_number ON members(aadhaar_number)")
            }
        }
        
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE loans ADD COLUMN loan_type TEXT NOT NULL DEFAULT 'PERSONAL'")
                database.execSQL("ALTER TABLE loans ADD COLUMN guarantor_name TEXT")
                database.execSQL("ALTER TABLE loans ADD COLUMN guarantor_phone TEXT")
                database.execSQL("ALTER TABLE loans ADD COLUMN guarantor_aadhaar TEXT")
                database.execSQL("ALTER TABLE loans ADD COLUMN loan_status TEXT NOT NULL DEFAULT 'ACTIVE'")
                database.execSQL("CREATE INDEX INDEX_loans_loan_type ON loans(loan_type)")
                database.execSQL("CREATE INDEX INDEX_loans_loan_status ON loans(loan_status)")
                database.execSQL("CREATE INDEX INDEX_loans_guarantor_phone ON loans(guarantor_phone)")
            }
        }
        
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE savings_entries ADD COLUMN frequency TEXT NOT NULL DEFAULT 'WEEKLY'")
                database.execSQL("ALTER TABLE savings_entries ADD COLUMN payment_method TEXT")
                database.execSQL("ALTER TABLE savings_entries ADD COLUMN payment_reference TEXT")
                database.execSQL("ALTER TABLE savings_entries ADD COLUMN collected_by TEXT")
                database.execSQL("CREATE INDEX INDEX_savings_entries_frequency ON savings_entries(frequency)")
                database.execSQL("CREATE INDEX INDEX_savings_entries_payment_method ON savings_entries(payment_method)")
                database.execSQL("CREATE INDEX INDEX_savings_entries_collected_by ON savings_entries(collected_by)")
            }
        }
        
        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create new savings table with updated schema
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS savings (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        memberId INTEGER NOT NULL,
                        amount REAL NOT NULL,
                        week TEXT NOT NULL DEFAULT '',
                        date INTEGER NOT NULL,
                        status TEXT NOT NULL DEFAULT 'PENDING',
                        userId TEXT NOT NULL DEFAULT '',
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL,
                        isDeleted INTEGER NOT NULL DEFAULT 0,
                        FOREIGN KEY(memberId) REFERENCES members(id) ON DELETE CASCADE
                    )
                """.trimIndent())
                
                // Copy data from old savings_entries table to new savings table
                database.execSQL("""
                    INSERT INTO savings (id, memberId, amount, week, date, status, userId, createdAt, updatedAt, isDeleted)
                    SELECT savings_id, member_id, amount, '', date, status, user_id, created_at, updated_at, 0
                    FROM savings_entries
                """.trimIndent())
                
                // Drop old table
                database.execSQL("DROP TABLE IF EXISTS savings_entries")
                
                // Create indexes for new table
                database.execSQL("CREATE INDEX INDEX_savings_memberId ON savings(memberId)")
                database.execSQL("CREATE INDEX INDEX_savings_status ON savings(status)")
                database.execSQL("CREATE INDEX INDEX_savings_memberId_status_date ON savings(memberId, status, date)")
            }
        }
        
        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                createOptimizedIndexes(db)
            }
            
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                db.execSQL("PRAGMA foreign_keys=ON")
                db.execSQL("PRAGMA journal_mode=WAL")
            }
            
            private fun createOptimizedIndexes(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE INDEX INDEX_members_name_active ON members(name, isActive)")
                db.execSQL("CREATE INDEX INDEX_savings_member_status_date ON savings(memberId, status, date)")
                db.execSQL("CREATE INDEX INDEX_loans_member_status ON loans(memberId, isPaid)")
                db.execSQL("CREATE INDEX INDEX_loans_due_date_status ON loans(dueDate, isPaid)")
            }
        }
    }
}

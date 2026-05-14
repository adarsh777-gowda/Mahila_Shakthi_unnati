package com.adarsh.mahilashaktiunnati

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.adarsh.mahilashaktiunnati.data.database.AppDatabase
import com.adarsh.mahilashaktiunnati.ui.navigation.AppNavGraph
import com.adarsh.mahilashaktiunnati.ui.theme.MahilaShaktiUnnatiTheme
import com.adarsh.mahilashaktiunnati.viewmodel.AuthViewModel
import com.adarsh.mahilashaktiunnati.viewmodel.MemberViewModel
import com.adarsh.mahilashaktiunnati.viewmodel.MemberViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Migrations match the Success Criteria for data integrity
        val migration2to3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE members ADD COLUMN userId TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE members ADD COLUMN createdAt INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE members ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0")

                db.execSQL("ALTER TABLE savings ADD COLUMN userId TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE savings ADD COLUMN createdAt INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE savings ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0")

                db.execSQL("ALTER TABLE loans ADD COLUMN userId TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE loans ADD COLUMN createdAt INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE loans ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0")
            }
        }

        val migration3to4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE members ADD COLUMN photoUri TEXT")
                db.execSQL("ALTER TABLE members ADD COLUMN isDeleted INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE savings ADD COLUMN isDeleted INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE loans ADD COLUMN isDeleted INTEGER NOT NULL DEFAULT 0")
            }
        }

        // Initialize Database
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "mahila-db"
        ).addMigrations(migration2to3, migration3to4)
            .fallbackToDestructiveMigration()
            .build()

        val memberDao = db.memberDao()
        val savingsDao = db.savingsDao()
        val loanDao = db.loanDao()

        setContent {
            val auth = FirebaseAuth.getInstance()
            var currentUser by remember { mutableStateOf(auth.currentUser) }

            DisposableEffect(auth) {
                val listener = FirebaseAuth.AuthStateListener {
                    currentUser = it.currentUser
                }
                auth.addAuthStateListener(listener)
                onDispose { auth.removeAuthStateListener(listener) }
            }

            val isLoggedIn = currentUser != null
            val authViewModel: AuthViewModel = viewModel()

            // Fixed ViewModel Factory initialization
            val memberViewModel: MemberViewModel = viewModel(
                factory = MemberViewModelFactory(memberDao, savingsDao, loanDao)
            )

            MahilaShaktiUnnatiTheme {
                AppNavGraph(
                    isLoggedIn = isLoggedIn,
                    authViewModel = authViewModel,
                    memberViewModel = memberViewModel,
                    onLogout = { auth.signOut() }
                )
            }
        }
    }
}
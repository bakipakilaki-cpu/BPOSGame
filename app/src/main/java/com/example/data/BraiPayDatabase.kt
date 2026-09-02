package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Product::class, TransactionHistory::class, GameNfcCard::class, JobItem::class], version = 12, exportSchema = false)
abstract class BraiPayDatabase : RoomDatabase() {
    abstract fun braiPayDao(): BraiPayDao

    companion object {
        @Volatile
        private var INSTANCE: BraiPayDatabase? = null

        fun getDatabase(context: Context): BraiPayDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BraiPayDatabase::class.java,
                    "braipay_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

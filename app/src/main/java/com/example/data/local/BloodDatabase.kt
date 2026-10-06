package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.ActivityLog
import com.example.data.model.BloodBank
import com.example.data.model.BloodStock
import com.example.data.model.Donor
import com.example.data.model.EmergencyRequest

@Database(
    entities = [
        BloodBank::class,
        BloodStock::class,
        Donor::class,
        EmergencyRequest::class,
        ActivityLog::class
    ],
    version = 1,
    exportSchema = false
)
abstract class BloodDatabase : RoomDatabase() {
    abstract fun bloodBankDao(): BloodBankDao
    abstract fun bloodStockDao(): BloodStockDao
    abstract fun donorDao(): DonorDao
    abstract fun emergencyRequestDao(): EmergencyRequestDao
    abstract fun activityLogDao(): ActivityLogDao

    companion object {
        @Volatile
        private var INSTANCE: BloodDatabase? = null

        fun getInstance(context: Context): BloodDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BloodDatabase::class.java,
                    "blood_bridge_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

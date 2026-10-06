package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ActivityLog
import com.example.data.model.BloodBank
import com.example.data.model.BloodStock
import com.example.data.model.Donor
import com.example.data.model.EmergencyRequest
import kotlinx.coroutines.flow.Flow

@Dao
interface BloodBankDao {
    @Query("SELECT * FROM blood_banks ORDER BY name ASC")
    fun getAllBloodBanks(): Flow<List<BloodBank>>

    @Query("SELECT * FROM blood_banks WHERE city = :city ORDER BY name ASC")
    fun getBloodBanksByCity(city: String): Flow<List<BloodBank>>

    @Query("SELECT * FROM blood_banks WHERE id = :id LIMIT 1")
    suspend fun getBloodBankById(id: Long): BloodBank?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(banks: List<BloodBank>)

    @Query("SELECT COUNT(*) FROM blood_banks")
    suspend fun getCount(): Int
}

@Dao
interface BloodStockDao {
    @Query("SELECT * FROM blood_stock")
    fun getAllStocks(): Flow<List<BloodStock>>

    @Query("SELECT * FROM blood_stock WHERE bloodBankId = :bankId")
    fun getStocksForBank(bankId: Long): Flow<List<BloodStock>>

    @Query("SELECT * FROM blood_stock WHERE bloodGroup = :bloodGroup")
    fun getStocksForBloodGroup(bloodGroup: String): Flow<List<BloodStock>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(stocks: List<BloodStock>)

    @Query("UPDATE blood_stock SET unitsAvailable = :units, lastUpdatedEpoch = :timestamp WHERE id = :stockId")
    suspend fun updateStockUnits(stockId: Long, units: Int, timestamp: Long)

    @Query("UPDATE blood_stock SET unitsAvailable = MAX(0, unitsAvailable + :delta), lastUpdatedEpoch = :timestamp WHERE bloodBankId = :bankId AND bloodGroup = :bloodGroup AND component = :component")
    suspend fun adjustUnits(bankId: Long, bloodGroup: String, component: String, delta: Int, timestamp: Long)
}

@Dao
interface DonorDao {
    @Query("SELECT * FROM donors ORDER BY id DESC")
    fun getAllDonors(): Flow<List<Donor>>

    @Query("SELECT * FROM donors WHERE bloodGroup = :bloodGroup ORDER BY id DESC")
    fun getDonorsByBloodGroup(bloodGroup: String): Flow<List<Donor>>

    @Query("SELECT * FROM donors WHERE id = :id LIMIT 1")
    suspend fun getDonorById(id: Long): Donor?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonor(donor: Donor): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(donors: List<Donor>)

    @Update
    suspend fun updateDonor(donor: Donor)

    @Query("UPDATE donors SET accountStatus = :status WHERE id = :id")
    suspend fun updateAccountStatus(id: Long, status: String)

    @Query("UPDATE donors SET verifiedByAdmin = :verified WHERE id = :id")
    suspend fun updateVerification(id: Long, verified: Boolean)

    @Query("SELECT COUNT(*) FROM donors")
    suspend fun getCount(): Int
}

@Dao
interface EmergencyRequestDao {
    @Query("SELECT * FROM emergency_requests ORDER BY createdAtEpoch DESC")
    fun getAllRequests(): Flow<List<EmergencyRequest>>

    @Query("SELECT * FROM emergency_requests WHERE status = 'OPEN' ORDER BY createdAtEpoch DESC")
    fun getOpenRequests(): Flow<List<EmergencyRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: EmergencyRequest): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(requests: List<EmergencyRequest>)

    @Update
    suspend fun updateRequest(request: EmergencyRequest)

    @Query("UPDATE emergency_requests SET status = :status, acceptedDonorCode = :donorCode WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String, donorCode: String?)
}

@Dao
interface ActivityLogDao {
    @Query("SELECT * FROM activity_logs ORDER BY timestampEpoch DESC LIMIT 50")
    fun getRecentLogs(): Flow<List<ActivityLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ActivityLog)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(logs: List<ActivityLog>)
}

package com.echo.app.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import com.echo.app.data.models.AppNotificationItem
import com.echo.app.data.models.ClashItem
import com.echo.app.data.models.EchoPost
import com.echo.app.data.models.EchoUserProfile
import com.echo.app.data.models.ReverbItem
import com.echo.app.data.models.TerminalSettings
import com.echo.app.data.models.WaveItem
import com.echo.app.data.models.WhisperItem
import kotlinx.coroutines.flow.Flow

@Dao
interface EchoDao {
    // Waves
    @Query("SELECT * FROM waves ORDER BY createdAt DESC")
    fun getAllWaves(): Flow<List<WaveItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWave(wave: WaveItem)

    @Update
    suspend fun updateWave(wave: WaveItem)

    @Query("DELETE FROM waves WHERE id = :waveId")
    suspend fun deleteWave(waveId: String)

    // Echoes
    @Query("SELECT * FROM echoes ORDER BY createdAt DESC")
    fun getAllEchoes(): Flow<List<EchoPost>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEcho(echo: EchoPost)

    @Update
    suspend fun updateEcho(echo: EchoPost)

    @Query("DELETE FROM echoes WHERE id = :postId")
    suspend fun deleteEcho(postId: String)

    // Reverbs
    @Query("SELECT * FROM reverbs ORDER BY createdAt ASC")
    fun getAllReverbs(): Flow<List<ReverbItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReverb(reverb: ReverbItem)

    @Update
    suspend fun updateReverb(reverb: ReverbItem)

    @Query("DELETE FROM reverbs WHERE id = :reverbId")
    suspend fun deleteReverb(reverbId: String)

    // Clashes
    @Query("SELECT * FROM clashes WHERE isActive = 1")
    fun getActiveClashes(): Flow<List<ClashItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClash(clash: ClashItem)

    @Update
    suspend fun updateClash(clash: ClashItem)

    // User Profile
    @Query("SELECT * FROM user_profile WHERE uid = :uid LIMIT 1")
    fun getUserProfile(uid: String): Flow<EchoUserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: EchoUserProfile)

    // Whispers
    @Query("SELECT * FROM whispers ORDER BY timestamp DESC")
    fun getAllWhispers(): Flow<List<WhisperItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWhisper(whisper: WhisperItem)

    @Query("DELETE FROM whispers WHERE id = :whisperId")
    suspend fun deleteWhisper(whisperId: String)

    // Notifications
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<AppNotificationItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: AppNotificationItem)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllNotificationsRead()

    @Query("DELETE FROM notifications WHERE id = :notificationId")
    suspend fun deleteNotification(notificationId: String)

    @Query("DELETE FROM notifications")
    suspend fun clearAllNotifications()

    // Settings
    @Query("SELECT * FROM terminal_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<TerminalSettings?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSettings(settings: TerminalSettings)
}

@Database(
    entities = [
        EchoPost::class,
        ReverbItem::class,
        WaveItem::class,
        ClashItem::class,
        EchoUserProfile::class,
        WhisperItem::class,
        AppNotificationItem::class,
        TerminalSettings::class
    ],
    version = 6,
    exportSchema = false
)
abstract class EchoDatabase : RoomDatabase() {
    abstract fun echoDao(): EchoDao
}

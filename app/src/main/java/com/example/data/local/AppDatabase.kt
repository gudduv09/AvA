package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.CommentDao
import com.example.data.local.dao.DownloadDao
import com.example.data.local.dao.DraftDao
import com.example.data.local.dao.MessageDao
import com.example.data.local.dao.ModerationDao
import com.example.data.local.dao.NotificationDao
import com.example.data.local.dao.SavedVideoDao
import com.example.data.local.dao.UserDao
import com.example.data.local.dao.VideoDao
import com.example.data.local.entity.BlockedUserEntity
import com.example.data.local.entity.CommentEntity
import com.example.data.local.entity.DownloadEntity
import com.example.data.local.entity.DraftEntity
import com.example.data.local.entity.MessageEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.ReportEntity
import com.example.data.local.entity.SavedVideoEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.VideoEntity

@Database(
    entities = [
        UserEntity::class,
        VideoEntity::class,
        CommentEntity::class,
        SavedVideoEntity::class,
        NotificationEntity::class,
        MessageEntity::class,
        DraftEntity::class,
        BlockedUserEntity::class,
        ReportEntity::class,
        DownloadEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun videoDao(): VideoDao
    abstract fun commentDao(): CommentDao
    abstract fun savedVideoDao(): SavedVideoDao
    abstract fun notificationDao(): NotificationDao
    abstract fun messageDao(): MessageDao
    abstract fun draftDao(): DraftDao
    abstract fun moderationDao(): ModerationDao
    abstract fun downloadDao(): DownloadDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ava_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}

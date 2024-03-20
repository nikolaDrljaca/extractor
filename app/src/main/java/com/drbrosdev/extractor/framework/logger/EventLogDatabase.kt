package com.drbrosdev.extractor.framework.logger

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.drbrosdev.extractor.data.DatabaseConverters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.time.LocalDateTime


@Database(
    entities = [EventEntity::class],
    version = 3
)
@TypeConverters(DatabaseConverters::class)
abstract class EventLogDatabase : RoomDatabase() {

    abstract fun eventLogDao(): EventLogDao

    companion object {
        fun createLogDatabase(context: Context): EventLogDatabase = Room.databaseBuilder(
            context,
            EventLogDatabase::class.java,
            "event_log_database"
        )
            .fallbackToDestructiveMigration()
            .addCallback(triggerCallback)
            .build()


        private val triggerCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                db.execSQL(
                    """
                    CREATE TRIGGER logTrimmer AFTER INSERT ON event_entity
                    BEGIN
                        DELETE FROM event_entity
                        WHERE event_order=(select min(event_order) from event_entity)
                            AND (select count(*) from event_entity)=100;
                    END;
                 """.trimIndent()
                )
            }
        }
    }
}

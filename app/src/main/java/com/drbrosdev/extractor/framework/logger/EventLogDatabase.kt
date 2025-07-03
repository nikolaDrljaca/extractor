package com.drbrosdev.extractor.framework.logger

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.drbrosdev.extractor.data.DatabaseConverters


@Database(
    entities = [EventRecord::class],
    version = 4
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
                    CREATE TRIGGER logTrimmer AFTER INSERT ON event
                    BEGIN
                        DELETE FROM event
                        WHERE event_order=(select min(event_order) from event)
                            AND (select count(*) from event)=101;
                    END;
                 """.trimIndent()
                )
            }
        }
    }
}

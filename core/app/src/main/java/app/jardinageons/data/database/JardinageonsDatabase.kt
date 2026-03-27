package app.jardinageons.data.database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import app.jardinageons.JardinageonsApplication
import app.jardinageons.data.dao.HarvestDao
import app.jardinageons.data.dao.SeedDao
import app.jardinageons.data.entities.HarvestEntity
import app.jardinageons.data.entities.SeedEntity
import java.lang.RuntimeException


const val JARDINAGEONS_DB_FILENAME = "JardinageonsDb"
@Database(entities = [SeedEntity::class, HarvestEntity::class],version=1)
abstract class JardinageonsDatabase : RoomDatabase() {
    abstract fun seedDao() : SeedDao
    abstract fun harvestDao() : HarvestDao

    companion object {
        private lateinit var application: JardinageonsApplication

        @Volatile
        private var instance: JardinageonsDatabase? = null

        fun getInstance(): JardinageonsDatabase {
            if (this::application.isInitialized) {
                if (instance == null) {
                    synchronized(this) {
                        if (instance == null)
                            instance = Room.databaseBuilder(
                                application.applicationContext,
                                JardinageonsDatabase::class.java,
                                JARDINAGEONS_DB_FILENAME
                            )
                                .build()
                    }
                }
                return instance!!
            } else {
                throw RuntimeException()
            }
        }
        @Synchronized
        fun initialize(app: JardinageonsApplication) {
            if (::application.isInitialized) {
                throw RuntimeException("Already initialized")
            }
            application = app
        }
    }
}





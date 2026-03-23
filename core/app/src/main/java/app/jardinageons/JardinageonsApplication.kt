package app.jardinageons

import android.app.Application
import app.jardinageons.data.database.JardinageonsDatabase

class JardinageonsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        JardinageonsDatabase.initialize(this)
    }
}
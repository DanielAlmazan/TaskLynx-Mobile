package edu.tasklynx.tasklynxmobile

import android.app.Application
import androidx.room.Room
import edu.tasklynx.tasklynxmobile.data.tasklynxDB
import edu.tasklynx.tasklynxmobile.utils.Preferences

class TaskLynxApplication: Application() {
    lateinit var tasksDB: tasklynxDB
        private set

    companion object {
        lateinit var preferences: Preferences
            private set
    }

    override fun onCreate() {
        super.onCreate()

        preferences = Preferences(applicationContext)

        tasksDB = Room.databaseBuilder(
            this,
            tasklynxDB::class.java,
            "tasks"
        ).build()
    }
}
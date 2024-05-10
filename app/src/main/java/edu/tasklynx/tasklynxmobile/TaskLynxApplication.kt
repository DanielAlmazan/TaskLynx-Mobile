package edu.tasklynx.tasklynxmobile

import android.app.Application
import androidx.room.Room
import edu.tasklynx.tasklynxmobile.data.tasklynxDB

class TaskLynxApplication: Application() {
    lateinit var tasksDB: tasklynxDB
        private set

    override fun onCreate() {
        super.onCreate()

        tasksDB = Room.databaseBuilder(
            this,
            tasklynxDB::class.java,
            "tasks"
        ).build()
    }
}
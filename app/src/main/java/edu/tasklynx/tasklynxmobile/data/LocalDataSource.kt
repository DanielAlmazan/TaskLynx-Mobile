package edu.tasklynx.tasklynxmobile.data

import edu.tasklynx.tasklynxmobile.models.Trabajo

class LocalDataSource (val db: tasklynxDB) {
    suspend fun insertTask(trabajo: Trabajo) {
        db.trabajoDao().insertTask(trabajo)
    }

    suspend fun getTasks(): List<Trabajo> {
        return db.trabajoDao().getTasks()
    }
}
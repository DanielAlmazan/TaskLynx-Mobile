package edu.tasklynx.tasklynxmobile.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import edu.tasklynx.tasklynxmobile.models.Trabajador

class TrabajadorConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromTrabajador(trabajador: Trabajador): String {
        return gson.toJson(trabajador)
    }

    @TypeConverter
    fun toTrabajador(trabajadorString: String): Trabajador {
        return gson.fromJson(trabajadorString, Trabajador::class.java)
    }
}

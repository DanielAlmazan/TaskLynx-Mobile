package edu.tasklynx.tasklynxmobile.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import edu.tasklynx.tasklynxmobile.models.Trabajador

class TrabajadorConverter {

    // Esta clase se usa para convertir el objeto trabajador en un string, y viceversa
    // Necesario para poder almacenar el objeto trabajador en la base de datos

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

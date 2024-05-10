package edu.tasklynx.tasklynxmobile.models


import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(tableName = "trabajador")
data class Trabajador(
    @SerializedName("apellidos")
    val apellidos: String,
    @SerializedName("contraseña")
    val contraseña: String,
    @SerializedName("dni")
    val dni: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("especialidad")
    val especialidad: String,
    @SerializedName("idTrabajador")
    val idTrabajador: String,
    @SerializedName("nombre")
    val nombre: String
)
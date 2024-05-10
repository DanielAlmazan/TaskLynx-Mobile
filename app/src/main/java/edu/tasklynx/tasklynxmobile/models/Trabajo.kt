package edu.tasklynx.tasklynxmobile.models


import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(tableName = "trabajo")
data class Trabajo(
    @SerializedName("categoria")
    val categoria: String,
    @SerializedName("codTrabajo")
    val codTrabajo: String,
    @SerializedName("descripcion")
    val descripcion: String,
    @SerializedName("fecFin")
    val fecFin: Any,
    @SerializedName("fecIni")
    val fecIni: String,
    @SerializedName("idTrabajador")
    val idTrabajador: Trabajador,
    @SerializedName("prioridad")
    val prioridad: Int,
    @SerializedName("tiempo")
    val tiempo: Int
)
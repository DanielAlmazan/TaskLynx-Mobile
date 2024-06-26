package edu.tasklynx.tasklynxmobile.models


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Trabajo(
    @SerializedName("categoria")
    val categoria: String,
    @SerializedName("codTrabajo")
    val codTrabajo: String,
    @SerializedName("descripcion")
    val descripcion: String,
    @SerializedName("fecFin")
    var fecFin: String?,
    @SerializedName("fecIni")
    val fecIni: String,
    @SerializedName("idTrabajador")
    val idTrabajador: Trabajador?,
    @SerializedName("prioridad")
    val prioridad: Int,
    @SerializedName("tiempo")
    var tiempo: Double?
) : Parcelable
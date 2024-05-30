package edu.tasklynx.tasklynxmobile.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "trabajo",
    foreignKeys = [
        ForeignKey(
            entity = Trabajador::class,
            parentColumns = ["idTrabajador"],
            childColumns = ["idTrabajador"],
            onDelete = ForeignKey.CASCADE
        )
    ])
data class TrabajoRoom(
    @SerializedName("categoria")
    val categoria: String,
    @SerializedName("codTrabajo")
    @PrimaryKey val codTrabajo: String,
    @SerializedName("descripcion")
    val descripcion: String,
    @SerializedName("fecFin")
    var fecFin: String?,
    @SerializedName("fecIni")
    val fecIni: String,
    @SerializedName("idTrabajador")
    val idTrabajador: String?,
    @SerializedName("prioridad")
    val prioridad: Int,
    @SerializedName("tiempo")
    var tiempo: Double?
)
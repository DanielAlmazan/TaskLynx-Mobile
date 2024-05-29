package edu.tasklynx.tasklynxmobile.data

import TrabajoListResponse
import TrabajoSingleResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

class TasklynxAPI {
    companion object {
        const val BASE_URL = "http://10.0.2.2:8080/api/"
        // No toqueis la url, es la que se usa para el emulador de android studio

        fun getRetrofit2Api(): TasklinkAPIInterface {
            return Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build()
                .create(TasklinkAPIInterface::class.java)
        }
    }

    interface TasklinkAPIInterface {

        // Get pending tasks for an employee by his id
        @GET("trabajadores/{id}/trabajos/pendientes")
        suspend fun getPendingTasksByEmployeeId(@Path("id") id: String): TrabajoListResponse

        // Finish a task by its id
        @PUT("trabajos/finalizar/{id}")
        fun finishTask(
            @Path("id") id: String,
            @Query("fec_fin") finishDate: String,
            @Query("tiempo") timeSpend: Int
        ): TrabajoSingleResponse

        // Get a task by its id
        @GET("trabajos/{id}")
        suspend fun getTaskById(@Path("id") id: String): TrabajoSingleResponse

        // Get pending jobs for an specific priority indicated by the user
        @GET("trabajadores/{id}/trabajos/pendientes/{prioridad}")
        suspend fun getPendingJobsByPriority(@Path("id") id: String, @Path("prioridad") prioridad: Int): TrabajoListResponse
    }
}
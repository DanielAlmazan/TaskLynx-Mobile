package edu.tasklynx.tasklynxmobile.data

import TrabajoResponse
import edu.tasklynx.tasklynxmobile.models.Trabajador
import edu.tasklynx.tasklynxmobile.models.Trabajo
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

class TasklynxAPI {
    companion object {
        const val BASE_URL = "http://10.0.2.2:8080/api/"

        fun getRetrofit2Api(): TasklinkAPIInterface {
            return Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build()
                .create(TasklinkAPIInterface::class.java)
        }
    }

    interface TasklinkAPIInterface {
        // Métodos

        // Get pending jobs for an employee by his id
        @GET("trabajadores/{id}/trabajos/pendientes")
        suspend fun getPendingJobsByEmployeeId(@Path("id") id: String): TrabajoResponse

        // Get completed jobs
        @GET("trabajadores/{id}/trabajos/completados")
        suspend fun getCompletedJobsByEmployeeId(@Path("id") id: String): TrabajoResponse

        @PUT("trabajos/finalizar/{id}")
        fun finishTask(@Path("id") id: String)
    }
}
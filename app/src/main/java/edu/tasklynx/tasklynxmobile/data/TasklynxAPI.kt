package edu.tasklynx.tasklynxmobile.data

import edu.tasklynx.tasklynxmobile.models.Trabajador
import edu.tasklynx.tasklynxmobile.models.Trabajo
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

class TasklynxAPI {
    companion object {
        const val BASE_URL = "http://localhost:8080/api/"

        fun getRetrofit2Api(): TasklinkAPIInterface {
            return Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build()
                .create(TasklinkAPIInterface::class.java)
        }
    }

    interface TasklinkAPIInterface {
        // Métodos

        // Get an employee by email and password
        @GET("trabajadores/{email}/{pass}")
        suspend fun getEmployeeByEmailAndPass(@Path("email") email: String, @Path("pass") pass: String): Trabajador

        // Get pending jobs for an employee by his id
        @GET("trabajadores/{id}/trabajos/pendientes")
        suspend fun getPendingJobsByEmployeeId(@Path("id") id: String): List<Trabajo>

        // Get completed jobs
        @GET("trabajadores/{id}/trabajos/completados")
        suspend fun getCompletedJobsByEmployeeId(@Path("id") id: String): List<Trabajo>

        // Get pending jobs for an specific employee ordered by priority
        @GET("trabajadores/{id}/trabajos/pendientes/prioridad")
        suspend fun getPendingJobsOrderedByPriority(@Path("id") id: String): List<Trabajo>

        // Get pending jobs for an specific by priority indicated by the user
        @GET("trabajadores/{id}/trabajos/pendientes/{prioridad}")
        suspend fun getPendingJobsByPriority(@Path("id") id: String, @Path("prioridad") prioridad: Int): List<Trabajo>

        @PUT("trabajos/finalizar/{id}")
        fun finishTask(@Path("id") id: String)
    }
}
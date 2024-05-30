package edu.tasklynx.tasklynxmobile.data

import TrabajoListResponse
import TrabajoSingleResponse
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

        fun getRetrofit2Api(): TaskLynxAPIInterface {
            return Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build()
                .create(TaskLynxAPIInterface::class.java)
        }
    }

    interface TaskLynxAPIInterface {
        // Get pending tasks by a logged employee
        @GET("trabajadores/{id}/{password}")
        suspend fun getPendingTasksByLoggedEmployee(
            @Path("id") id: String,
            @Path("password") password: String
        ): TrabajoListResponse

        // Finish a task by its id
        @PUT("trabajos/finalizar/{id}")
        fun finishTask(
            @Path("id") id: String,
            @Query("fec_fin") finishDate: String,
            @Query("tiempo") timeSpend: Int
        ): TrabajoSingleResponse
    }
}
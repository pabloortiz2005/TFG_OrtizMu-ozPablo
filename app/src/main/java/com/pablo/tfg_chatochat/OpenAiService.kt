
package com.pablo.tfg_chatochat

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenAiService {
    @Headers("Content-Type: application/json")
    @POST("v1/chat/completions")
    fun enviarMensaje(@Body request: OpenAiRequest): Call<OpenAiResponse>
}
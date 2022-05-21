package com.example.teacherassistant.data.remote

import com.example.teacherassistant.common.Constants.Companion.CONTENT_TYPE
import com.example.teacherassistant.common.Constants.Companion.SERVER_KEY
import com.example.teacherassistant.common.PushNotification
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface MessageApi {
    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(@Body notification: PushNotification): Response<ResponseBody>
}
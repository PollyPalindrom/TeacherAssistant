package com.example.teacherassistant.domain.repository

import com.example.teacherassistant.common.PushNotification
import okhttp3.ResponseBody
import retrofit2.Response

interface MessageRepository {

    suspend fun postNotification(notification: PushNotification): Response<ResponseBody>

}
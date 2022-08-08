package com.example.common_module.domain.repository

import com.example.common_module.common.PushNotification
import okhttp3.ResponseBody
import retrofit2.Response

interface MessageRepository {

    suspend fun postNotification(notification: PushNotification): Response<ResponseBody>

}
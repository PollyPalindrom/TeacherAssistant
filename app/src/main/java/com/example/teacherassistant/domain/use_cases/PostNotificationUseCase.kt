package com.example.teacherassistant.domain.use_cases

import com.example.teacherassistant.common.PushNotification
import com.example.teacherassistant.common.Resource
import com.example.teacherassistant.data.repository.Repository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class PostNotificationUseCase @Inject constructor(private val repository: Repository) {
    operator fun invoke(notification: PushNotification): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())
            val result = repository.postNotification(notification)
            if (result.isSuccessful) {
                emit(Resource.Success(Gson().toJson(result.body())))
                println(Gson().toJson(result.body()))
            } else {
                emit(Resource.Error("An unexpected error occurred"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(
                Resource.Error(
                    e.localizedMessage ?: "Couldn't reach server. Check your internet connection"
                )
            )
        }
    }
}
package com.m_and_a_company.canelatube.core.daison

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

open class DaisonApiCallImp : DaisonApiCall {
    override suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): DaisonResponseService<T> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiCall.invoke()
                if (response.isSuccessful) {
                    DaisonResponseService.Success(data = response.body()!!)
                } else {
                    val errorResponse = JSONObject(response.errorBody()?.string() ?: "{}")
                    DaisonResponseService.Error(
                        statusCode = response.code(),
                        message = errorResponse.optString("message")?: response.message(),
                        errors = null
                    )

                }
            } catch (e: HttpException) {
                DaisonResponseService.Error(
                    statusCode = 1,
                    message = e.message(),
                    errors = null
                )
            } catch (e: IOException) {
                DaisonResponseService.Error(
                    statusCode = DaisonUtil.STATUS_CODE_NO_INTERNET,
                    message = DaisonUtil.getMessageFromStatusCode(DaisonUtil.STATUS_CODE_NO_INTERNET),
                    errors = null
                )
            } catch (e: Exception) {
                DaisonResponseService.Error(
                    statusCode = e.hashCode(),
                    message = e.message ?: "Something went wrong",
                    errors = null
                )
            }
        }
    }


}
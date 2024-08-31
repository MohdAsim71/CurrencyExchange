package com.sample.exchange.core.network

import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

sealed class Result<out T : Any> {
    data class Success<T : Any>(val data: T) : Result<T>()
    data class ApiError<T : Any>(val code: Int, val message: String?) : Result<T>()
    data class ApiException<T : Any>(val e: Throwable) : Result<T>()
}

suspend fun <T : Any> handleApi(
    execute: suspend () -> Response<T>
): Result<T> {
    return try {
        val response = execute()
        val body = response.body()
        if (response.isSuccessful && body != null) {
            Result.Success(body)
        } else {
            Result.ApiError(code = response.code(), message = response.message())
        }
    } catch (e: HttpException) {
        val message = e.message().ifEmpty {
            when (e.code()) {
                400 -> "Bad Request: The server cannot process the request due to a client error."
                401 -> "Unauthorized: The request requires user authentication."
                403 -> "Forbidden: The server understood the request, but refuses to authorize it."
                404 -> "Not Found: The requested resource could not be found."
                405 -> "Method Not Allowed: The method specified in the request is not allowed for the resource."
                500 -> "Internal Server Error: The server encountered an unexpected condition that prevented it from fulfilling the request."
                501 -> "Not Implemented: The server does not support the functionality required to fulfill the request."
                502 -> "Bad Gateway: The server, while acting as a gateway or proxy, received an invalid response from the upstream server it accessed in attempting to fulfill the request."
                503 -> "Service Unavailable: The server is currently unavailable."
                504 -> "Gateway Timeout: The server, while acting as a gateway or proxy, did not receive a timely response from the upstream server specified by the URI."
                else -> "Unknown HTTP Error: An unknown HTTP error occurred with status code ${e.code()}."
            }
        }
        Result.ApiError(code = e.code(), message = message)
    } catch (e: IOException) {
        Result.ApiException(NoInternetException())
    } catch (e: Exception) {
        Result.ApiException(e)
    }
}

class NoInternetException(override val message: String = "No internet connection") :
    Exception()

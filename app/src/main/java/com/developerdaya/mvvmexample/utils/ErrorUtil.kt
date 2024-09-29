package com.developerdaya.mvvmexample.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.developerdaya.mvvmexample.R
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object ErrorUtil {
     fun getGsonInstance(): Gson {
        return GsonBuilder().create()
    }
    fun handleApiError(context: Context?, throwable: Throwable)
    {
        if (context == null) return
        when (throwable)
        {
            is ConnectException -> Toast.makeText(
                context,
                "Network Error PLease Try Later ",
                Toast.LENGTH_SHORT
            ).show()
            is SocketTimeoutException -> Toast.makeText(
                context,
                "Connection Lost PLease Try Later",
                Toast.LENGTH_SHORT
            ).show()
            is UnknownHostException, is InternalError -> Toast.makeText(
                context,
                "Server Error PLease Try Later",
                Toast.LENGTH_SHORT
            ).show()
            is HttpException -> {
                try {
                    when (throwable.code()) {
                        401 -> {
                            Toast.makeText(context, "Your session has expired. Please login again.", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            displayError(context, throwable)
                        }
                    }
                } catch (exception: Exception) {
                    Log.e("error", exception.toString())
                }
            }
            else -> {
                Toast.makeText(
                    context, "Something Went Wrong",
                    Toast.LENGTH_LONG
                ).show()
            }

        }
    }


    fun displayError(context: Context, exception: HttpException) {
        try {
            val errorBody = getGsonInstance().fromJson(
                exception.response()!!.errorBody()?.charStream(),
                ApiError::class.java
            )
            Toast.makeText(context, errorBody.message, Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Log.e("MyExceptions", e.message!!)
            Toast.makeText(
                context, "Something Went Wrong",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}


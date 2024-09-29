package com.developerdaya.mvvmexample.network

import com.developerdaya.mvvmexample.model.EmployeeList
import io.reactivex.Observable
import retrofit2.http.GET

interface ApiInterface {
    @GET("51508676-f32f-48d8-8742-f74c526ee62c")
    fun getEmployees(): Observable<EmployeeList>
}
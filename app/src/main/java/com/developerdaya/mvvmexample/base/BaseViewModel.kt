package com.developerdaya.mvvmexample.base

import androidx.lifecycle.ViewModel
import com.developerdaya.mvvmexample.network.ApiInterface
import com.developerdaya.mvvmexample.network.RetrofitUtil

abstract class BaseViewModel : ViewModel() {
    val api: ApiInterface by lazy {
        RetrofitUtil.createBaseApiService()
    }
}

package com.developerdaya.mvvmexample.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import com.developerdaya.mvvmexample.base.BaseViewModel
import com.developerdaya.mvvmexample.model.EmployeeList
import io.reactivex.disposables.Disposable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class EmployeesViewModel : BaseViewModel() {
    lateinit var disposable: Disposable
    val getEmployeesResp = MutableLiveData<EmployeeList>()
    var progressLoading = MutableLiveData<Boolean>()
    var errorMessage = MutableLiveData<Throwable>()
    var statusCode = MutableLiveData<String>("MVVM Example")

    fun getEmployees() {
        disposable = api.getEmployees()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                progressLoading.value = true
            }
            .doOnTerminate {
                progressLoading.value = false
            }
            .subscribe({
                getEmployeesResp.value = it
            }, {
                errorMessage.value = it
            })
    }


}

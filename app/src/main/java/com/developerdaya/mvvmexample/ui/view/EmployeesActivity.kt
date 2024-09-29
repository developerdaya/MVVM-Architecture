package com.developerdaya.mvvmexample.ui.view

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.developerdaya.mvvmexample.R
import com.developerdaya.mvvmexample.databinding.ActivityEmployeesBinding
import com.developerdaya.mvvmexample.ui.adapter.EmployeeAdapter
import com.developerdaya.mvvmexample.ui.viewmodel.EmployeesViewModel
import com.google.android.material.snackbar.Snackbar

class EmployeesActivity : AppCompatActivity() {
    private lateinit var employeeAdapter: EmployeeAdapter
    lateinit var binding: ActivityEmployeesBinding
    private val viewModel: EmployeesViewModel by viewModels()
    var TAG = "EmployeesActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployeesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        observer()

    }

    private fun initViews() {
        binding.viewModel  = EmployeesViewModel()
        binding.lifecycleOwner = this
        viewModel.getEmployees()
    }

    private fun observer() {
        viewModel.getEmployeesResp.observe(this) {
            Log.d(TAG, "observer: $it")
            binding.viewModel?.statusCode?.value ="MVVM Example : ${it.message}"
            binding.statusName.text = "MVVM Example : ${it.message}"
            Snackbar.make(binding.root, "Success : ${it.message}", Snackbar.LENGTH_LONG).show()
            employeeAdapter = EmployeeAdapter(it.employees)
           binding. recyclerView.adapter = employeeAdapter
        }

        viewModel.errorMessage.observe(this) {
            Snackbar.make(binding.root, it.localizedMessage, Snackbar.LENGTH_LONG).show()
        }
    }
}

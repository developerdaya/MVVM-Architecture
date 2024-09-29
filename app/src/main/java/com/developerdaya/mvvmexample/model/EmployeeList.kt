package com.developerdaya.mvvmexample.model

data class EmployeeList(var message:String,val employees:ArrayList<Employee>)
{
    data class Employee(
        val name: String,
        val profile: String
    )
}

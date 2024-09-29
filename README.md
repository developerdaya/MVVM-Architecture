# MVVM-Architecture
Implementing the MVVM (Model-View-ViewModel) architecture in an Android Kotlin application requires several components to work together. Below, I'll provide a comprehensive guide with the necessary code snippets to set up a simple application using the provided GET Restful API.

### 1. Dependencies

First, ensure that you have the necessary dependencies in your `build.gradle` file:

```groovy
  dependencies {
        implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
        implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")
        implementation ("com.squareup.retrofit2:retrofit:2.9.0")
        implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
        implementation ("com.squareup.retrofit2:adapter-rxjava2:2.9.0")
        implementation ("io.reactivex.rxjava2:rxjava:2.2.20")
        implementation ("io.reactivex.rxjava2:rxandroid:2.1.1")
        implementation ("com.squareup.okhttp3:logging-interceptor:4.9.3")
    }

```

### 2. Create API Interface

Create an API interface to define the API endpoints. Here's how you can do that:

```kotlin
package com.developerdaya.mvvmexample.network
import com.developerdaya.mvvmexample.model.EmployeeList
import io.reactivex.Observable
import retrofit2.http.GET
interface ApiInterface {
    @GET("51508676-f32f-48d8-8742-f74c526ee62c")
    fun getEmployees(): Observable<EmployeeList>
}
```

### 3. Create the Retrofit Instance

Create a Retrofit instance to manage network requests.

```kotlin
package com.developerdaya.mvvmexample.network
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
object RetrofitUtil {
    const val BASE_URL = "https://mocki.io/v1/"
    fun createBaseApiService(): ApiInterface {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(ApiInterface::class.java)
    }
}

```

### 4. Create the BaseViewModel

This class will handle common functionalities for all ViewModels.

```kotlin
package com.developerdaya.mvvmexample.base
import androidx.lifecycle.ViewModel
import com.developerdaya.mvvmexample.network.ApiInterface
import com.developerdaya.mvvmexample.network.RetrofitUtil
abstract class BaseViewModel : ViewModel() {
    val api: ApiInterface by lazy {
        RetrofitUtil.createBaseApiService()
    }
}

```

### 5. Create the ViewModel

Now, create your specific ViewModel that will extend `BaseViewModel`.

```kotlin
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

```

### 6. Create the Employee Data Class

Define the data model for an employee.

```kotlin
package com.developerdaya.mvvmexample.model
data class EmployeeList(var message:String,val employees:ArrayList<Employee>)
{
    data class Employee(
        val name: String,
        val profile: String
    )
}

```

### 7. Create the RecyclerView Adapter

Create an adapter for the RecyclerView to display the list of employees.

```kotlin
package com.developerdaya.mvvmexample.ui.adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.developerdaya.mvvmexample.databinding.ItemEmployeeBinding
import com.developerdaya.mvvmexample.model.EmployeeList
class EmployeeAdapter(val employees: ArrayList<EmployeeList.Employee>) :
    RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        var inflater = LayoutInflater.from(parent.context)
        val binding = ItemEmployeeBinding.inflate(inflater, parent, false)
        return EmployeeViewHolder(binding)
    }
    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        holder.bind(employees[position])
    }

    override fun getItemCount(): Int = employees.size
    class EmployeeViewHolder(var binding: ItemEmployeeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(employee: EmployeeList.Employee) {
            binding.textName.text = employee.name
            binding.textProfile.text = employee.profile
        }
    }
}

```

### 8. Create the RecyclerView Item Layout

Create an XML layout for the RecyclerView item (e.g., `item_employee.xml`).

```xml
<!-- item_employee.xml -->
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:background="#D0D0D0"
    android:padding="15dp"
    android:layout_marginTop="10dp"
    android:orientation="vertical">

    <TextView
        android:id="@+id/textName"
        android:text="Developer Daya"
        android:textSize="20sp"
        android:fontFamily="serif"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />

    <TextView
        android:id="@+id/textProfile"
        android:text="Android Developer"
        android:textSize="20sp"
        android:fontFamily="sans-serif-medium"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />
</LinearLayout>

```

### 9. Create the Activity/Fragment Layout

Create the layout for your Activity or Fragment (e.g., `activity_employees.xml`).

```xml
<layout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools">
    <data>
        <variable
            name="viewModel"
            type="com.developerdaya.mvvmexample.ui.viewmodel.EmployeesViewModel" />
    </data>

<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <TextView
        android:id="@+id/statusName"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@{viewModel.statusCode}"
        android:textSize="20sp"
        android:fontFamily="serif"
        android:layout_marginTop="16dp"
        android:layout_marginHorizontal="18dp"
        android:layout_gravity="center"
        />

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recyclerView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_marginBottom="50dp"
        android:layout_marginHorizontal="18dp"
        android:layout_marginTop="16dp"
        android:orientation="vertical"
        app:layoutManager="androidx.recyclerview.widget.LinearLayoutManager"
        app:layout_constraintTop_toBottomOf="@+id/categoryName"
        tools:listitem="@layout/item_employee"
        />
</LinearLayout>
</layout>
```

### 10. Setup the Activity/Fragment

In your Activity or Fragment, set up the RecyclerView, ViewModel, and data binding.

```kotlin
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

```

### 11. Error Handling Class (Optional)

You can create a simple error handling class if needed.

```kotlin
data class ApiError(
    val message: String
)
```

### 11.5 Handle Error
```
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

```



### 12. Enable Data Binding (Optional)

If you want to use Data Binding, ensure you have it enabled in your `build.gradle` file.

```
    dataBinding {
        enable = true
    }
      viewBinding {
        enable = true
    }


```

Yeh dependencies Android project mein use ki jaane wali libraries hain jo aapke application ke different functionalities ko enhance karte hain. Inke code snippets aur inke kaam ke baare mein yeh hai:

### 1. AndroidX Lifecycle Libraries
```groovy
implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")
```
- **`lifecycle-viewmodel-ktx`**: 
  - **Kaam**: Yeh ViewModel ko implement karne ke liye use hota hai, jo UI-related data ko manage karta hai. Isse aapko lifecycle-aware components create karne mein madad milti hai. KTX version ka matlab hai ki yeh Kotlin ke liye optimized hai, aur aapko coroutines aur extension functions ka support deta hai.
  - **Example**: 
    ```kotlin
    class MyViewModel : ViewModel() {
        val data: LiveData<String> = MutableLiveData("Hello, World!")
    }
    ```

- **`lifecycle-livedata-ktx`**: 
  - **Kaam**: Yeh LiveData class ko use karne ke liye hai, jo data holder hai aur aapke UI ko updates ki notification deta hai jab data change hota hai. Yeh lifecycle-aware hai, isliye aapko manually updates handle nahi karna padta.
  - **Example**:
    ```kotlin
    val liveData: MutableLiveData<String> = MutableLiveData()
    liveData.observe(lifecycleOwner) { value ->
        // UI update
    }
    ```

### 2. Retrofit
```groovy
implementation ("com.squareup.retrofit2:retrofit:2.9.0")
implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
implementation ("com.squareup.retrofit2:adapter-rxjava2:2.9.0")
```
- **`retrofit`**: 
  - **Kaam**: Yeh ek type-safe HTTP client hai jo RESTful web services se data fetch karne mein madad karta hai. Isse API calls ko simple aur organized tareeke se implement kiya ja sakta hai.
  - **Example**:
    ```kotlin
    interface ApiService {
        @GET("employees")
        fun getEmployees(): Call<EmployeeList>
    }
    ```

- **`converter-gson`**: 
  - **Kaam**: Yeh Retrofit ke saath Gson ko use karne ke liye hai. Iska matlab hai ki JSON responses ko automatically model objects mein convert kiya ja sakta hai.
  - **Example**: Iske bina, aapko manually JSON parsing karna padega.

- **`adapter-rxjava2`**: 
  - **Kaam**: Yeh Retrofit ko RxJava ke saath integrate karne ke liye hai, jisse aap API calls ko reactive programming ke pattern ke saath manage kar sakte hain.
  - **Example**:
    ```kotlin
    interface ApiService {
        @GET("employees")
        fun getEmployees(): Observable<EmployeeList>
    }
    ```

### 3. RxJava and RxAndroid
```groovy
implementation ("io.reactivex.rxjava2:rxjava:2.2.20")
implementation ("io.reactivex.rxjava2:rxandroid:2.1.1")
```
- **`rxjava`**: 
  - **Kaam**: Yeh reactive programming ke liye use hoti hai, jo asynchronous programming aur event-driven applications ko facilitate karta hai. Aap streams ke zariye data ko process kar sakte hain.
  - **Example**:
    ```kotlin
    val observable = Observable.just("Hello, World!")
    observable.subscribe { item -> println(item) }
    ```

- **`rxandroid`**: 
  - **Kaam**: Yeh RxJava ka Android-specific extension hai, jo Android ke main thread par work karne mein madad karta hai.
  - **Example**:
    ```kotlin
    val observable = Observable.just("Hello, World!")
    observable.observeOn(AndroidSchedulers.mainThread()).subscribe { item -> println(item) }
    ```

### 4. OkHttp Logging Interceptor
```groovy
implementation ("com.squareup.okhttp3:logging-interceptor:4.9.3")
```
- **`logging-interceptor`**: 
  - **Kaam**: Yeh OkHttp client ke requests aur responses ko log karne ke liye use hota hai. Yeh aapko debugging ke liye help karta hai, jisse aap API calls ki details dekh sakte hain.
  - **Example**:
    ```kotlin
    val logging = HttpLoggingInterceptor()
    logging.setLevel(HttpLoggingInterceptor.Level.BODY)
    
    val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()
    ```

### Happy Coding :)

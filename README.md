# MVVM-Architecture
Implementing the MVVM (Model-View-ViewModel) architecture in an Android Kotlin application requires several components to work together. Below, I'll provide a comprehensive guide with the necessary code snippets to set up a simple application using the provided GET Restful API.

### 1. Dependencies

First, ensure that you have the necessary dependencies in your `build.gradle` file:

```groovy
dependencies {
    implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1'
    implementation 'androidx.lifecycle:lifecycle-livedata-ktx:2.5.1'
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'io.reactivex.rxjava2:rxjava:2.2.20'
    implementation 'io.reactivex.rxjava2:rxandroid:2.1.1'
    implementation 'androidx.recyclerview:recyclerview:1.2.1'
    implementation 'com.google.android.material:material:1.4.0'
}
```

### 2. Create API Interface

Create an API interface to define the API endpoints. Here's how you can do that:

```kotlin
package com.consultantuser.webservice

import io.reactivex.Single
import retrofit2.http.GET

interface ApiInterface {
    @GET("your_endpoint_here") // Replace with your actual endpoint
    fun getEmployees(): Single<List<Employee>>
}
```

### 3. Create the Retrofit Instance

Create a Retrofit instance to manage network requests.

```kotlin
package com.consultantuser.webservice

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Retrofit {
    private const val BASE_URL = "https://mocki.io/v1/856e559e-1af2-41ff-93ea-02e3ef91ea55/" // Your base URL

    fun createBaseApiService(): ApiInterface {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)
    }
}
```

### 4. Create the BaseViewModel

This class will handle common functionalities for all ViewModels.

```kotlin
package com.basecode.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.consultantuser.webservice.ApiInterface
import com.consultantuser.webservice.Retrofit

abstract class BaseViewModel : ViewModel() {
    val throwable = MutableLiveData<Throwable>()
    val success = MutableLiveData<Any>()

    val api: ApiInterface by lazy {
        Retrofit.createBaseApiService()
    }

    fun onResponseError(it: Throwable?) {
        throwable.postValue(it)
    }
}
```

### 5. Create the ViewModel

Now, create your specific ViewModel that will extend `BaseViewModel`.

```kotlin
import com.basecode.base.BaseViewModel
import io.reactivex.disposables.Disposable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class EmployeesViewModel : BaseViewModel() {
    lateinit var disposable: Disposable
    val getEmployeesResp = MutableLiveData<List<Employee>>() // List of employees
    var progressLoading = MutableLiveData<Boolean>()
    var errorMessage = MutableLiveData<Throwable>()

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
data class Employee(
    val name: String,
    val profile: String
)
```

### 7. Create the RecyclerView Adapter

Create an adapter for the RecyclerView to display the list of employees.

```kotlin
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class EmployeeAdapter(private val employees: List<Employee>) : RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_employee, parent, false)
        return EmployeeViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        holder.bind(employees[position])
    }

    override fun getItemCount(): Int = employees.size

    class EmployeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(employee: Employee) {
            // Bind employee data to your views here
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
    android:orientation="vertical">

    <TextView
        android:id="@+id/textName"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />

    <TextView
        android:id="@+id/textProfile"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />
</LinearLayout>
```

### 9. Create the Activity/Fragment Layout

Create the layout for your Activity or Fragment (e.g., `activity_employees.xml`).

```xml
<!-- activity_employees.xml -->
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recyclerView"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
</LinearLayout>
```

### 10. Setup the Activity/Fragment

In your Activity or Fragment, set up the RecyclerView, ViewModel, and data binding.

```kotlin
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class EmployeesActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var employeeAdapter: EmployeeAdapter

    private val viewModel: EmployeesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employees)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.getEmployeesResp.observe(this) {
            employeeAdapter = EmployeeAdapter(it)
            recyclerView.adapter = employeeAdapter
        }

        viewModel.errorMessage.observe(this) {
            // Handle error
        }

        viewModel.getEmployees()
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

### 12. Enable Data Binding (Optional)

If you want to use Data Binding, ensure you have it enabled in your `build.gradle` file.

```groovy
android {
    ...
    buildFeatures {
        dataBinding true
    }
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

### Happy Coding : :)

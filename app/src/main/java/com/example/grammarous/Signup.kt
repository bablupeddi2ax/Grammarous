    package com.example.grammarous;

    import android.app.AlertDialog
    import android.content.BroadcastReceiver
    import android.content.Context
    import android.content.Intent
    import android.content.IntentFilter
    import android.content.SharedPreferences
    import android.os.Build
    import androidx.appcompat.app.AppCompatActivity;

    import android.os.Bundle;
    import android.os.Handler
    import android.os.Looper
    import android.util.Log
    import android.view.View
    import android.widget.AdapterView
    import android.widget.ArrayAdapter
    import android.widget.Button
    import android.widget.EditText
    import android.widget.ImageView
    import android.widget.Spinner
    import android.widget.TextView
    import android.widget.Toast
    import android.window.OnBackInvokedDispatcher
    import androidx.activity.OnBackPressedCallback
    import androidx.annotation.RequiresApi
    import com.example.grammarous.utils.TimeTracker
    import com.google.android.datatransport.Priority
    import com.google.firebase.FirebaseApp
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.database.DatabaseReference
    import com.google.firebase.database.FirebaseDatabase
    import com.google.firebase.messaging.FirebaseMessaging
    import kotlin.system.exitProcess

    class Signup : AppCompatActivity() {
       private lateinit var txtLogin : TextView
       private lateinit var btnSignup: Button
       private lateinit var edtName : EditText
       private lateinit var edtEmail:EditText
       private lateinit var edtPassword:EditText
       private lateinit var edtAge:EditText
       private lateinit var auth:FirebaseAuth
       private lateinit var dbRef:FirebaseDatabase
       private lateinit var usersRef:DatabaseReference
       private lateinit var sharedPrefs:SharedPreferences
       private lateinit var spinner: Spinner
        private lateinit var imgSideDisplay : ImageView
        private lateinit var adapter: ArrayAdapter<String>
        private  var role: Role? = null
        private val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == "com.example.grammarous.SHOW_SCREEN_TIME_ALERT") {
                    showAlertDialog()
                }
            }
        }

        private fun showAlertDialog() {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Screen Time Alert!")
            dialog.setMessage("You have spent 10 minutes of your time already. Please take a break")
            dialog.setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.dismiss()
                closeApp()
            }
            dialog.show()
        }

        private fun closeApp() {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            android.os.Process.killProcess(android.os.Process.myPid())
            exitProcess(0)
        }

        override fun onStart() {
            super.onStart()
            // Inside onCreate of your MainActivity or Application class
            FirebaseApp.initializeApp(this)

        }
        override fun onCreate( savedInstanceState:Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_signup)
            val filter = IntentFilter("com.example.grammarous.SHOW_SCREEN_TIME_ALERT")
            registerReceiver(broadcastReceiver, filter)
            val serviceIntent = Intent(this@Signup,TimeTracker::class.java)
            startService(serviceIntent)
            FirebaseApp.initializeApp(this)
            sharedPrefs= getSharedPreferences("userData", MODE_PRIVATE)
            if(sharedPrefs.getBoolean("isLoggedIn",false)){
                navigateToMain()
                return
            }
            val types = listOf("Child","Parent")
            txtLogin = findViewById(R.id.txtLogin)
            txtLogin.setOnClickListener {
                val intent = Intent(this@Signup,LoginActivity::class.java)
                startActivity(intent)
            }
            edtAge=findViewById(R.id.edtAge)
            edtName = findViewById(R.id.edtName)
            edtEmail   = findViewById(R.id.edtEmail)
            edtPassword = findViewById(R.id.edtPassword)
            btnSignup  = findViewById(R.id.btnSignup)
            spinner = findViewById(R.id.spinner)
            adapter = ArrayAdapter(this,R.layout.spinner_item_layout,R.id.txt_type,types)
            spinner.adapter = adapter
            imgSideDisplay=findViewById(R.id.imgSideDisplay)
            btnSignup.setOnClickListener{
                val name = edtName.text.trim().toString()
                val email = edtEmail.text.trim().toString()
                val password = edtPassword.text.trim().toString()
                val age = edtAge.text.trim().toString().toInt()
                signup(name,email,password,age)
            }

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    role = when (types[position]) {
                        "Child" -> Role.CHILD
                        "Parent" -> Role.PARENT
                        else -> Role.CHILD
                    }
                    when(types[position]){
                        "Child"-> imgSideDisplay.setImageResource(R.drawable.img_1)
                        "Parent"-> imgSideDisplay.setImageResource(R.drawable.profile)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Handle the case when nothing is selected.
                }
            }

        }

        private fun signup(name: String, email: String, password: String, age: Int) {
            auth = FirebaseAuth.getInstance()
            dbRef = FirebaseDatabase.getInstance()
            usersRef = dbRef.getReference("users")

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid.toString()
                    // get fcmToken for this guy
                        var user = getFcmToken(name,email,age)
//                    val user = User(name, email,userId,age, fcmToken =token )
                    with(sharedPrefs.edit()) {
                        putString("name",name)
                        putString("userId",userId)
                        putInt("age",age)
                        putBoolean("isLoggedIn", true)
                        putString("token",user.fcmToken)
                        apply()
                    }
                    Log.i("token",user.fcmToken.toString())
                    Log.i("user",user.toString())
                    usersRef.child(auth.currentUser?.uid!!).setValue(user)
                    val looper = Looper.getMainLooper()
                    val handler = Handler(looper)
                    handler.postDelayed({imgSideDisplay.animate().alpha(0f).translationXBy(2000f).start()},0)
                    handler.postDelayed({btnSignup.animate().alpha(0f).translationYBy(1500f).start()},3000).let {
                        val intent = Intent(this@Signup, MainActivity::class.java)
                        intent.putExtra("userId", userId.toString())
                        intent.putExtra("name", user.name.toString())
                        intent.putExtra("age", user.age.toString())
                        intent.putExtra("email", user.email.toString())
                        intent.putExtra("role", role)
                        startActivity(intent)
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(this@Signup, it.message?.toString(), Toast.LENGTH_SHORT).show()
            }
        }
        private fun navigateToMain() {
            // Directly navigate user to main activity
            val intent = Intent(this@Signup, MainActivity::class.java)
            startActivity(intent)
            finish() // Finish this activity to prevent the user from going back
        }

        override fun onDestroy() {
            super.onDestroy()
            unregisterReceiver(broadcastReceiver)
        }
        fun getFcmToken(name: String,email: String,age: Int):User{
            var user = User()
            FirebaseMessaging.getInstance().token.addOnSuccessListener { tk ->
                if (tk.isNotBlank() && !tk.isNullOrBlank()) {
                    // Store the FCM token in the database
                   user.fcmToken = tk
                }
            }.addOnFailureListener {
                Log.e("tokenerror", it.message.toString())  // Use Log.e for ERROR level
            }

            user.age = age
            user.email = email
            user.name = name
            return user
        }
        val dispatcher = onBackPressedDispatcher



        override fun getOnBackInvokedDispatcher(): OnBackInvokedDispatcher {
            dispatcher.addCallback(object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Your custom back button logic here
                    // Example: Show a confirmation dialog before exiting
                }
            })
            return super.getOnBackInvokedDispatcher()
        }


    }
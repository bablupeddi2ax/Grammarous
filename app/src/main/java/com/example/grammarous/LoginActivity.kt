package com.example.grammarous

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.messaging
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

class LoginActivity : AppCompatActivity() {
    private lateinit var spinner: Spinner
    private lateinit var adapter: ArrayAdapter<String>
    private  var role: Role? = null

    private lateinit var edtName : EditText
    private lateinit var edtPassword:EditText
    private lateinit var btnLogin: Button
//    private lateinit var txtSignup  : Button
    private lateinit var imgSideDisplay : ImageView
    private lateinit var forgotPassword : TextView
    private lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_activty)

        val types = listOf("Child","Parent")
        auth = FirebaseAuth.getInstance()
        spinner = findViewById(R.id.loginSpinner)
        edtName = findViewById(R.id.edtLoginName)
        edtPassword  = findViewById(R.id.edtLoginPass)
        btnLogin = findViewById(R.id.btnLogin)
        imgSideDisplay  = findViewById(R.id.imgSideDisplay)
        imgSideDisplay.setImageResource(R.drawable.profile)
        forgotPassword= findViewById(R.id.txtForgot)
        adapter = ArrayAdapter(this,R.layout.spinner_item_layout,R.id.txt_type,types)
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        role = Role.CHILD
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
//        forgotPassword.setOnClickListener(object : OnClickListener{
//            override fun onClick(p0: View?) {
//                Toast.makeText()
//            }
//        })


        btnLogin.setOnClickListener{
            val childName = edtName.text.toString()
            val password = edtPassword.text.toString()
            login(role!!,childName,password)
        }


    }
    fun checkToken(): Boolean {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val usersRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.uid)

            usersRef.child("fcmToken").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val fcmToken = dataSnapshot.getValue(String::class.java)
                    if (fcmToken.isNullOrEmpty()) {
                        // Token is missing or empty, generate a new one
                        getFcmToken(currentUser)
                    } else {
                        Log.d("FCM Token", "User has an FCM token: $fcmToken")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("FCM Token", "Error checking FCM token: ${databaseError.message}")
                }
            })
            return true
        }
        return false
    }

    fun getFcmToken(user: FirebaseUser) {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            if (!token.isNullOrBlank()) {
                // Update the token in the user's database record
                val usersRef = FirebaseDatabase.getInstance().getReference("users").child(user.uid)
                usersRef.child("fcmToken").setValue(token).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FCM Token", "FCM token updated successfully: $token")
                    } else {
                        Log.e("FCM Token", "Failed to update FCM token: ${task.exception?.message}")
                    }
                }
            }
        }.addOnFailureListener { exception ->
            Log.e("FCM Token", "Error retrieving FCM token: ${exception.message}")
        }
    }

    private fun login(role: Role, childName: String, password: String) {
                Firebase.messaging.subscribeToTopic("dailyWords").addOnSuccessListener {
                    Log.i("dailyyWords","done")
                }.addOnFailureListener {
                    Log.i("dailyyWords","failed")
                }
            auth.signInWithEmailAndPassword(childName,password).addOnCompleteListener {
                if(it.isSuccessful){
                    Toast.makeText(this@LoginActivity,"Login successful",Toast.LENGTH_SHORT).show()
//                    val intent  = Intent(this@LoginActivity,MainActivity::class.java)
//                    intent.putExtra("childName",childName)
//                    intent.putExtra("role",role.name)
//                    startActivity(intent)
                   checkToken()
                    val sharedPrefs =getSharedPreferences("userData", Context.MODE_PRIVATE)
                    sharedPrefs.edit().putBoolean("isLoggedIn",true).apply()
                    sharedPrefs.edit().putString("email",childName).apply()
                    startActivity(Intent(this@LoginActivity,MainActivity::class.java))
                }
            }.addOnFailureListener {
                Toast.makeText(this@LoginActivity,it.message?.toString(),Toast.LENGTH_SHORT).show()
            }
    }
}
package com.example.grammarous

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.grammarous.alphabets.LearnAlphabets
import com.example.grammarous.alphabets.ShakeAlphabets
import com.google.firebase.auth.FirebaseAuth

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
        imgSideDisplay.setImageResource(R.drawable.img_2)
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
                    "Parent"-> imgSideDisplay.setImageResource(R.drawable.img_2)
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

    private fun login(role: Role, childName: String, password: String) {
            auth.signInWithEmailAndPassword(childName,password).addOnCompleteListener {
                if(it.isSuccessful){
                    Toast.makeText(this@LoginActivity,"Login successful",Toast.LENGTH_SHORT).show()
//                    val intent  = Intent(this@LoginActivity,MainActivity::class.java)
//                    intent.putExtra("childName",childName)
//                    intent.putExtra("role",role.name)
//                    startActivity(intent)
                    startActivity(Intent(this@LoginActivity,MainActivity::class.java))
                }
            }.addOnFailureListener {
                Toast.makeText(this@LoginActivity,it.message?.toString(),Toast.LENGTH_SHORT).show()
            }
    }
}
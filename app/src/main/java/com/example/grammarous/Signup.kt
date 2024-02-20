    package com.example.grammarous;

    import android.content.Intent
    import android.content.SharedPreferences
    import androidx.appcompat.app.AppCompatActivity;

    import android.os.Bundle;
    import android.view.View
    import android.view.View.OnClickListener
    import android.widget.AdapterView
    import android.widget.ArrayAdapter
    import android.widget.Button
    import android.widget.EditText
    import android.widget.ImageView
    import android.widget.Spinner
    import android.widget.TextView
    import android.widget.Toast
    import com.example.grammarous.alphabets.ShakeAlphabets
    import com.google.firebase.FirebaseApp
    import com.google.firebase.app
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.database.DatabaseReference
    import com.google.firebase.database.FirebaseDatabase

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

        override fun onCreate( savedInstanceState:Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_signup)
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
                        "Parent"-> imgSideDisplay.setImageResource(R.drawable.img_2)
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
                    val user = User(name, email,userId,age)
                    with(sharedPrefs.edit()) {
                        putString("name",name)
                        putString("userId",userId)
                        putInt("age",age)
                        putBoolean("isLoggedIn", true)
                        apply()
                    }

                    usersRef.child(auth.currentUser?.uid!!).setValue(user)
                    val intent = Intent(this@Signup,MainActivity::class.java)
                    intent.putExtra("userId",userId.toString())
                    intent.putExtra("name",user.name.toString())
                    intent.putExtra("age",user.age.toString())
                    intent.putExtra("email",user.email.toString())
                    intent.putExtra("role",role)
                    startActivity(intent)
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

    }
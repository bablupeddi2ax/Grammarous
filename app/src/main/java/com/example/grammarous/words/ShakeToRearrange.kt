package com.example.grammarous.words

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.grammarous.R
import com.example.grammarous.ShakeDetector
import com.example.grammarous.parentViews.Word
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.Random
import kotlin.system.exitProcess


class ShakeToRearrange : AppCompatActivity(), ShakeDetector.OnShakeListener {
    private  var firebaseAuth: FirebaseAuth?=null
    private  var databaseReference: DatabaseReference?=null
    private lateinit var txtGenWord: TextView
    private lateinit var btnRearrange:Button
    private lateinit var btnNext:Button
    private lateinit var mSensorManager: SensorManager
    private lateinit var mShakeDetector: ShakeDetector
    private var genWord: Word? = null
    private var word:String?=null
    private var age: Int = 4
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "com.example.grammarous.SHOW_SCREEN_TIME_ALERT") {
                showAlertDialog()
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shake_to_rearrange)
        val filter = IntentFilter("com.example.grammarous.SHOW_SCREEN_TIME_ALERT")
        filter.priority = RECEIVER_NOT_EXPORTED
        registerReceiver(broadcastReceiver, filter, RECEIVER_NOT_EXPORTED)
        initViews()
        btnRearrange.setOnClickListener {
            if(!txtGenWord.text.isNullOrBlank()){
                val word = genWord?.getWord()
                txtGenWord.text = word
            }
        }
        btnNext.setOnClickListener {
            if(genWord!=null){
                if(databaseReference==null){
                    initFirebase()
                }
                initWord()
            }
        }
        initFirebase()

        initWord()
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mShakeDetector = ShakeDetector()
        mShakeDetector.setOnShakeListener(this)

    }

    private fun initViews() {
        txtGenWord = findViewById(R.id.txtGenWord)
        btnRearrange = findViewById(R.id.btnRearrange)
        btnNext = findViewById(R.id.btnNext)
    }

    private fun initAge(): String {
        val sharedPrefs = getSharedPreferences("userData", Context.MODE_PRIVATE)
        age = sharedPrefs.getInt("age", 4)
        return when (age) {
            in 3..6 -> "3-6"
            in 7..10 -> "7-10"
            in 11..15 -> "11-15"
            else -> "3-6"
        }
    }

    private fun initWord() {
        val ageGroup = initAge()
        CoroutineScope(Dispatchers.IO).launch {
            databaseReference?.child(ageGroup)
                ?.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val childCount = snapshot.childrenCount
                        val randNum = Random().nextInt(childCount.toInt())
                        var i = 0
                        for (postSnapshot in snapshot.children) {
                            if (i == randNum) {
                                genWord = postSnapshot.getValue(Word::class.java)
                                word = genWord?.getWord().toString()
                                genWord?.let { word ->
                                    Log.i("genWord", word.toString())

                                    val jumbledWord = word.getWord()?.let { jumbleWord(it) }
                                    runOnUiThread {
                                        jumbledWord?.lowercase(Locale.ROOT)
                                        txtGenWord.text = jumbledWord
                                    }
                                }
                                break
                            }
                            i++
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("ShakeToRearrange", "Database error: ${error.message}")
                    }
                })
        }
    }

    private fun jumbleWord(word: String): String {
        val arWord = word.toCharArray().toMutableList()
        arWord.shuffle()
        return arWord.joinToString("")
    }
    override fun onResume() {
        super.onResume()
        mSensorManager.registerListener(
            mShakeDetector,
            mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_UI)

    }
    override fun onPause() {
        super.onPause()
        mSensorManager.unregisterListener(mShakeDetector)
    }
    private fun initFirebase() {
        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("kidwords")
    }

    override fun onShake(count: Int) {
        Log.i("inside shake",count.toString())
            val w = genWord?.getWord()
                    txtGenWord.text = w.toString()
        word = word?.lowercase(Locale.ROOT)
        if(word!=null) {
            runOnUiThread{
                animateWordRearrange(txtGenWord, word.toString())
            }
        }

    }
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }
    private fun closeApp() {
        finishAffinity()
        android.os.Process.killProcess(android.os.Process.myPid())
        exitProcess(0)
    }
    private fun animateWordRearrange(textView: TextView, newWord: String) {
        val oldWord = textView.text.toString()
        val charCount = Math.max(oldWord.length, newWord.length)

        // Create animations for each character
        val animations: MutableList<Animator> = ArrayList()
        for (i in 0 until charCount) {
            val oldChar = if (i < oldWord.length) oldWord[i] else ' '
            val newChar = if (i < newWord.length) newWord[i] else ' '

            // Calculate Y translation (upward movement)
            val yTranslation = -textView.height / 2f

            // Calculate X translation based on character position
            var xTranslation: Float
            xTranslation = if (i < oldWord.length && i < newWord.length) {
                if (oldWord[i] == newChar) {
                    0f // Same character, no movement
                } else {
                    // Find the new position of the character in the new word
                    val newIndex = newWord.indexOf(oldChar)
                    ((newIndex - i) * textView.width / charCount).toFloat()
                }
            } else if (i >= oldWord.length) {
                // New character, move from right
                ((i - oldWord.length + 1) * textView.width / charCount).toFloat()
            } else {
                // Old character, move out left
                (-(i + 1) * textView.width / charCount).toFloat()
            }

            // Create animations for each character
            val charAnimation = AnimatorSet()
            val yAnim = ObjectAnimator.ofFloat(textView, "translationY", 0f, yTranslation)
            val xAnim = ObjectAnimator.ofFloat(textView, "translationX", 0f, xTranslation)
            charAnimation.playSequentially(yAnim, xAnim)
            charAnimation.setDuration(1000) // Adjust duration as needed
            animations.add(charAnimation)
        }

        // Combine animations and start them together
        val animationSet = AnimatorSet()
        animationSet.playTogether(animations)
        animationSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                // Update text after animations finish
                textView.text = newWord
            }

            override fun onAnimationEnd(animation: Animator) {
                // Reset translations after animation
                textView.translationX = 0f
                textView.translationY = 0f
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        animationSet.start()
    }
    private fun showAlertDialog() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Screen Time Alert!")
        dialog.setMessage("You have spent 10 minutes of your time already. Please take a break")
        dialog.setPositiveButton("OK") { dialogInterface, _ ->
            dialogInterface.dismiss()
            closeApp()
        }
        dialog.setOnDismissListener{
            closeApp()
        }
        dialog.show()
    }

}

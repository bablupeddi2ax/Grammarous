package com.example.grammarous.words

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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
import java.util.Locale
import java.util.Random

class ShakeWords : AppCompatActivity(), ShakeDetector.OnShakeListener,TextToSpeech.OnInitListener {
    private lateinit var  mAuth:FirebaseAuth
    private lateinit var mSensorManager: SensorManager
    private lateinit var mShakeDetector: ShakeDetector
    private var randGenWord: Word?=null
    private lateinit var mdbRef:DatabaseReference
    private lateinit var txtRandGenWord: TextView
    private lateinit var tts: TextToSpeech
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shake_words)
        txtRandGenWord = findViewById(R.id.randWord)
        tts = TextToSpeech(this, this)
        val btnSpeakWord: Button = findViewById(R.id.btnSpeakWord)

        // Set click listener for the Speak Word button
        btnSpeakWord.setOnClickListener {
            // Call speakWord method when the button is clicked
            if(randGenWord==null){
                Toast.makeText(this,"Please shake your device first!",Toast.LENGTH_SHORT).show()
            }else {
                speakWord()
            }
        }

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mShakeDetector = ShakeDetector()
        mShakeDetector.setOnShakeListener(this)
        txtRandGenWord.setOnClickListener{
            val intent = Intent(this@ShakeWords, WordDetails::class.java)
            intent.putExtra("word", randGenWord?.getWord())
            intent.putExtra("definition",randGenWord?.getDefinition())
            intent.putExtra("synonyms",randGenWord?.getSynonyms())
            intent.putExtra("pronunciation",randGenWord?.getPronunciation())
            intent.putExtra("partsOfSpeech",randGenWord?.getPartsOfSpeech())
            startActivity(intent)

        }
    }
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Set language for TTS
            val result = tts.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Log error if language is not supported
                Log.e("TTS", "Language is not supported")
            }
        } else {
            // Log error if TTS initialization fails
            Log.e("TTS", "Initialization failed")
        }
    }
    private fun speakWord() {
        // Check if TTS is initialized
        if (::tts.isInitialized) {
            val word = randGenWord?.getWord().toString()
            tts.speak(word, TextToSpeech.QUEUE_FLUSH, null, null)
        }
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
    override fun onShake(count: Int) {
        mAuth  = FirebaseAuth.getInstance()
        mdbRef = FirebaseDatabase.getInstance().getReference("words")

        mdbRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val count = snapshot.childrenCount
                val randomNumber = Random().nextInt(count.toInt())
                var i = 0

                for(postSnapShot in snapshot.children){
                    if(i == randomNumber){
                        randGenWord = postSnapShot.getValue(Word::class.java)
                        break
                    }
                    i++
                }

                // Check if a random word is found
                if (randGenWord != null) {
                    // Pass word details to the next activity
                    txtRandGenWord.text = randGenWord?.getWord().toString()

                } else {
                    Log.i("error", "Failed to retrieve a random word.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("error", error.message)
            }
        })
    }
    override fun onDestroy() {
        // Shutdown TextToSpeech when the activity is destroyed
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }

}
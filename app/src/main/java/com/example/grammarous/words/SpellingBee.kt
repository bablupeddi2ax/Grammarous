package com.example.grammarous.words

import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable.RepeatMode
import com.example.grammarous.R
import com.example.grammarous.parentViews.Word
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale
import java.util.Random
class SpellingBee : AppCompatActivity(), TextToSpeech.OnInitListener {
    // Declare variables
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var btnWord: Button
    private lateinit var edtSpelling: EditText
    private lateinit var btnCheck: Button
    private  var genWord: Word? = null
    private lateinit var lottie: LottieAnimationView
    private lateinit var beeAnimationView: LottieAnimationView
    private lateinit var btnReveal: Button
    private lateinit var txtWrong: TextView
    private lateinit var databaseReference: DatabaseReference
    private var isWordChecked = false
    private var isWordGenerated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spelling_bee)

        // Initialize views and Firebase database reference
        initializeViews()
        initializeDatabaseReference()

        // Initialize TextToSpeech
        textToSpeech = TextToSpeech(this, this)

        generateWord(true)
        // Set onClick listeners
        setOnClickListeners()
    }

    private fun initializeViews() {
        // Initialize views
        lottie = findViewById(R.id.animationView)
        lottie.isVisible = false
        txtWrong = findViewById(R.id.txtWrong)
        btnCheck = findViewById(R.id.btnCheck)
        btnReveal = findViewById(R.id.btnReveal)
        edtSpelling = findViewById(R.id.edtSpelling)
        btnWord = findViewById(R.id.btnWord)
        beeAnimationView = findViewById(R.id.beeAnimationView)
        beeAnimationView.setOnClickListener{
            beeAnimationView.playAnimation()
            beeAnimationView.setSafeMode(true)
//            beeAnimationView.playSoundEffect()
        }

    }

    private fun initializeDatabaseReference() {
        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("words")
    }

    private fun setOnClickListeners() {
        // Set onClick listeners
        btnWord.setOnClickListener { speakWord() }
        btnCheck.setOnClickListener { checkSpelling()
        if(edtSpelling.text.toString() == genWord?.getWord()){
            textToSpeech.speak("Correct",TextToSpeech.QUEUE_FLUSH,null,null)
        }
        }
        btnReveal.setOnClickListener { revealWord() }
    }

    private fun generateWord(speak:Boolean) {
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val count = snapshot.childrenCount
                    val randomNumber = Random().nextInt(count.toInt())
                    var i = 0

                    for (postSnapShot in snapshot.children) {
                        if (i == randomNumber) {
                            genWord = postSnapShot.getValue(Word::class.java)
                            isWordGenerated = true
                            break
                        }
                        i++
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    isWordGenerated = false
                }
            })


    }
    private fun speakWord(){
        val word = genWord?.getWord()
        textToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, null, null)

    }
    private fun requestNewWord(){
        isWordChecked = true
    }
    private fun checkSpelling() {

            val word = genWord?.getWord().toString()
            val enteredWord = edtSpelling.text.toString()

            if (word.equals(enteredWord, ignoreCase = true)) {
                isWordChecked = true
                lottie.visibility = View.VISIBLE
                lottie.playAnimation()
                textToSpeech.speak("Correct", TextToSpeech.QUEUE_FLUSH,null,null)
                txtWrong.visibility = View.GONE
                edtSpelling.text.clear() // Clear EditText
                generateWord(false)
            } else {
                txtWrong.visibility = View.VISIBLE
                lottie.visibility = View.GONE
                textToSpeech.speak("Please try again buddy",TextToSpeech.QUEUE_FLUSH,null,null)

        }
    }

    private fun revealWord() {
        if (isWordGenerated) {
            val word = genWord?.getWord().toString()
            if (word.isNotBlank() && word.isNotEmpty()) {
                Toast.makeText(this@SpellingBee, word, Toast.LENGTH_SHORT).show()
            } else {
                // Handle if word is empty
            }
        } else {
            Toast.makeText(this, "Please generate a word first.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // TextToSpeech engine initialization successful
            val result = textToSpeech.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Log error if language is not supported
                Log.e("TTS", "Language is not supported")
            }
        } else {
            // Log error if TextToSpeech initialization fails
            Log.e("TTS", "Initialization failed")
        }
    }


    // Implement other required methods
}

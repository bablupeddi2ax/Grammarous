package com.example.grammarous.words

import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log

import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.grammarous.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import java.util.Locale

class WordDetails : AppCompatActivity(),TextToSpeech.OnInitListener {
    private lateinit var txtWord: TextView
    private lateinit var txtDefinition: TextView
    private lateinit var txtSynonyms: TextView
    private lateinit var txtPartsOfSpeech: TextView
    private lateinit var txtPronunciation: TextView
    private lateinit var tts:TextToSpeech
   private  var genWord = ""

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_details)
        tts = TextToSpeech(this,this)
        val speakBtn = findViewById<Button>(R.id.btnSpeak)
        speakBtn.setOnClickListener{
            speak()
        }

        txtWord = findViewById(R.id.txtWord)
        txtDefinition = findViewById(R.id.txtDefinition)
        txtSynonyms = findViewById(R.id.txtSynonyms)
        txtPronunciation = findViewById(R.id.txtPronunciation)
        txtPartsOfSpeech = findViewById(R.id.txtPartsOfSpeech)

        val intent = intent
        genWord = intent.getStringExtra("word") ?: ""
        var mRef:DatabaseReference = FirebaseDatabase.getInstance().getReference("words")
        val wordReference: Query = mRef.orderByChild("word").equalTo(genWord)
        wordReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (postSnapshot in dataSnapshot.children) {
                    val word: String? = postSnapshot.child("word").getValue(String::class.java)
                    val partOfSpeech: String? = postSnapshot.child("partsofspeech").getValue(String::class.java)
                    val definition: String? = postSnapshot.child("definition").getValue(String::class.java)
                    val pronunciation: String? = postSnapshot.child("audio").getValue(String::class.java)
                    val synonyms: ArrayList<String> = arrayListOf()
                    postSnapshot.child("synonym").children.forEach {
                        synonyms.add(it.value.toString())
                    }
                    val antonyms: ArrayList<String>  = arrayListOf()
                    postSnapshot.child("antonym").children.forEach {
                            antonyms.add(it.value.toString())
                    }
                    val syns = synonyms.joinToString(", ") ?:""
                    txtSynonyms.text = syns
                    txtSynonyms.text = txtSynonyms.text.toString().plus(antonyms?.joinToString { "" }?:"")

                    txtPronunciation.text = "Pronunciation: ".plus(pronunciation.toString())
                    txtDefinition.text = definition.toString()
                    txtPartsOfSpeech.text = "Parts Of Speech:".plus(partOfSpeech.toString())
                    txtWord.text = word.toString()


                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
                Log.e("WordDetails", "Error: ${databaseError.message}")
            }
        })

    }

    private fun speak() {
        if (::tts.isInitialized && genWord.isNotEmpty()) {
            val word = genWord
            tts.speak(word, TextToSpeech.QUEUE_FLUSH, null, null)
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
}

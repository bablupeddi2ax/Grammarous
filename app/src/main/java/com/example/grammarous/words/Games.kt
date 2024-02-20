package com.example.grammarous.words

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.grammarous.R
import java.util.Locale

class Games : AppCompatActivity() {
    private lateinit var cardViewMarching:CardView
    private lateinit var cardViewFillinTheBlanks:CardView
    private lateinit var cardViewSpellingBee:CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_games)

        cardViewMarching = findViewById(R.id.cardViewMatching)
        cardViewFillinTheBlanks = findViewById(R.id.cardViewFillInTheBlanks)
        cardViewSpellingBee = findViewById(R.id.cardViewSpellingBee)

        cardViewMarching.setOnClickListener{
            val intent = Intent(this@Games,MatchingGames::class.java)
            startActivity(intent)
        }
        cardViewFillinTheBlanks.setOnClickListener{
            val intent = Intent(this@Games,FillBlanks::class.java)
            startActivity(intent)
        }
        cardViewSpellingBee.setOnClickListener {
            val intent = Intent(this@Games,SpellingBee::class.java)
            startActivity(intent)
        }

    }


}
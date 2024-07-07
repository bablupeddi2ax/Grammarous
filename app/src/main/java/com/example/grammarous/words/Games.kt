package com.example.grammarous.words

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.grammarous.R
import com.example.grammarous.mcq.Mcq
import com.example.grammarous.mcq.Topic
import java.util.Locale

class Games : AppCompatActivity() {
    // private lateinit var cardViewMarching:CardView
    private lateinit var cardViewFillinTheBlanks:CardView
    private lateinit var cardViewSpellingBee:CardView
    private lateinit var cardViewMcq:CardView
    private lateinit var layoutJumbledWords:CardView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_games)


        cardViewFillinTheBlanks = findViewById(R.id.cardViewFillInTheBlanks)
        cardViewSpellingBee = findViewById(R.id.cardViewSpellingBee)
        cardViewMcq= findViewById(R.id.cardViewMcq)
        layoutJumbledWords = findViewById(R.id.cardViewJumbledWords)

//        cardViewMarching.setOnClickListener{
//            val intent = Intent(this@Games,MatchingGames::class.java)
//            startActivity(intent)
//        }
        cardViewFillinTheBlanks.setOnClickListener{
            val intent = Intent(this@Games,FillBlanks::class.java)
            startActivity(intent)
        }
        cardViewSpellingBee.setOnClickListener {
            val intent = Intent(this@Games,SpellingBee::class.java)
            startActivity(intent)
        }
        cardViewMcq.setOnClickListener {
            val intent = Intent(this@Games, Topic::class.java)
            startActivity(intent)
        }
        layoutJumbledWords.setOnClickListener {
            val intent = Intent(this@Games,Mcq::class.java)
            startActivity(intent)
        }

    }


}
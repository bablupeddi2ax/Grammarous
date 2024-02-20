package com.example.grammarous.words

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.KeyEvent
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.marginRight
import androidx.core.view.setMargins
import com.example.grammarous.R
import com.example.grammarous.parentViews.Word
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Random

class FillBlanks : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private var genWord: Word?=null
    private val editTextList = mutableListOf<EditText>()
    private val revealedMap = mutableMapOf<Int,Boolean>()
    private lateinit var linearLayout: LinearLayout
    private lateinit var btnCheck:Button
    private lateinit var btnReveal:Button
    private lateinit var btnSkip:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fill_blanks)
        databaseReference  = FirebaseDatabase.getInstance().getReference("words")
        linearLayout = findViewById(R.id.blanksLinearLayout)
        btnCheck = findViewById(R.id.btnCheck)
        btnReveal  = findViewById(R.id.btnReveal)
        btnSkip = findViewById(R.id.btnSkip)
        btnCheck.setOnClickListener {
            checkAnswer()
        }
        btnReveal.setOnClickListener {
            revealOneLetter()
        }
        btnSkip.setOnClickListener {
            skip()
        }
        // get word from db
        getWord()
        //display edittexts based on number of letters in the word

        // convert word by replacing some letter s with underscores
//        modifyWord()

    }

    private fun skip() {
       revealedMap.clear()
        editTextList.clear()
        linearLayout.removeAllViews()

        val currentWord = getWord()
        Toast.makeText(this@FillBlanks,currentWord,Toast.LENGTH_SHORT).show()

    }

    private fun displayEditTexts(genWord: Word?) {
        val word = genWord?.getWord()
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(0,0,20,0)
      if(!word.isNullOrBlank()){
          val num = word.length
          for(i in 0 until num){
              val editText = EditText(this)
              editTextList.add(editText)
              editText.layoutParams  = layoutParams
              editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(1))
              editText.setOnKeyListener { v, keyCode, event ->
                  if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                      val index = editTextList.indexOf(v)
                      if (index < editTextList.size - 1) {
                          editTextList[index + 1].requestFocus()
                      } else {
                          editTextList[index].clearFocus()
                      }
                      return@setOnKeyListener true
                  }
                  return@setOnKeyListener false
              }

              if(i<2){
                  editText.setText(word[i].toString())
                  editText.isEnabled = false
                  revealedMap[i] = true
              }
              linearLayout.addView(editText)

          }
      }


    }

    private fun getWord():String {
        databaseReference.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val count = snapshot.childrenCount
                var i = 0
                val randNum = Random().nextInt(count.toInt())
                for(postSnapshot in snapshot.children){
                    if(i==randNum) {
                        try {
                            genWord = postSnapshot.getValue(Word::class.java)
                            break
                        }catch (e:Exception){
                            Log.i("inside catch ", e.message.toString())
                            genWord?.setWord(postSnapshot.getValue(String::class.java))
                        }

                    }
                    i++
                }
                displayEditTexts(genWord)

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        if(genWord?.getWord().toString().isNotBlank() || genWord?.getWord().toString().isNotEmpty()) {
            return genWord?.getWord().toString()
        }else{
            return "failed"
        }
    }
    private fun checkAnswer(){
        val userAnswer = editTextList.joinToString(""){it.text.toString()}
        if(userAnswer.length!=genWord?.getWord().toString().length){
            Toast.makeText(this@FillBlanks,"Some blanks are left empty",Toast.LENGTH_SHORT).show()
        }
        val correctAnswer = genWord?.getWord()
        if(userAnswer.equals(correctAnswer, ignoreCase = true)){
            Toast.makeText(this@FillBlanks,"COrrect ",Toast.LENGTH_SHORT).show()
            clear()
            getWord()
        }else{
            for (i in userAnswer.indices) {
                if (revealedMap[i]!=true && correctAnswer?.length != null && i< correctAnswer.length && userAnswer[i]!=correctAnswer[i] ) {
                    editTextList[i].text.clear()
                }else if(i < userAnswer.length && correctAnswer?.get(i) !=userAnswer[i]){
                    editTextList[i].text.clear()
                }
            }

        }
    }
    private fun clear(){
        linearLayout.removeAllViews()
        revealedMap.clear()
        editTextList.clear()
    }
    private fun revealOneLetter() {
        val unrevealedIndices = mutableListOf<Int>()

        // Find unrevealed indices
        for (i in editTextList.indices) {
            if (revealedMap[i] != true) {
                unrevealedIndices.add(i)
            }
        }

        // If there are unrevealed letters
        if (unrevealedIndices.isNotEmpty()) {
            // Choose a random unrevealed index
            val randomIndex = unrevealedIndices.random()

            // Ensure the randomIndex is within bounds
            if (randomIndex < genWord?.getWord().toString().length) {
                // Set the text of the corresponding EditText to the correct letter
                editTextList[randomIndex].setText(genWord?.getWord().toString()[randomIndex].toString())
                revealedMap[randomIndex] = true
            } else {
                // Handle the case when the randomIndex is out of bounds
                Toast.makeText(this@FillBlanks, "Failed to reveal letter", Toast.LENGTH_SHORT).show()
                Toast.makeText(this@FillBlanks,genWord?.getWord().toString(),Toast.LENGTH_SHORT).show()
            }
        } else {
            // If all letters are already revealed, display a message to the user
            Toast.makeText(this@FillBlanks, "All letters are already revealed", Toast.LENGTH_SHORT).show()
        }
    }

}
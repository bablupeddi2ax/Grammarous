package com.example.grammarous.words

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grammarous.R
import com.example.grammarous.alphabets.MyRecyclerViewAdapter
import com.example.grammarous.parentViews.Word
import com.google.firebase.database.*
import kotlinx.coroutines.*

class WordList : AppCompatActivity() {
    private lateinit var databaseReference: DatabaseReference
    private lateinit var listOfWordsStartingWithCharacter: MutableList<String>
    private lateinit var recyclerView: RecyclerView
    private lateinit var myAdapter: MyRecyclerViewAdapter

    private val parentJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + parentJob)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_list)
        recyclerView = findViewById(R.id.recyclerView)
        listOfWordsStartingWithCharacter = ArrayList()
        val alphabet = intent.getStringExtra("alphabet")
        Log.i("alphabet",alphabet.toString())
        databaseReference = FirebaseDatabase.getInstance().getReference("words")
        fetchDataFromFirebase(alphabet)
    }

    private fun fetchDataFromFirebase(alphabet: String?) {
        coroutineScope.launch {
            val query =
                databaseReference.orderByChild("word").startAt(alphabet.toString())


            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapShot: DataSnapshot) {
                    val listOfWords = mutableListOf<String>()
                    for (snapshot in dataSnapShot.children) {
                        val word = snapshot.getValue(Word::class.java)
                        listOfWords.add(word?.getWord().toString())
                    }
                    listOfWords.addAll(listOfWords.filter { it.length>=3 }.toMutableList())

                    setupRecyclerView(listOfWords)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }
    }

    private fun setupRecyclerView(listOfWords:MutableList<String>) {
        myAdapter = MyRecyclerViewAdapter(this@WordList, listOfWords)
        Log.i("listofwordswithchars",listOfWordsStartingWithCharacter.size.toString())
        Log.i("listofwordswithchars",listOfWordsStartingWithCharacter.toString())

        recyclerView.layoutManager = LinearLayoutManager(this@WordList)
        recyclerView.adapter = myAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        parentJob.cancel()
    }
}

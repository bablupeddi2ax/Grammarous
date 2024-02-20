package com.example.grammarous.words
import android.content.ClipData
import android.content.ClipDescription

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.OnDragListener
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.grammarous.R

class MatchingGames : AppCompatActivity() {
    private val synonymMap: HashMap<String, String> = hashMapOf()
    private lateinit var wordColumn: LinearLayout
    private lateinit var synonymColumn: LinearLayout
    private var correctMatches: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matching_games)

        synonymMap["Afraid"] = "SCARED"
        synonymMap["EMPTY"] = "BLANK"
        synonymMap["BIG"] = "HUGE"
        synonymMap["BUNNY"] = "HARE"
        synonymMap["CAR"] = "AUTOMOBILE"

        wordColumn = findViewById(R.id.wordsColumn)
        synonymColumn = findViewById(R.id.synonymsColumn)
        val dragEventListener = View.OnDragListener { view, event ->

            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                }

                DragEvent.ACTION_DRAG_ENTERED -> {
                    view.invalidate()
                    true
                }

                DragEvent.ACTION_DRAG_LOCATION -> {
                    true
                }

                DragEvent.ACTION_DRAG_EXITED -> {
                    view.invalidate()
                    true
                }

                DragEvent.ACTION_DROP -> {
                    val item = event.clipData.getItemAt(0)
                    val droppedWord = item.text.toString()

                    // Ensure that the view is a LinearLayout before casting
                    if (view is LinearLayout) {
                        val destination = view
                        val droppedTextView = event.localState as TextView
                        val droppedSynonym = synonymMap[droppedTextView.text.toString()]

                        if (droppedSynonym == droppedWord) {
                            // Remove both TextViews
                            destination.removeView(droppedTextView)
                            val owner = droppedTextView.parent as ViewGroup
                            owner.removeView(droppedTextView)
                            correctMatches++
                            if (correctMatches == synonymMap.size) {
                                // All matches found, show congratulations message
                                Toast.makeText(this@MatchingGames, "Congratulations! You've matched all synonyms.", Toast.LENGTH_SHORT).show()
                            }
                            // Set the drop result to true as the match is correct
                            return@OnDragListener true
                        } else {
                            // Handle incorrect drop
                            Toast.makeText(this@MatchingGames, "Incorrect match. Try again.", Toast.LENGTH_SHORT).show()
                            // Set the drop result to false as the match is incorrect
                            return@OnDragListener false
                        }
                    } else {
                        // Set the drop result to false if the view is not a LinearLayout
                        return@OnDragListener false
                    }
                }






                DragEvent.ACTION_DRAG_ENDED -> {
                    view.invalidate()
                    true
                }

                else -> false
            }
        }

        for ((word, _) in synonymMap) {
            val wordTextView = createDraggableTextView(word,dragEventListener)
            wordColumn.addView(wordTextView)
        }

        val synonymList = synonymMap.values.toList().shuffled()
        for (synonym in synonymList) {
            val synonymTextView = createDroppableTextView(synonym,dragEventListener)
            synonymColumn.addView(synonymTextView)
        }
    }

    private fun checkForCorrectMatch(v: View) {

    }

    private fun createDraggableTextView(text: String,dragEventListener: OnDragListener): TextView {
        val textView = TextView(this)
        textView.text = text
        textView.width = 100
        textView.height = 100
        textView.setOnLongClickListener { v ->
            val data = ClipData.newPlainText("text", text)
            val dragShadow = View.DragShadowBuilder(v)
            v.startDragAndDrop(data, dragShadow, v, 0)
            true
        }
        textView.setOnDragListener(dragEventListener)
        return textView
    }

    private fun createDroppableTextView(text: String, dragEventListener: OnDragListener): TextView {
        val textView = TextView(this)
        textView.text = text
        textView.width = 100
        textView.height = 100
        textView.setBackgroundColor(Color.LTGRAY)

        // Enable drag on the droppable TextView
        textView.setOnLongClickListener { v ->
            val clipText = text
            val item = ClipData.Item(clipText)
            val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
            val clipData = ClipData(clipText, mimeTypes, item)
            val dragShadowBuilder = View.DragShadowBuilder(v)
            v.startDragAndDrop(clipData, dragShadowBuilder, v, 0)
            true
        }

        // Handle drop events on the droppable TextView
        textView.setOnDragListener(dragEventListener)
        return textView
    }


// task dia fir reosurces nhi milre
             
}

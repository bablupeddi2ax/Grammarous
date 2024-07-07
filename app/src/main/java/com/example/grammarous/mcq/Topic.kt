package com.example.grammarous.mcq

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.core.view.get
import com.example.grammarous.R
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.util.Log

class Topic : AppCompatActivity() {
    private lateinit var topicGridLayout: GridLayout
    private lateinit var databaseReference: DatabaseReference
    private var topicList: MutableList<String> = mutableListOf()
    private lateinit var selectedTopic:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic)

        initView()
        initDb()
    }


private fun initView() {
    topicGridLayout = findViewById(R.id.topicGridLayout)
}



    private fun initDb() {
        databaseReference = Firebase.database.getReference("/mcq")
        CoroutineScope(Dispatchers.IO).launch {
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapShot in snapshot.children) {
                        val topic = postSnapShot.key.toString()
                        if (topic.isNotBlank()) {
                            topicList.add(topic)
                        }
                    }
                    // Add topics after fetching them from Firebase
                    initTopics()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }
    }
    private fun initTopics() {
//        topicList.addAll(listOf("1","2","3","4","5","6","7","8"))
    for (topic in topicList) {
        val topicLayout = LayoutInflater.from(this).inflate(R.layout.topic_layout, null)
        topicLayout.tag = topic
        val cardViewFront: CardView = topicLayout.findViewById(R.id.cardViewFront)
        cardViewFront.setBackgroundResource(R.drawable.topic_bg)

        val topicTextView: TextView = topicLayout.findViewById(R.id.txtTopic)
        topicTextView.text = topic
        cardViewFront.tag = topic
        // Set tags on the topicLayout
        val cardViewBack = topicLayout.findViewById<CardView>(R.id.cardViewBack)
        topicLayout.tag = topic // Set the tag to the topic string

        var isFlipped = false // Flag to track if card is flipped

        topicLayout.setOnClickListener {
            selectedTopic = topicLayout.tag.toString()
            val animatorSet = AnimatorSet()
//            val scaleXAnimator = ObjectAnimator.ofFloat(topicLayout, "pivotX", topicLayout.width / 2f, topicLayout.width / 2f) // Set pivot to center of the card (width / 2)
//            val scaleYAnimator = ObjectAnimator.ofFloat(topicLayout, "pivotY", topicLayout.height / 2f, topicLayout.height / 2f) // Set pivot to center of the card (height / 2)
//            val widthAnimator = ObjectAnimator.ofFloat(topicLayout, "scaleX", 1f, 1.5f) // Scale width from 1 to 1.2
//            val heightAnimator = ObjectAnimator.ofFloat(topicLayout, "scaleY", 1f, 1.5f) // Scale height from 1 to 1.2
//            val scaleXAnimatorN = ObjectAnimator.ofFloat(topicLayout, "pivotX", topicLayout.width / 2f, topicLayout.width / 2f) // Set pivot to center of the card (width / 2)
//            val scaleYAnimatorN = ObjectAnimator.ofFloat(topicLayout, "pivotY", topicLayout.height / 2f, topicLayout.height / 2f) // Set pivot to center of the card (height / 2)
//            val widthAnimatorN = ObjectAnimator.ofFloat(topicLayout, "scaleX", 1f, 0.75f) // Scale width from 1 to 1.2
//            val heightAnimatorN = ObjectAnimator.ofFloat(topicLayout, "scaleY", 1f, 0.75f) // Scale height from 1 to 1.2
            if (!isFlipped) { // Flip from front to back
                animatorSet.playSequentially(
                    ObjectAnimator.ofFloat(topicLayout, "rotationY", 0f, 360f), // Rotate 180 degrees for front-to-back flip
//                    scaleXAnimator,
//                    scaleYAnimator,
//                    widthAnimator,
//                    heightAnimator
                )
            } else { // Flip from back to front
                animatorSet.playSequentially(
                    ObjectAnimator.ofFloat(topicLayout, "rotationY", 360f, 0f), // Rotate 180 degrees for back-to-front flip
//                    scaleXAnimatorN,
//                    scaleYAnimatorN,
//                    widthAnimatorN,
//                    heightAnimatorN


                )
            }
            animatorSet.duration = 500
            animatorSet.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    isFlipped = !isFlipped
                    if(isFlipped){
                        cardViewBack.visibility  = View.VISIBLE
//                        topicLayout.scaleX = 1.2F
//                        topicLayout.scaleY = 1.2F
                    }else{
                        cardViewBack.visibility = View.GONE
//                        topicLayout.scaleX = 1.5F
//                        topicLayout.scaleY = 1.5F

                    }
                    // No need to manipulate views here, content remains the same
                    // after the flip animation (no reversed order)
                }
            })
            animatorSet.start()
        }





        val layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(25, 5, 20, 20)
        topicLayout.layoutParams = layoutParams

        topicGridLayout.addView(topicLayout)
    }
}


fun onEasyButtonClick(view: View) {
    // Handle click on the Easy button
    val intent = Intent(this@Topic, Mcq::class.java)
    val topic = view.tag as String // Cast directly to String
    intent.putExtra("topic", selectedTopic)
    intent.putExtra("level", "easy")
    startActivity(intent)
}

fun onMediumButtonClick(view: View) {
    val intent = Intent(this@Topic, Mcq::class.java)
    val topic = view.tag as String // Cast directly to String
    intent.putExtra("topic", selectedTopic)
    intent.putExtra("level", "medium")
    startActivity(intent)
}

fun onHardButtonClick(view: View) {
    val intent = Intent(this@Topic, Mcq::class.java)
    val topic = view.tag as String // Cast directly to String
    intent.putExtra("topic", selectedTopic)
    intent.putExtra("level", "hard")
    startActivity(intent)
}
    private fun findFlippedView(parent: ViewGroup): View? {
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            if (child.tag != null && child.tag.toString() == "flipped_layout") {
                return child
            }
        }
        return null
    }
}


//        topicLayout.setOnClickListener {
//            val animatorSet = AnimatorSet()
//            if (!isFlipped) { // Flip from front to back
//                animatorSet.playSequentially(
//                    ObjectAnimator.ofFloat(cardView, "rotationY", 0f, 180f)
//                )
//                animatorSet.addListener(object : AnimatorListenerAdapter() {
//                    override fun onAnimationEnd(animation: Animator) {
//                        // Change card content to flipped layout
//                        (topicLayout as ViewGroup).removeView(cardView)
//                        val flippedLayout = LayoutInflater.from(this@Topic).inflate(R.layout.card_flip_layout, null)
//                        flippedLayout.setBackgroundResource(R.drawable.topic_bg)
//                        (flippedLayout as ViewGroup).childCount != (1
//                            ?: ((flippedLayout as ViewGroup).get(0) as ViewGroup).children.let {
//                                (it as View).tag = topic
//                            })
//                        topicLayout.addView(flippedLayout)
//                    }
//                })
//                isFlipped = true // Update the flipped flag
//            } else { // Flip from back to front
//                animatorSet.playSequentially(
//                    ObjectAnimator.ofFloat(cardView, "rotationY", 180f, 360f)
//                )
//                animatorSet.addListener(object : AnimatorListenerAdapter() {
//                    override fun onAnimationEnd(animation: Animator) {
//                        // Change card content back to original layout
//                        (topicLayout as ViewGroup).removeAllViews()
//                        (topicLayout as ViewGroup).addView(cardView)
//                    }
//                })
//                isFlipped = false // Update the flipped flag
//            }
//            animatorSet.duration = 500
//            animatorSet.start()
//        }







































//topicLayout.setOnClickListener {
//    val animatorSet = AnimatorSet()
//    if (!isFlipped) { // Flip from front to back
//        Log.i("!isFLipped","$isFlipped")
//        animatorSet.playSequentially(
//            ObjectAnimator.ofFloat(cardView, "rotationY", 0f, 180f)
//        )
//        animatorSet.addListener(object : AnimatorListenerAdapter() {
//            override fun onAnimationEnd(animation: Animator) {
//                (topicLayout as ViewGroup).removeViewAt((topicLayout as ViewGroup).childCount - 1) // Remove flippedLayout
//                val flippedLayout = LayoutInflater.from(this@Topic).inflate(R.layout.card_flip_layout, null)
//                flippedLayout.setBackgroundResource(R.drawable.topic_bg)
//                (topicLayout as ViewGroup).addView(flippedLayout)
//            }
//        })
//    }else{
//        Log.i("inside else","$isFlipped")
//        animatorSet.startDelay = 500; // Add a small delay (optional)
//        animatorSet.playSequentially(ObjectAnimator.ofFloat(cardView, "rotationY", 180f, 0f));
//        animatorSet.addListener(object : AnimatorListenerAdapter() {
//            override fun onAnimationEnd(animation: Animator) {
//                val childCount = (topicLayout as ViewGroup).childCount
//                if (childCount > 0) { // Check if there's a child
//                    val childView = (topicLayout as ViewGroup).getChildAt(childCount - 1)
//                    if (childView is ViewGroup && childView.javaClass.simpleName == "FlippedLayout") {
//                        (topicLayout as ViewGroup).removeView(childView) // Remove flippedLayout
//                    }
//                }
//                (topicLayout as ViewGroup).addView(cardView) // Add cardView back
//            }
//        })
//    }
//    animatorSet.duration = 500
//    animatorSet.start()
//    isFlipped = !isFlipped; // Update flipped flag after animation
//}
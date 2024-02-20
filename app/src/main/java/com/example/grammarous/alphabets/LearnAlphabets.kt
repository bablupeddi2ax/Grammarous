package com.example.grammarous.alphabets

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grammarous.MainActivity
import com.example.grammarous.R

class LearnAlphabets : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TreeNodeAdapter
    private var nodeList = mutableListOf<TreeNode>(TreeNode('A'))
    private var isAlignedLeft = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn_alphabets)
        recyclerView = findViewById(R.id.recyclerViewLetters)
        adapter = TreeNodeAdapter(nodeList) {
            val lastNodeGenerated = nodeList.lastOrNull()
            val lastLetterGenerated = lastNodeGenerated?.getLetter()
            if (lastLetterGenerated != null) {
                if (nodeList.size==26) {
                    val dialog = Dialog(this)
                    dialog.setContentView(R.layout.dialog_layout)
                    val continueButton = dialog.findViewById<Button>(R.id.btnContinue)
                    val playAgainButton = dialog.findViewById<Button>(R.id.btnPlayAgain)
                    playAgainButton.setOnClickListener {
                        dialog.dismiss()
                        nodeList.removeAll(nodeList)
                        nodeList.addAll(mutableListOf<TreeNode>(TreeNode('A')))

                        adapter.notifyDataSetChanged()
                    }
                    continueButton.setOnClickListener {
                        val intent = Intent(this@LearnAlphabets, MainActivity::class.java)
                        startActivity(intent)
                    }
                    dialog.create()
                    dialog.show()
                }
                if (lastLetterGenerated != null) {
                    val nextLetter = (lastLetterGenerated + 1).toChar()
                    val newNode = TreeNode(nextLetter)
                    if (isAlignedLeft) {
                        newNode.setLeftAligned(true)
                    } else {
                        newNode.setLeftAligned(false)
                    }
                    isAlignedLeft = !isAlignedLeft
                    nodeList.add(newNode)
                    adapter.notifyDataSetChanged()
                }
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(this@LearnAlphabets)
        recyclerView = findViewById(R.id.recyclerViewLetters) // Make sure to use the correct view ID
        recyclerView.adapter = adapter
    }
}

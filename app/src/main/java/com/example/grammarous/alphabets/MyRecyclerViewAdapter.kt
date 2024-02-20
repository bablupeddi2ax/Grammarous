package com.example.grammarous.alphabets

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.grammarous.R
import com.example.grammarous.words.WordDetails

class MyRecyclerViewAdapter(val context: Context, val wordList: MutableList<String>):
    RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyRecyclerViewAdapter.MyViewHolder {
       val view = LayoutInflater.from(context).inflate(R.layout.word_list_item,parent,false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyRecyclerViewAdapter.MyViewHolder, position: Int) {
      holder.word.text = wordList[position]
    }

    override fun getItemCount(): Int {
        return wordList.size
    }
    inner class MyViewHolder(private val itemView: View):RecyclerView.ViewHolder(itemView){
        val word = itemView.findViewById<TextView>(R.id.txtWord)

    }
}
package com.example.grammarous.alphabets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.grammarous.R


class TreeNodeAdapter(
    private val nodeList: List<TreeNode>,
    private val onItemClickListener: View.OnClickListener
) : RecyclerView.Adapter<TreeNodeAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         val nodeText: TextView = itemView.findViewById(R.id.nodeText)
        fun bindData(node: TreeNode) {
            nodeText.text = node.getLetter().toString()
            // Set alignment based on leftAligned property
            if (node.isLeftAligned()) {
                // Align to the left
                nodeText.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
            } else {
                // Align to the right
                nodeText.textAlignment = View.TEXT_ALIGNMENT_TEXT_END
            }
        }

        init {
            itemView.setOnClickListener { view ->
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener.onClick(view)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.tree_node_layout,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
       return nodeList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val node  = nodeList[position]
        holder.bindData(node)
    }
}

package com.example.wordstest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CustomAdapter(private val dataSet: ArrayList<RecResult>): RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val imageView = view.findViewById<ImageView>(R.id.rec_image)!!
        val question = view.findViewById<TextView>(R.id.rec_question)!!
        val answer = view.findViewById<TextView>(R.id.rec_answer)!!
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val data = dataSet[position]

        viewHolder.imageView.setImageResource(data.imageView)
        viewHolder.question.text = data.question
        viewHolder.answer.text = data.answer
    }

    override fun getItemCount() = dataSet.size
}
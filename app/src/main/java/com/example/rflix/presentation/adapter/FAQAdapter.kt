package com.example.rflix.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.rflix.R


class FAQAdapter(private val faqList: ArrayList<Array<String>>) :
    RecyclerView.Adapter<FAQAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var cardItem: CardView = view.findViewById(R.id.cardItem)
        var quesText: TextView = view.findViewById(R.id.quesText)
        var ansText: TextView = view.findViewById(R.id.ansText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.faq_card_view_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val faq = faqList[position]
        holder.quesText.text = faq[0]

//        holder.cardItem.setOnClickListener {
//            holder.ansText.visibility = View.VISIBLE
        holder.ansText.text = faq[1]
//        }
    }

    override fun getItemCount(): Int {
        return faqList.size
    }
}
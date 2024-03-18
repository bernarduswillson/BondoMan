package com.example.transactionapp.ui.screen.mainmenu.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.transactionapp.R
import com.example.transactionapp.ui.viewmodel.model.TransactionDateList

class TransactionAdapter(
    private val listTransactionHistory: List<TransactionDateList>
): RecyclerView.Adapter<TransactionAdapter.TransactionHistoryItem>() {
    inner class TransactionHistoryItem(itemView: View): RecyclerView.ViewHolder(itemView){
        val linearLayout: LinearLayout = itemView.findViewById(R.id.transactionContainer)
        val dateText: TextView = itemView.findViewById(R.id.dateText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionHistoryItem {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.transaction_history_list, parent, false)
        return TransactionHistoryItem(view)
    }

    override fun getItemCount(): Int = listTransactionHistory.size

    override fun onBindViewHolder(holder: TransactionHistoryItem, position: Int) {
        holder.dateText.text = listTransactionHistory[position].date
        listTransactionHistory[position].listTransaction.forEach {
            val transactionCard = LayoutInflater.from(holder.itemView.context).inflate(R.layout.transaction_card_view, holder.linearLayout, false)
            val imageTransaction: ImageView = transactionCard.findViewById(R.id.statusTransactionIcon)
            val transactionObject: TextView = transactionCard.findViewById(R.id.transactionObject)
            val location: TextView = transactionCard.findViewById(R.id.location)
            val nominal: TextView = transactionCard.findViewById(R.id.nominalTransaction)

            imageTransaction.setImageResource(it.icon)
            transactionObject.text = it.title
            location.text = it.location
            nominal.text = it.nominal
            nominal.setTextColor(ContextCompat.getColor(holder.itemView.context, it.colorText))

            holder.linearLayout.addView(transactionCard)
        }
    }
}
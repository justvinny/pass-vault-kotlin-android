package com.vinsonb.password.manager.kotlin.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.models.Credit
import com.vinsonb.password.manager.kotlin.utilities.Constants.Credits.CREDITS

class CreditAdapter(
    private val context: Context,
    private val dataset: List<Credit> = CREDITS
) : RecyclerView.Adapter<CreditAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.credit_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewCreditTitle.text = dataset[position].title

        holder.iconOpenLink.setOnClickListener {
            openWebPage(dataset[position].url)
        }
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    private fun openWebPage(url: String) {
        val webPage: Uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, webPage)
        context.startActivity(intent)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewCreditTitle: TextView
        val iconOpenLink: ImageView

        init {
            textViewCreditTitle = view.findViewById(R.id.text_credit_title)
            iconOpenLink = view.findViewById(R.id.icon_open_link)
        }
    }
}
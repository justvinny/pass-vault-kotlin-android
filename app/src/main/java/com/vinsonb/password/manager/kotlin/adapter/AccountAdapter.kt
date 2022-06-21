package com.vinsonb.password.manager.kotlin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.database.enitities.Account
import com.vinsonb.password.manager.kotlin.utilities.ClipboardUtilities.copyToClipboard

private const val CLIP_LABEL = "Account Password"

class AccountAdapter(
    private var accountList: MutableList<Account> = mutableListOf()
) : RecyclerView.Adapter<AccountAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.account_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewPlatform.text = accountList[position].platform
        holder.textViewUsername.text = accountList[position].username

        holder.iconMaximizeAccount.setOnClickListener {
            // TODO Ticket for Individual Accounts
        }

        holder.iconCopy.setOnClickListener {
            copyToClipboard(
                holder.iconCopy.context,
                CLIP_LABEL,
                accountList[position].password,
                message = " password for ${accountList[position].username} "
            )
        }
    }

    override fun getItemCount(): Int {
        return accountList.size
    }

    fun updateAccountList(accountList: List<Account>) {
        this.accountList.clear()
        this.accountList.addAll(accountList)
        notifyDataSetChanged() // TODO Optimise for performance
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewPlatform: TextView
        val textViewUsername: TextView
        val iconCopy: ImageView
        val iconMaximizeAccount: ImageView

        init {
            textViewPlatform = view.findViewById(R.id.text_view_platform)
            textViewUsername = view.findViewById(R.id.text_view_username)
            iconCopy = view.findViewById(R.id.icon_copy_password)
            iconMaximizeAccount = view.findViewById(R.id.icon_maximize_account)
        }
    }
}
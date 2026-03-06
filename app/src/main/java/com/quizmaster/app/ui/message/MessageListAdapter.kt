package com.quizmaster.app.ui.message

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.quizmaster.app.data.local.entity.MessageEntity
import com.quizmaster.app.databinding.ItemMessageBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessageListAdapter : ListAdapter<MessageEntity, MessageListAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(private val b: ItemMessageBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: MessageEntity) {
            b.tvSubject.text = item.subject
            b.tvSender.text = "From: ${item.senderDisplayName}"
            b.tvPreview.text = item.content.take(80) + if (item.content.length > 80) "…" else ""
            val sdf = SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault())
            b.tvTimestamp.text = sdf.format(Date(item.timestamp))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<MessageEntity>() {
            override fun areItemsTheSame(a: MessageEntity, b: MessageEntity) = a.messageId == b.messageId
            override fun areContentsTheSame(a: MessageEntity, b: MessageEntity) = a == b
        }
    }
}

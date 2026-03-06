package com.quizmaster.app.ui.instructor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.quizmaster.app.data.local.entity.InstructorEntity
import com.quizmaster.app.databinding.ItemInstructorBinding

class InstructorListAdapter(
    private val onSelect: (InstructorEntity) -> Unit
) : ListAdapter<InstructorEntity, InstructorListAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(private val b: ItemInstructorBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: InstructorEntity) {
            b.tvName.text = "${item.firstName} ${item.lastName}"
            b.btnSelect.setOnClickListener { onSelect(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemInstructorBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<InstructorEntity>() {
            override fun areItemsTheSame(a: InstructorEntity, b: InstructorEntity) = a.instructorId == b.instructorId
            override fun areContentsTheSame(a: InstructorEntity, b: InstructorEntity) = a == b
        }
    }
}

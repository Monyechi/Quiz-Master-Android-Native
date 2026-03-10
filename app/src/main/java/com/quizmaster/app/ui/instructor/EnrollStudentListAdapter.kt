package com.quizmaster.app.ui.instructor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.quizmaster.app.data.local.entity.StudentEntity
import com.quizmaster.app.databinding.ItemEnrollStudentBinding

class EnrollStudentListAdapter(
    private val onAssign: (StudentEntity) -> Unit
) : ListAdapter<StudentEntity, EnrollStudentListAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(private val binding: ItemEnrollStudentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StudentEntity) {
            binding.tvName.text = "${item.firstName} ${item.lastName}"
            binding.tvDisplayName.text = "Display Name: ${item.displayName}"
            binding.tvGrade.text = "Grade: ${item.grade}"
            binding.btnAssign.setOnClickListener { onAssign(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEnrollStudentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<StudentEntity>() {
            override fun areItemsTheSame(oldItem: StudentEntity, newItem: StudentEntity): Boolean {
                return oldItem.studentId == newItem.studentId
            }

            override fun areContentsTheSame(oldItem: StudentEntity, newItem: StudentEntity): Boolean {
                return oldItem == newItem
            }
        }
    }
}

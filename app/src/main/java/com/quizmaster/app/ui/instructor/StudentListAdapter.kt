package com.quizmaster.app.ui.instructor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.quizmaster.app.data.local.entity.StudentEntity
import com.quizmaster.app.databinding.ItemStudentBinding

class StudentListAdapter(
    private val onMessageClick: (StudentEntity) -> Unit
) : ListAdapter<StudentEntity, StudentListAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(private val b: ItemStudentBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: StudentEntity) {
            b.tvName.text = "${item.firstName} ${item.lastName}"
            b.tvDisplayName.text = item.displayName
            b.tvGrade.text = "Grade: ${item.grade}"
            b.btnMessage.setOnClickListener { onMessageClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemStudentBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<StudentEntity>() {
            override fun areItemsTheSame(a: StudentEntity, b: StudentEntity) = a.studentId == b.studentId
            override fun areContentsTheSame(a: StudentEntity, b: StudentEntity) = a == b
        }
    }
}

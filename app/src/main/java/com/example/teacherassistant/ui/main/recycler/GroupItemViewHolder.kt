package com.example.teacherassistant.ui.main.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.teacherassistant.common.Group
import com.example.teacherassistant.common.OpenEmailWindowListener
import com.example.teacherassistant.common.OpenNextFragmentListener
import com.example.teacherassistant.databinding.GroupNoteItemBinding

class GroupItemViewHolder(
    private val binding: GroupNoteItemBinding,
    private val listener: OpenNextFragmentListener,
    private val dialogWindowListener: OpenEmailWindowListener
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(group: Group, role: String) {
        binding.name.text = group.name
        binding.title.text = group.title
        binding.root.setOnClickListener {
            listener.openNextFragment(group.id)
        }
        binding.addStudent.setOnClickListener {
            dialogWindowListener.openEmailWindow(group.id, group.title, group.name)
        }
        if (role == "Student") {
            binding.addStudent.apply {
                isClickable = false
                visibility = View.INVISIBLE
            }
        } else {
            binding.addStudent.apply {
                isClickable = true
                visibility = View.VISIBLE
            }
        }
    }
}
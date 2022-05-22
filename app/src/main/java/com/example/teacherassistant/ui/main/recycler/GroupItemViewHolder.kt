package com.example.teacherassistant.ui.main.recycler

import androidx.recyclerview.widget.RecyclerView
import com.example.teacherassistant.common.Group
import com.example.teacherassistant.common.OpenNextFragmentListener
import com.example.teacherassistant.databinding.GroupNoteItemBinding

class GroupItemViewHolder(
    private val binding: GroupNoteItemBinding,
    private val listener: OpenNextFragmentListener
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(group: Group) {
        binding.name.text = group.name
        binding.title.text = group.title
        binding.root.setOnClickListener {
            listener.openNextFragment(group.name)
        }
    }
}
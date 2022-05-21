package com.example.teacherassistant.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.teacherassistant.R
import com.example.teacherassistant.databinding.FragmentEntryBinding

class EntryFragment : Fragment() {

    private lateinit var binding: FragmentEntryBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEntryBinding.inflate(inflater,container,false)
        return binding.root
    }

}
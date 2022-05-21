package com.example.teacherassistant

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.teacherassistant.databinding.MainActivityBinding
import com.example.teacherassistant.ui.main.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding
    private val viewModel: MainActivityViewModel by viewModels<MainActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (viewModel.getUserState()) {
            findNavController(R.id.my_host_activity).navigate(R.id.mainFragment)
        } else {
            findNavController(R.id.my_host_activity).navigate(R.id.signInFragment)
        }
    }
}
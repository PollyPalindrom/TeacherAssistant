package com.example.teacherassistant.ui.main.mainActivity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.teacherassistant.R
import com.example.teacherassistant.common.Constants
import com.example.teacherassistant.common.OpenNextFragmentListener
import com.example.teacherassistant.databinding.MainActivityBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OpenNextFragmentListener {

    private lateinit var binding: MainActivityBinding
    private val viewModel: MainActivityViewModel by viewModels<MainActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)

        setContentView(binding.root)
        if (viewModel.getUserState()) {
            viewModel.checkRole(this, Constants.COLLECTION_FIRST_PATH)
        } else {
            findNavController(R.id.my_host_activity).navigate(R.id.signInFragment)
        }
    }

    override fun openNextFragment(path: String) {
        val bundle = Bundle()
        bundle.putString(Constants.ROLE, path)
        findNavController(R.id.my_host_activity).navigate(R.id.mainFragment, bundle)
    }
}
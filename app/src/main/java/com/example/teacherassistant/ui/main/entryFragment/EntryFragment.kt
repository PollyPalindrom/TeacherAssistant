package com.example.teacherassistant.ui.main.entryFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.teacherassistant.R

class EntryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                EntryImage()
            }
        }
    }

    @Composable
    fun EntryImage() {
        ConstraintLayout {
            val image = createRef()
            Image(
                painter = painterResource(R.drawable.ic_baseline_group_24),
                contentDescription = "Entry Image",
                modifier = Modifier.constrainAs(image) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )

        }
    }

}
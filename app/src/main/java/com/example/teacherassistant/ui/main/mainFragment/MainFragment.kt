package com.example.teacherassistant.ui.main.mainFragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.teacherassistant.R
import com.example.teacherassistant.common.Constants
import com.example.teacherassistant.common.Group
import com.example.teacherassistant.common.GroupsState
import com.example.teacherassistant.common.OpenNextFragmentListener
import com.example.teacherassistant.ui.main.firebaseService.FirebaseService
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment(), OpenNextFragmentListener {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                GroupsScreen()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FirebaseService.sharedPref =
            requireActivity().getSharedPreferences(
                Constants.SHARED_PREF_NAME,
                Context.MODE_PRIVATE
            )
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            FirebaseService.token = it
            viewModel.setNewToken(
                it,
                Constants.COLLECTION_FIRST_PATH,
                Constants.COLLECTION_SECOND_PATH,
                Constants.COLLECTION_THIRD_PATH_STUDENTS
            )
        }
        viewModel.subscribeGroupListChanges(
            Constants.COLLECTION_FIRST_PATH,
            Constants.COLLECTION_SECOND_PATH
        )
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            })
    }

    @Composable
    fun GroupsScreen() {
        val groupEntities: GroupsState? by viewModel.groupsListOpen.collectAsState()
        groupEntities?.groups?.let { Groups(groups = it) }
    }

    @Composable
    fun Groups(groups: List<Group>) {
        var groupDialog by remember { mutableStateOf(false) }

        val onClick = { groupDialog = false }
        Scaffold(floatingActionButton = {
            if (arguments?.getString(Constants.ROLE) == Constants.TEACHER) {
                FloatingActionButton(onClick = { groupDialog = !groupDialog }) {
                    Icon(Icons.Filled.Add, "Add group")
                }
                if (groupDialog) {
                    GroupDialog(onClick = onClick, openDialog = { name, title ->
                        groupDialog = false
                        if (name.isNotBlank() && title.isNotBlank()
                        ) {
                            viewModel.createGroup(
                                Constants.COLLECTION_FIRST_PATH,
                                Constants.COLLECTION_SECOND_PATH,
                                name,
                                title
                            )
                        } else {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.textErrorMessage),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    })
                }
            }
        }) {
            LazyColumn(modifier = Modifier.padding(vertical = 4.dp)) {
                items(groups) { group ->
                    GroupsList(
                        name = group.name,
                        title = group.title,
                        id = group.id,
                        group.students
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun GroupsList(name: String, title: String, id: String, students: String) {

        var expanded by remember { mutableStateOf(false) }
        var emailDialog by remember { mutableStateOf(false) }

        Surface(
            color = MaterialTheme.colors.primary,
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
            onClick = { openNextFragment(id) }
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(12.dp)
                ) {
                    Text(text = name)
                    if (expanded) {
                        Text(
                            text = title
                        )
                        Text(text = students)
                    }
                }
                if (arguments?.getString(Constants.ROLE) == Constants.TEACHER) {
                    IconButton(onClick = { emailDialog = !emailDialog }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_baseline_add_24),
                            contentDescription = ""
                        )
                    }
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = if (expanded) {
                            stringResource(R.string.show_less)
                        } else {
                            stringResource(R.string.show_more)
                        }

                    )
                }
            }
        }
        if (emailDialog) {
            EmailDialog(
                onClick = { emailDialog = false }, openDialog = { text: String ->
                    emailDialog = false
                    if (text.isNotBlank()) {
                        viewModel.addStudent(
                            text,
                            Constants.COLLECTION_FIRST_PATH,
                            Constants.COLLECTION_SECOND_PATH,
                            id,
                            Constants.COLLECTION_THIRD_PATH_STUDENTS,
                            title, name
                        )
                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.textErrorMessage),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
        }
    }

    @Composable
    fun GroupDialog(onClick: () -> Unit, openDialog: (name: String, title: String) -> Unit) {
        var name by rememberSaveable { mutableStateOf("") }
        var title by rememberSaveable { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = onClick,
            title = {
                Text(text = getString(R.string.groupWindowMessage))
            },
            text = {
                Column {
                    TextField(
                        value = name,
                        onValueChange = {
                            name = it
                        }, label = {
                            Text(text = "Name")
                        }
                    )
                    TextField(
                        value = title,
                        onValueChange = {
                            title = it
                        }, label = {
                            Text(text = "Title")
                        }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { openDialog(name, title) }
                ) {
                    Text(getString(R.string.positiveButton))
                }
            }
        )
    }

    override fun openNextFragment(path: String) {
        if (viewModel.checkState()) {
            val bundle = Bundle()
            bundle.putString(Constants.GROUP_ID, path)
            bundle.putString(
                Constants.ROLE,
                arguments?.getString(Constants.ROLE)
            )
            findNavController().navigate(R.id.notesFragment, bundle)
        }
    }

    @Composable
    fun EmailDialog(onClick: () -> Unit, openDialog: (text: String) -> Unit) {
        var text by rememberSaveable { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = onClick,
            title = {
                Text(text = getString(R.string.email))
            },
            text = {
                TextField(
                    value = text,
                    onValueChange = {
                        text = it
                    }
                )
            },
            confirmButton = {
                Button(
                    onClick = { openDialog(text) }
                ) {
                    Text(getString(R.string.positiveButton))
                }
            }
        )
    }
}
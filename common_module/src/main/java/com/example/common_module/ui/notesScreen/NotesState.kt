package com.example.common_module.ui.notesScreen

data class NotesState(val notes: List<Note> = emptyList(), val error: String? = null)
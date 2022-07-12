package com.example.teacherassistant.ui.main.notesScreen

data class NotesState(val notes: List<Note> = emptyList(), val error: String? = null)
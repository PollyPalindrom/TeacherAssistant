package com.example.teacherassistant.common

data class NotesState(val notes: List<Note> = emptyList(), val error: String?= null)
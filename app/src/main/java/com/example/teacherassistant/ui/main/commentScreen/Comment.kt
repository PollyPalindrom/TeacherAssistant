package com.example.teacherassistant.ui.main.commentScreen

data class Comment(
    val time: String,
    val text: String,
    val author: String,
    val isReply: Boolean = false,
    val addressee: String = ""
)

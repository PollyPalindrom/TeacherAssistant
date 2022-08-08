package com.example.common_module.ui.commentScreen

data class Comment(
    val time: String,
    val text: String,
    val author: String,
    val isReply: Boolean = false,
    val addressee: String = ""
)

package com.pablo.tfg_chatochat.model

data class Chat(
    val chatId: String = "",
    val title: String = "",
    val lastMessage: String = "",
    val timestamp: Long = 0L,
    val participants: List<String> = emptyList()
)

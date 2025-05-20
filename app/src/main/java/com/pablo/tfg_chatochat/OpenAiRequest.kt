package com.pablo.tfg_chatochat

data class OpenAiRequest(
    val model: String = "gpt-3.5-turbo",
    val messages: List<Message>
)
//
data class Message(
    val role: String,
    val content: String
)
//
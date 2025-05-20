package com.pablo.tfg_chatochat


data class OpenAiResponse(
    val id: String? = null,
    val `object`: String? = null,
    val created: Long = 0,
    val model: String? = null,
    val choices: List<Choice> = emptyList()
)


data class Choice(
    val index: Int = 0,
    val message: Message,
    val finish_reason: String? = null
)

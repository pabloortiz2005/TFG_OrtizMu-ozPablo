package com.pablo.tfg_chatochat.DataClass

data class ChatModel(
    val chatId: String = "",
    val uidEmisor: String = "",
    val uidReceptor: String = "",
    val ultimoMensaje: String = "",
    val timestampUltimoMensaje: Long = 0,
    val participants: List<String> = listOf(),
    val titulos: Map<String, String> = mapOf()  // <UID, Título que ve ese usuario>
)

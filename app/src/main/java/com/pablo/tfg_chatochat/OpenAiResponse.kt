data class OpenAiResponse(
    val choices: List<Choice>
)

data class Choice(
     val message: Message
)

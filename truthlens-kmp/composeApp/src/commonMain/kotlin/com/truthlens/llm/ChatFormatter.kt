package com.truthlens.llm

enum class Role {
    SYSTEM, USER, ASSISTANT
}

data class Message(val role: Role, val content: String)

object ChatFormatter {
    private const val BOS_TOKEN = "<|bos|>" // Usually the model config defines this, but we can leave it empty or default if needed. 

    fun format(messages: List<Message>, addGenerationPrompt: Boolean = true): String {
        val builder = StringBuilder()
        
        // Let's just use empty bos for now if it's implicitly handled, or add it.
        // builder.append(BOS_TOKEN)

        for (message in messages) {
            val roleStr = when (message.role) {
                Role.SYSTEM -> "system"
                Role.USER -> "user"
                Role.ASSISTANT -> "assistant"
            }
            builder.append("<|im_start|>").append(roleStr).append("\n")
            builder.append(message.content)
            builder.append("<|im_end|>\n")
        }

        if (addGenerationPrompt) {
            builder.append("<|im_start|>assistant\n")
        }

        return builder.toString()
    }
}

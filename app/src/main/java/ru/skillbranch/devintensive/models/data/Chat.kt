package ru.skillbranch.devintensive.models.data

import ru.skillbranch.devintensive.extensions.shortFormat
import ru.skillbranch.devintensive.models.BaseMessage
import ru.skillbranch.devintensive.models.ImageMessage
import ru.skillbranch.devintensive.models.TextMessage
import ru.skillbranch.devintensive.utils.Utils
import java.util.*

data class Chat(
    val id: String,
    val title: String,
    val members: List<User> = listOf(),
    var messages: MutableList<BaseMessage> = mutableListOf(),
    var isArchive: Boolean = false
) {

    fun unreadableMessageCount(): Int {
        var count = 0
        for (message in messages) {
            if (!message.isRead) {
                count++
            }
        }
        return count
    }

    fun lastMessageDate(): Date? {
        if (messages.isEmpty()) {
            return null
        }
        var lastDate = Date()
        for (message in messages) {
            if (lastDate.after(message.date)) {
                lastDate = message.date
            }
        }
        return lastDate
    }

    fun lastMessageShort(): Pair<String?, String?> {
        if (messages.isEmpty()) {
            return "Сообщений нет" to ""
        }

        val message = messages[0]
        return if (message is TextMessage) {
            message.text to message.from.firstName
        } else {
            "${message.from.firstName} - отправил фото" to message.from.firstName
        }
    }

    private fun isSingle(): Boolean = members.size == 1

    fun toChatItem(): ChatItem {
        return if (isSingle()) {
            val user = members.first()
            ChatItem(
                id = id,
                avatar = user.avatar,
                initials = Utils.toInitials(user.firstName, user.lastName) ?: "??",
                title = "${user.firstName ?: ""} ${user.lastName ?: ""}",
                shortDescription = lastMessageShort().first,
                messageCount = unreadableMessageCount(),
                lastMessageDate = lastMessageDate()?.shortFormat(),
                isOnline = user.isOnline
            )
        } else {
            ChatItem(
                id = id,
                avatar = null,
                initials = "",
                title = title,
                shortDescription = lastMessageShort().first,
                messageCount = unreadableMessageCount(),
                lastMessageDate = lastMessageDate()?.shortFormat(),
                isOnline = false,
                chatType = ChatType.GROUP,
                author = lastMessageShort().second
            )
        }
    }

}

enum class ChatType {
    SINGLE,
    GROUP,
    ARCHIVE
}

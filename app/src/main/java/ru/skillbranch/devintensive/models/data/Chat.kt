package ru.skillbranch.devintensive.models.data

import ru.skillbranch.devintensive.extensions.format
import ru.skillbranch.devintensive.extensions.shortFormat
import ru.skillbranch.devintensive.models.BaseMessage
import ru.skillbranch.devintensive.utils.Utils
import java.util.*

class Chat(
    val id: String,
    val title: String,
    val members: List<User> = listOf(),
    var messages: MutableList<BaseMessage> = mutableListOf(),
    var isArchive: Boolean = false
) {

    private fun unreadableMessageCount(): Int {
        // TODO
        return (0..20).random()
    }

    private fun lastMessageDate(): Date? {
        // TODO
        return Date()
    }

    private fun lastMessageShort(): String {
        // TODO
        return "Сообщений нет"
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
                shortDescription = lastMessageShort(),
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
                shortDescription = lastMessageShort(),
                messageCount = unreadableMessageCount(),
                lastMessageDate = lastMessageDate()?.shortFormat(),
                isOnline = false
            )
        }
    }

}

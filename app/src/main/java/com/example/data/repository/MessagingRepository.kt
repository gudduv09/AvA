package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entity.MessageEntity
import com.example.data.mock.DemoDataProvider
import com.example.domain.model.Conversation
import com.example.domain.model.DirectMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class MessagingRepository(private val database: AppDatabase) {

    private val messageDao = database.messageDao()
    private val userDao = database.userDao()

    fun getConversations(): Flow<List<Conversation>> {
        return combine(messageDao.getAllMessages(), userDao.getAllUsers()) { messages, users ->
            val userMap = users.associateBy { it.id }
            val groups = messages.groupBy { it.conversationId }
            groups.mapNotNull { (convId, convMessages) ->
                val lastMsg = convMessages.maxByOrNull { it.timestamp } ?: return@mapNotNull null
                val otherUserId = if (lastMsg.senderId == DemoDataProvider.currentUser.id) lastMsg.recipientId else lastMsg.senderId
                val otherUser = userMap[otherUserId]?.toDomain() ?: DemoDataProvider.creators.find { it.id == otherUserId } ?: return@mapNotNull null
                val unread = convMessages.count { !it.isRead && !it.isFromCurrentUser }
                Conversation(
                    id = convId,
                    user = otherUser,
                    lastMessage = lastMsg.text,
                    lastTimestamp = lastMsg.timestamp,
                    unreadCount = unread
                )
            }.sortedByDescending { it.lastTimestamp }
        }
    }

    fun getMessagesForConversation(convId: String): Flow<List<DirectMessage>> {
        return messageDao.getMessagesForConversation(convId).map { entities ->
            entities.map { entity ->
                DirectMessage(
                    id = entity.id,
                    senderId = entity.senderId,
                    recipientId = entity.recipientId,
                    text = entity.text,
                    timestamp = entity.timestamp,
                    isFromCurrentUser = entity.isFromCurrentUser,
                    isRead = entity.isRead
                )
            }
        }
    }

    suspend fun sendMessage(conversationId: String, recipientId: String, text: String) {
        val msg = MessageEntity(
            id = "msg_" + System.currentTimeMillis(),
            conversationId = conversationId,
            senderId = DemoDataProvider.currentUser.id,
            recipientId = recipientId,
            text = text,
            timestamp = System.currentTimeMillis(),
            isFromCurrentUser = true,
            isRead = true
        )
        messageDao.insertMessage(msg)
    }
}

package com.example.ui.screens.inbox

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.InteractionRepository
import com.example.data.repository.MessagingRepository
import com.example.domain.model.Conversation
import com.example.domain.model.DirectMessage
import com.example.domain.model.NotificationItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class InboxTab {
    NOTIFICATIONS,
    MESSAGES
}

class InboxViewModel(
    private val interactionRepository: InteractionRepository,
    private val messagingRepository: MessagingRepository
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(InboxTab.NOTIFICATIONS)
    val selectedTab: StateFlow<InboxTab> = _selectedTab.asStateFlow()

    val notifications: StateFlow<List<NotificationItem>> = interactionRepository.getNotifications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val conversations: StateFlow<List<Conversation>> = messagingRepository.getConversations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Chat State
    private val _activeConversation = MutableStateFlow<Conversation?>(null)
    val activeConversation: StateFlow<Conversation?> = _activeConversation.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<DirectMessage>>(emptyList())
    val chatMessages: StateFlow<List<DirectMessage>> = _chatMessages.asStateFlow()

    fun selectTab(tab: InboxTab) {
        _selectedTab.value = tab
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            interactionRepository.markAllNotificationsAsRead()
        }
    }

    fun openConversation(conv: Conversation) {
        _activeConversation.value = conv
        viewModelScope.launch {
            messagingRepository.getMessagesForConversation(conv.id).collect {
                _chatMessages.value = it
            }
        }
    }

    fun closeConversation() {
        _activeConversation.value = null
        _chatMessages.value = emptyList()
    }

    fun sendMessage(text: String) {
        val conv = _activeConversation.value ?: return
        viewModelScope.launch {
            messagingRepository.sendMessage(conv.id, conv.user.id, text)
        }
    }
}

package com.example.doccur.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doccur.entities.Notification
import com.example.doccur.repositories.NotificationRepository
import com.example.doccur.websocket.NotificationWebSocketClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val repository: NotificationRepository,
    private val wsBaseUrl: String = "ws://172.20.10.4:8000" // Remplacer par l'URL de votre backend
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var wsClient: NotificationWebSocketClient? = null

    fun connectWebSocket(userId: Int, userType: String) {
        wsClient = NotificationWebSocketClient(wsBaseUrl, userType, userId)

        viewModelScope.launch {
            wsClient?.notificationFlow?.collect { notification ->
                // Ajouter la nouvelle notification à la liste
                val currentList = _notifications.value.toMutableList()
                // Vérifier si la notification n'existe pas déjà
                if (currentList.none { it.id == notification.id }) {
                    currentList.add(0, notification) // Ajouter au début de la liste
                    _notifications.value = currentList
                }
            }
        }

        wsClient?.connect()
    }

    fun disconnectWebSocket() {
        wsClient?.disconnect()
        wsClient = null
    }

    fun fetchNotifications(userId: Int, userType: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = repository.getNotifications(userId, userType)
                _notifications.value = result
                _error.value = null

                // Connecter au WebSocket après avoir récupéré les notifications existantes
                connectWebSocket(userId, userType)
            } catch (e: Exception) {
                _error.value = "Failed to load notifications: ${e.message}"
                _notifications.value = emptyList()
            } finally {
                _loading.value = false
            }
        }
    }

    fun markAsRead(notificationId: Int) {
        viewModelScope.launch {
            try {
                val success = repository.markNotificationAsRead(notificationId)
                if (success) {
                    // Mettre à jour la liste locale des notifications
                    _notifications.value = _notifications.value.map { notification ->
                        if (notification.id == notificationId) {
                            notification.copy(isRead = true)
                        } else {
                            notification
                        }
                    }

                    // Informer le serveur via WebSocket que la notification est lue
                    wsClient?.markNotificationAsRead(notificationId)
                }
            } catch (e: Exception) {
                _error.value = "Failed to mark notification as read: ${e.message}"
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disconnectWebSocket()
    }
}








//package com.example.doccur.viewmodels
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.doccur.entities.Notification
//import com.example.doccur.repositories.NotificationRepository
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//
//class NotificationViewModel(private val repository: NotificationRepository) : ViewModel() {
//
//    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
//    val notifications: StateFlow<List<Notification>> = _notifications
//
//    private val _loading = MutableStateFlow(false)
//    val loading: StateFlow<Boolean> = _loading
//
//    private val _error = MutableStateFlow<String?>(null)
//    val error: StateFlow<String?> = _error
//
//    fun fetchNotifications(userId: Int, userType: String) {
//        viewModelScope.launch {
//            _loading.value = true
//            try {
//                val result = repository.getNotifications(userId, userType)
//                _notifications.value = result
//                _error.value = null
//            } catch (e: Exception) {
//                _error.value = "Failed to load notifications: ${e.message}"
//                _notifications.value = emptyList()
//            } finally {
//                _loading.value = false
//            }
//        }
//    }
//
//    fun markAsRead(notificationId: Int) {
//        viewModelScope.launch {
//            try {
//                val success = repository.markNotificationAsRead(notificationId)
//                if (success) {
//                    // Update the local notification list to reflect the change
//                    _notifications.value = _notifications.value.map { notification ->
//                        if (notification.id == notificationId) {
//                            notification.copy(isRead = true)
//                        } else {
//                            notification
//                        }
//                    }
//
//                    // You can also notify the UI that the notification has been marked as read
//                    _notifications.value = _notifications.value.toList()
//                }
//            } catch (e: Exception) {
//                _error.value = "Failed to mark notification as read: ${e.message}"
//            }
//        }
//    }
//
//}
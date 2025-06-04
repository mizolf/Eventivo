package hr.mcesnik.eventivo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _userId = MutableStateFlow<String?>(auth.currentUser?.uid)
    val userId: StateFlow<String?> = _userId

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _authError.value = null

            try {
                auth.signInWithEmailAndPassword(email, password).await()
                _userId.value = auth.currentUser?.uid
                onSuccess()
            } catch (e: Exception) {
                _authError.value = e.message ?: "Authentication failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _authError.value = null

            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                _userId.value = auth.currentUser?.uid
                onSuccess()
            } catch (e: Exception) {
                _authError.value = e.message ?: "Registration failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        auth.signOut()
        _userId.value = null
    }
}
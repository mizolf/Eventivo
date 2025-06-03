package hr.mcesnik.eventivo.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _userId = MutableStateFlow<String?>(auth.currentUser?.uid)
    val userId: StateFlow<String?> = _userId

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading


    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        _isLoading.value = true
        _authError.value = null

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    _userId.value = auth.currentUser?.uid
                    onSuccess()
                } else {
                    _authError.value = task.exception?.message ?: "Authentication failed"
                }
            }
    }

    fun register(email: String, password: String, onSuccess: () -> Unit) {
        _isLoading.value = true
        _authError.value = null

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    _authError.value = task.exception?.message ?: "Registration failed"
                }
            }
    }

    fun logout() {
        auth.signOut()
        _userId.value = null
    }
}
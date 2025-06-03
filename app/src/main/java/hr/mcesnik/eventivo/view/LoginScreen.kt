package hr.mcesnik.eventivo.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import hr.mcesnik.eventivo.viewmodel.AuthViewModel


@Composable
fun LoginScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    val isLoading by authViewModel.isLoading.collectAsState()
    val authError by authViewModel.authError.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            Text(
                text = "Welcome to Eventivo",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333),
                letterSpacing = 1.sp
            )

            Text(
                text = "Please sign in",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333),
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(16.dp))


            OutlinedTextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text("Email", color = Color(0xFF666666)) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = "Email",
                        tint = Color(0xFF666666)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF333333),
                    unfocusedBorderColor = Color(0xFFCCCCCC),
                    focusedLabelColor = Color(0xFF333333),
                    unfocusedLabelColor = Color(0xFF666666)
                ),
                shape = RoundedCornerShape(8.dp)
            )


            OutlinedTextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text("Password", color = Color(0xFF666666)) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = "Password",
                        tint = Color(0xFF666666)
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF333333),
                    unfocusedBorderColor = Color(0xFFCCCCCC),
                    focusedLabelColor = Color(0xFF333333),
                    unfocusedLabelColor = Color(0xFF666666)
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))


            val annotatedString = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color(0xFF666666))) {
                    append("Don't have an account? ")
                }
                pushStringAnnotation(tag = "register", annotation = "register")
                withStyle(style = SpanStyle(color = Color(0xFF7C4DFF), fontWeight = FontWeight.Medium)) {
                    append("Register Here")
                }
                pop()
            }

            ClickableText(
                text = annotatedString,
                onClick = { offset ->
                    annotatedString.getStringAnnotations(
                        tag = "register",
                        start = offset,
                        end = offset
                    ).firstOrNull()?.let {
                        // Navigate to register screen
                        navController.navigate("register")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                style = androidx.compose.ui.text.TextStyle(
                    textAlign = TextAlign.Start,
                    fontSize = 14.sp
                )
            )

            Spacer(modifier = Modifier.height(16.dp))


            Button(
                onClick = {
                    authViewModel.login(email.value, password.value){
                        navController.navigate("home"){
                            popUpTo("login") { inclusive = true}
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF333333)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "LOGIN",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp
                    )
                }
            }
            authError?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
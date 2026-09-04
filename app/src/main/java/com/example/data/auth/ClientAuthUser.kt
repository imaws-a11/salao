package com.example.data.auth

data class ClientAuthUser(
    val uid: String,
    val email: String,
    val displayName: String,
    val phoneNumber: String = "",
    val isAnonymous: Boolean = false,
    val isEmailVerified: Boolean = false,
    val providerId: String = "firebase"
)

package com.example.myapplication.users

data class User(
    var uid: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var email: String = "",
    var phone: String? = null,
    var caregiver: String = "",
    var password: String = "",
) {
    override fun toString(): String {
        return "$firstName $lastName"
    }
}

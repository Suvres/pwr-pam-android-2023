package com.example.myapplication.log

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.users.UserRepository
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : Activity(), View.OnClickListener {

    private var userRepository = UserRepository()

    private val fbAuth = FirebaseAuth.getInstance()
    private val LOGIN_DEBUG = "LOGIN_ACTIVITY_DEBUG"

    private val currentUser = UserRepository.getCurrentUserID()

    private var registerTextView: TextView? = null
    private var signInButton: Button? = null
    private var loginEditText: EditText? = null
    private var passwordEditText: EditText? = null

    private var textInputLayoutEmail: TextInputLayout? = null
    private var textInputLayoutPassword: TextInputLayout? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (fbAuth.currentUser != null) {
            val intent: Intent = Intent(applicationContext, MainActivity::class.java).apply {
                flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            startActivity(intent)
        }

        init()

        registerTextView?.setOnClickListener(this)
        signInButton?.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.textSingUp -> setupRegistrationClick()
            R.id.cirLoginButton -> setupLoginClick()
        }
    }

    private fun setupRegistrationClick() {
        val intent = Intent(this, RegistrationActivity::class.java)
        startActivity(intent)
    }

    private fun setupLoginClick() {
        val email = loginEditText!!.text?.trim().toString()
        val password = passwordEditText!!.text?.trim().toString()


        fun isEmpty(checkFiled: String, titleFiled: String): Boolean { // nested functions
            if (checkFiled.isNotEmpty()) {
                return true
            }
            Toast.makeText(applicationContext, "Wprowadź $titleFiled", Toast.LENGTH_SHORT).show()
            return false
        }

        if (isEmpty(email, "email") && isEmpty(password, "hasło")) {
            UserRepository.auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { authRes ->
                    if (authRes.user !== null) {
                        createUserIfNotExist(authRes.user!!, password)

                        val intent: Intent =
                            Intent(applicationContext, MainActivity::class.java).apply {
                                flags =
                                    (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            }
                        startActivity(intent)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(applicationContext, "Niepoprawne dane", Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }


    private fun validEmail(checkFiled: String, textInputLayout: TextInputLayout?): Boolean {
        if(!isEmpty(checkFiled)) {
            changeErrorTextAttributes(textInputLayout, "E-mail nie może być pusty.", true)
            return false
        }else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(checkFiled).matches()) {
            changeErrorTextAttributes(textInputLayout, "Zły format e-mailu.", true)
            return false
        }
        textInputLayout?.isErrorEnabled = false
        return true
    }

    private fun validPassword(checkFiled: String, textInputLayout: TextInputLayout?): Boolean {
        if(!isEmpty(checkFiled)) {
            changeErrorTextAttributes(textInputLayout, "Hasło nie może być puste.", true)
            return false
        }else if(checkFiled.length < 6) {
            changeErrorTextAttributes(textInputLayout, "Hasło musi składać się z minimum 6 znaków.", true)
            return false
        }
        textInputLayout?.isErrorEnabled = false
        return true
    }

    private fun changeErrorTextAttributes(textInputLayout: TextInputLayout?, massage: String, enabled: Boolean) {
        textInputLayout?.isErrorEnabled = enabled
        textInputLayout?.error = massage
    }

    private fun isEmpty(checkFiled: String): Boolean {
        if (checkFiled.isNotEmpty()) {
            return true
        }
        return false
    }





    private fun init() {
        registerTextView = findViewById(R.id.textSingUp)
        signInButton = findViewById(R.id.cirLoginButton)
        loginEditText = findViewById(R.id.editTextEmail)
        passwordEditText = findViewById(R.id.editTextPassword)
        textInputLayoutEmail = findViewById(R.id.tl_login_email)
        textInputLayoutPassword = findViewById(R.id.tl_login_password)
    }


    private fun createUserIfNotExist(firebaseUser: FirebaseUser, password: String) {
         userRepository.getUser(firebaseUser.uid).addOnSuccessListener {
            if(!it.exists()) {
                userRepository.createFromFirebaseUser(firebaseUser, password) {};
            }
        }.addOnFailureListener {
             Log.d(LOGIN_DEBUG, it.toString())
         }
    }
}
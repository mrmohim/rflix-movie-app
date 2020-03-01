package com.example.rflix.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.rflix.R
import com.example.rflix.utils.FireUtils
import com.example.rflix.utils.OtherUtils
import com.example.rflix.utils.SharedData
import com.google.firebase.auth.FirebaseAuth
import com.orhanobut.hawk.Hawk

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val toolbar: Toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Log In"

        val auth: FirebaseAuth = FirebaseAuth.getInstance()

        val btnSignIn = findViewById<Button>(R.id.btn_login)
        val btnSignUp = findViewById<Button>(R.id.btn_signup)
        val inputEmail = findViewById<EditText>(R.id.email)
        val inputPassword = findViewById<EditText>(R.id.password)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val btnResetPassword = findViewById<Button>(R.id.btn_reset_password)

        btnSignIn.setOnClickListener {
            OtherUtils.hideKeyboard(this)
            val email = inputEmail.text.toString().trim()
            val password = inputPassword.text.toString().trim()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill-up all the field!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (!task.isSuccessful) {
                        if (password.length < 6) {
                            inputPassword.error = "Password too short, enter minimum 6 characters!"
                        } else {
                            Toast.makeText(
                                this@LoginActivity, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        SharedData.uuid = auth.uid!!
                        FireUtils.getUserInfo(SharedData.uuid)
                        Hawk.put("login", SharedData.uuid)

                        SharedData.backFrom = "login"
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    progressBar.visibility = View.GONE
                }
        }

        btnResetPassword.setOnClickListener {
            SharedData.backFrom = "reset"
            val intent = Intent(this@LoginActivity, ResetPasswordActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnSignUp.setOnClickListener {
            SharedData.backFrom = "signup"
            val intent = Intent(this@LoginActivity, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        SharedData.backFrom = "login"
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        super.onBackPressed()
    }
}

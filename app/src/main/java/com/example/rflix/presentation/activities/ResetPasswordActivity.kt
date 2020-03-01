package com.example.rflix.presentation.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import com.example.rflix.R
import com.example.rflix.utils.SharedData
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_reset_password.*

class ResetPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Reset Password"

        val auth: FirebaseAuth = FirebaseAuth.getInstance()

        val inputEmail = findViewById<EditText>(R.id.email)
        val btnReset = findViewById<Button>(R.id.btn_reset_password)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val statusText = findViewById<TextView>(R.id.textDetails)

        btnReset.setOnClickListener {
            val email = inputEmail.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Enter your registered email id!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this) { task ->
                    Toast.makeText(
                        this@ResetPasswordActivity,
                        "createUserWithEmail:onComplete:" + task.isSuccessful,
                        Toast.LENGTH_SHORT
                    ).show()

                    if (task.isSuccessful) {
                        btnReset.visibility = View.GONE
                        textInputLayout.visibility = View.GONE
                        statusText.text = "Please check your email. We have sent an email with instructions to reset your password!"
                        Toast.makeText(
                            this@ResetPasswordActivity, "Success!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@ResetPasswordActivity, "Failed to send reset email!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    progressBar.visibility = View.GONE
                }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        SharedData.backFrom = "reset"
        val intent = Intent(this@ResetPasswordActivity, MainActivity::class.java)
        startActivity(intent)
        super.onBackPressed()
    }
}

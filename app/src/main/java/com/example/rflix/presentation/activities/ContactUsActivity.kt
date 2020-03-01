package com.example.rflix.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.rflix.R
import com.example.rflix.utils.FireUtils
import com.example.rflix.utils.OtherUtils
import com.example.rflix.utils.SharedData
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_contact_us.*


class ContactUsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Contact Us"

        val auth: FirebaseAuth = FirebaseAuth.getInstance()

        btnMessenger.setOnClickListener {
            SharedData.backFrom = "messenger"
            val intent = Intent(this@ContactUsActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnSend.setOnClickListener {
            OtherUtils.hideKeyboard(this)
            val email = email.text.toString().trim()
            val message = message.text.toString().trim()
            if (email.isEmpty() || message.isEmpty()) {
                Toast.makeText(this, "Please fill-up all the field!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            FireUtils.feedBack(auth, email, message)
            progressBar.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))
            progressBar.visibility = View.VISIBLE
            Handler().postDelayed({
                doneImage.visibility = View.VISIBLE
                feedbackStatus.text = "Thanks!\nYour feedback has been sent to\nthe rflix team!"
                feedbackStatus.textSize = 20F
                feedbackLayout.visibility = View.GONE
                progressBar.visibility = View.GONE
            }, 1000)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        SharedData.backFrom = "contact"
        val intent = Intent(this@ContactUsActivity, MainActivity::class.java)
        startActivity(intent)
        super.onBackPressed()
    }
}

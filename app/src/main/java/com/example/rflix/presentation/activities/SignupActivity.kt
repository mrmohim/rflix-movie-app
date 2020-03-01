package com.example.rflix.presentation.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
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
import com.example.rflix.utils.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.activity_signup.*
import java.io.ByteArrayOutputStream


class SignupActivity : AppCompatActivity() {
    private val REQUEST_IMAGE_CAPTURE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Sign Up"

        val auth: FirebaseAuth = FirebaseAuth.getInstance()

        val btnSignIn = findViewById<Button>(R.id.sign_in_button)
        val btnSignUp = findViewById<Button>(R.id.sign_up_button)
        val inputName = findViewById<EditText>(R.id.name)
        val inputEmail = findViewById<EditText>(R.id.email)
        val inputPassword = findViewById<EditText>(R.id.password)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val btnResetPassword = findViewById<Button>(R.id.btn_reset_password)

        btnSignUp.setOnClickListener {
            OtherUtils.hideKeyboard(this)
            val name = inputName.text.toString().trim()
            val email = inputEmail.text.toString().trim()
            val password = inputPassword.text.toString().trim()
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill-up all the field!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(
                    this,
                    "Password too short, enter minimum 6 characters!",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    Toast.makeText(
                        this@SignupActivity,
                        "createUserWithEmail:onComplete:" + task.isSuccessful,
                        Toast.LENGTH_SHORT
                    ).show()

                    if (!task.isSuccessful) {
                        Toast.makeText(
                            this@SignupActivity, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        FireUtils.signUp(auth, name, email)
                        Hawk.put("login", auth.uid)
                        SharedData.uuid = auth.uid.toString()

                        SharedData.backFrom = "signup"
                        showImageLayout()
                    }
                    progressBar.visibility = View.GONE
                }
        }

        btnResetPassword.setOnClickListener {
            SharedData.backFrom = "reset"
            val intent = Intent(this@SignupActivity, ResetPasswordActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnSignIn.setOnClickListener {
            SharedData.backFrom = "login"
            val intent = Intent(this@SignupActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun showImageLayout() {
        firstLayout.visibility = View.GONE
        secondLayout.visibility = View.VISIBLE

        profileImage.setOnClickListener {
            takePictureIntent()
        }

        btnNext.setOnClickListener {
            if (btnNext.text.toString().trim() == "Next") {
                SharedData.userInfo = SharedData.tempUserInfo!!
                FireUtils.updateUserInfo(SharedData.uuid, SharedData.tempUserInfo!!)
            }

            val intent = Intent(this@SignupActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun takePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { pictureIntent ->
            pictureIntent.resolveActivity(this.packageManager).also {
                startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap

            uploadImageAndSaveUri(imageBitmap)
        }
    }

    private fun uploadImageAndSaveUri(bitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        val storageRef = FirebaseStorage.getInstance()
            .reference
            .child("pics/${SharedData.userInfo!!.uid}.jpeg")
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val image = baos.toByteArray()

        val upload = storageRef.putBytes(image)
        progressImg.visibility = View.VISIBLE
        upload.addOnCompleteListener { uploadTask ->
            if (uploadTask.isSuccessful) {
                storageRef.downloadUrl.addOnCompleteListener { urlTask ->
                    urlTask.result?.let {
                        val imageUri = it

                        SharedData.tempUserInfo = User(SharedData.userInfo!!.uid, SharedData.userInfo!!.name, SharedData.userInfo!!.email, imageUri.toString(), SharedData.userInfo!!.favourite, SharedData.userInfo!!.watchList)

//                        Toast.makeText(activity, imageUri.toString(), Toast.LENGTH_LONG).show()
                        btnNext.text = "Next"
                        profileImage.setImageBitmap(bitmap)
                        profileImage.rotation = 90F
                        progressImg.visibility = View.GONE
                    }
                }
            } else {
                uploadTask.exception?.let {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        progressBar.visibility = View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        SharedData.backFrom = "signup"
        val intent = Intent(this@SignupActivity, MainActivity::class.java)
        startActivity(intent)
        super.onBackPressed()
    }
}

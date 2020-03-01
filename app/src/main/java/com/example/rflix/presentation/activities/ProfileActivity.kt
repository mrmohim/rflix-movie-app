package com.example.rflix.presentation.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.rflix.R
import com.example.rflix.utils.FireUtils
import com.example.rflix.utils.SharedData
import com.example.rflix.utils.User
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.name
import kotlinx.android.synthetic.main.activity_profile.profileImage
import kotlinx.android.synthetic.main.activity_profile.progressBar
import kotlinx.android.synthetic.main.activity_profile.progressImg
import java.io.ByteArrayOutputStream

class ProfileActivity : AppCompatActivity() {
    private val REQUEST_IMAGE_CAPTURE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Profile"

        Handler().postDelayed({
            progressBar.visibility = View.GONE

            Glide.with(this)
                .load(SharedData.userInfo!!.imgUrl)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        profileImageInfo.setImageResource(R.drawable.ic_profile_white_24dp)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        profileImageInfo.rotation = 90F
                        return false
                    }
                })
                .error(R.drawable.ic_profile_white_24dp)
                .placeholder(R.drawable.ic_profile_white_24dp)
                .centerCrop()
                .into(profileImageInfo)
            userNameInfo.text = SharedData.userInfo!!.name
            userEmailInfo.text = SharedData.userInfo!!.email

            btnEditInfo.setOnClickListener {
                infoView.visibility = View.GONE
                editView.visibility = View.VISIBLE
                name.setText(SharedData.userInfo!!.name)

                profileImage.setOnClickListener {
                    takePictureIntent()
                }

                btnSave.setOnClickListener {
                    val name = name.text.toString().trim()
                    if (name.isEmpty()) {
                        Toast.makeText(this, "Please fill-up all the field!", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    SharedData.tempUserInfo!!.name = name
                    SharedData.userInfo = SharedData.tempUserInfo!!
                    FireUtils.updateUserInfo(SharedData.uuid, SharedData.tempUserInfo!!)

                    val intent = Intent(this@ProfileActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }, 1000)
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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        SharedData.backFrom = "profile"
        val intent = Intent(this@ProfileActivity, MainActivity::class.java)
        startActivity(intent)
        super.onBackPressed()
    }
}

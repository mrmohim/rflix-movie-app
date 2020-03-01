package com.example.rflix.utils

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlin.collections.HashMap

object FireUtils {

    fun signUp(auth: FirebaseAuth, name: String, email: String) {
        val database: DatabaseReference = FirebaseDatabase.getInstance().reference
        SharedData.userInfo = User(auth.uid!!, name, email, "", arrayListOf(), arrayListOf())
        SharedData.uuid = auth.uid!!
        database.child("users").child(auth.uid!!).setValue(SharedData.userInfo)
    }

    fun feedBack(auth: FirebaseAuth, email: String, messege: String) {
        val database: DatabaseReference = FirebaseDatabase.getInstance().reference
        SharedData.feedback = Feedback(auth.uid!!,email, messege)
        database.child("feedback").child(auth.uid!!).setValue(SharedData.feedback)
    }

    fun updateUserInfo(uuid: String, user: User) {
        val database: DatabaseReference = FirebaseDatabase.getInstance().reference
        val key = database.child("users").push().key
        if (key == null) {
            Log.e("FireBaseUtils", "Couldn't get push key for users!")
            return
        }

        val childUpdates = HashMap<String, Any>()
        childUpdates["/users/$uuid"] = user

        database.updateChildren(childUpdates)
    }

    fun getUserInfo(uuid: String) {
        val database: DatabaseReference = FirebaseDatabase.getInstance().reference
        database.child("users").child(uuid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                SharedData.userInfo = dataSnapshot.getValue(User::class.java)
                Log.e("MohimData", "Read value successfully!")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FireBaseUtils", "Failed to read value!")
            }
        })
    }
}
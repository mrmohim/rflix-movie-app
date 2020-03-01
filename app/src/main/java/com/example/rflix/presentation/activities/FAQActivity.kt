package com.example.rflix.presentation.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rflix.R
import com.example.rflix.presentation.adapter.FAQAdapter
import com.example.rflix.utils.SharedData
import java.util.*

class FAQActivity : AppCompatActivity() {
    private var links = ArrayList<Array<String>>()
    private lateinit var adapterFAQ: FAQAdapter
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "FAQ"

        setUpLinksAndAdapter()
    }

    private fun setUpLinksAndAdapter() {
        val recyclerView = findViewById<RecyclerView>(R.id.faqList)
        links.add(
            arrayOf(
                "1. What is rflix?",
                "rflix is the world\'s leading entertainment service provider for emerging markets, offering users access to thousands of TV and movie series - as well as local entertainment, LIVE sports, rflix Originals, Snackables and more. And we only provide all the entertainment you want with just one click and high quality on any device you use. Users will enjoy the full version and open the VIP appearance to gain access to EVERYTHING. (Now you know rflix, we want to hear from you!)"
            )
        )
        links.add(
            arrayOf(
                "2. Where is rflix available?",
                "rflix is currently available in Bangladesh."
            )
        )
        links.add(
            arrayOf(
                "3. What devices can I use to stream rflix?",
                "rflix can be used on phones, laptops, tablets. It\'s easy! For those who can\'t wait, download the rflix app on the Google Play Store for the rflix service on your phone / tablet."
            )
        )
        links.add(
            arrayOf(
                "4. What is the minimum requirement for my devices?",
                "We\'re making sure our technology is up-to-date. So you can serve your device smoothly. Make sure you have at least the following system requirements. If not, be afraid of something else.  Android devices: 4.4 and above."
            )
        )
        links.add(
            arrayOf(
                "5. If i don\'t have an account, can I treat rflix?",
                "No, you need to sign up for an account to get started with rflix. You can browse our extensive catalog before signing up, but when you sign in rflix you have to sign out."
            )
        )
        links.add(
            arrayOf(
                "6. How to sign up?",
                "It\'s nice. We have made this process easy for you. And we want to give you the maximum rflix experience. So we give you VIP service. On the APP: Click Sign Up when the app launches."
            )
        )
        links.add(
            arrayOf(
                "7. What is the internet connection speed required?",
                "The wise ones will surely win, except for the rabbit in the children\'s story. Generally, we need at least a broadband internet. However, we will try our best to make sure you can stream rflix with style, even with turtle speed internet."
            )
        )
        links.add(
            arrayOf(
                "8. How do I change my password?",
                "Want to update your password? Well, by following these steps: - Sign up for your account on the rflix website with your current email address and password. - Click on My Account. - Click on Change Password under Profile. - Fill in the details and click Reset Password."
            )
        )
        links.add(
            arrayOf(
                "9. I forgot my password, can I recover it?",
                "Users can use the \'forgot password\' function on the login page, enter the email address you have registered and we will send you the password-fixing email."
            )
        )

        adapterFAQ = FAQAdapter(links)
        val mLayoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false
            )
        recyclerView.layoutManager = mLayoutManager
        recyclerView.adapter = adapterFAQ
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        SharedData.backFrom = "faq"
        val intent = Intent(this@FAQActivity, MainActivity::class.java)
        startActivity(intent)
        super.onBackPressed()
    }
}

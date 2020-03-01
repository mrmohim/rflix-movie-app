package com.example.rflix.presentation.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.rflix.R
import com.example.rflix.constant.Config
import com.example.rflix.utils.FireUtils
import com.example.rflix.utils.SharedData
import com.example.rflix.utils.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.navigation_view_header.*
import kotlinx.android.synthetic.main.navigation_view_header.view.*


class MainActivity : AppCompatActivity() {
    var WAITING_TIME = 5000L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (SharedData.splashFlag) {
            Handler().postDelayed({
                if (SharedData.uuid != "unknown") {
                    setHeaderInfo()
                }
                splashLayout.visibility = View.GONE
                SharedData.splashFlag = false
            }, WAITING_TIME)
            val myAnim: Animation = AnimationUtils.loadAnimation(this, R.anim.my_splash_animation)
            logo.startAnimation(myAnim)
        } else {
            splashLayout.visibility = View.GONE
            if (SharedData.backFrom == "messenger") {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Config.FB_URL))
                startActivity(intent)
            }
            if (SharedData.backFrom != "login" && SharedData.backFrom != "signup")
                WAITING_TIME = 1000L
            if (SharedData.uuid != "unknown") {
                progressBarMain.visibility = View.VISIBLE
                Handler().postDelayed({
                    setHeaderInfo()
                    progressBarMain.visibility = View.GONE
                }, WAITING_TIME)
            }
        }

        initView()
    }

    private fun setHeaderInfo() {
        textName.text = SharedData.userInfo!!.name
        textEmail.text = SharedData.userInfo!!.email
        Glide.with(this)
            .load(SharedData.userInfo!!.imgUrl)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    profileImage.setImageResource(R.drawable.ic_profile_white_24dp)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    profileImage.rotation = 90F
                    return false
                }
            })
            .error(R.drawable.ic_profile_white_24dp)
            .placeholder(R.drawable.ic_profile_white_24dp)
            .centerCrop()
            .into(profileImage)
    }

    private fun initView() {
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        Hawk.init(this).build()
        if (Hawk.count() == 0L) Hawk.put(
            "login",
            "unknown"
        )
        updateSharedData()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        setUpDrawerLayout()
        changeNavigationHeader()
        changeNavigationItem()

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navItemProfile -> {
                    Toast.makeText(this, "Profile", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@MainActivity, ProfileActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                R.id.navItemSettings -> {
                    Toast.makeText(this, "Settings", Toast.LENGTH_LONG).show()
                }
                R.id.navItemContact -> {
                    Toast.makeText(this, "Contact Us", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@MainActivity, ContactUsActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                R.id.navItemFAQ -> {
                    Toast.makeText(this, "FAQ", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@MainActivity, FAQActivity::class.java)
                    startActivity(intent)
                    finish()
                }
//                R.id.navItemTerms -> {
//                    Toast.makeText(this, "Terms of use", Toast.LENGTH_LONG).show()
//                }
//                R.id.navItemPrivacy -> {
//                    Toast.makeText(this, "Privacy Policy", Toast.LENGTH_LONG).show()
//                }
                R.id.navItemLogout -> {
                    Toast.makeText(this, "Logout", Toast.LENGTH_LONG).show()
                    Hawk.put("login", "unknown")
                    SharedData.uuid = "unknown"
                    SharedData.userInfo = User()
                    SharedData.tempUserInfo = User()

                    auth.signOut()
                    val user = auth.currentUser
                    if (user == null) {
                        SharedData.backFrom = "login"
                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
            menuItem.isChecked = true
            drawerLayout.closeDrawers()
            true
        }

        val bottomNavView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_upcoming,
                R.id.navigation_favourite,
                R.id.navigation_watchlist
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNavView.setupWithNavController(navController)
        when (SharedData.backFrom) {
            "favourite" -> bottomNavView.selectedItemId = R.id.navigation_favourite
            "watchlist" -> bottomNavView.selectedItemId = R.id.navigation_watchlist
            "upcoming" -> bottomNavView.selectedItemId = R.id.navigation_upcoming
            else -> bottomNavView.selectedItemId = R.id.navigation_home
        }
    }

    private fun updateSharedData() {
        SharedData.uuid = Hawk.get("login")
        if (SharedData.uuid != "unknown") {
            FireUtils.getUserInfo(SharedData.uuid)
        }
    }

    private fun changeNavigationItem() {
        navigationView.menu.clear()

        if (SharedData.uuid != "unknown") {
            navigationView.inflateMenu(R.menu.drawer_items_user)
        } else {
            navigationView.inflateMenu(R.menu.drawer_items)
        }
    }

    private fun changeNavigationHeader() {
        val headerView = navigationView.getHeaderView(0)
        if (SharedData.uuid != "unknown") {
            headerView.signUpLayout.visibility = View.GONE
            headerView.userLayout.visibility = View.VISIBLE
        } else {
            headerView.userLayout.visibility = View.GONE
            headerView.signUpLayout.visibility = View.VISIBLE

            headerView.btnSignUp.setOnClickListener {
                SharedData.backFrom = "signup"
                val intent = Intent(this@MainActivity, SignupActivity::class.java)
                startActivity(intent)
                finish()
            }

            headerView.btnLogin.setOnClickListener {
                SharedData.backFrom = "login"
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setUpDrawerLayout() {
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.drawerOpen, R.string.drawerClose
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.search_bar_menu, menu)
        val item: MenuItem = menu!!.getItem(0)
        searchView.setMenuItem(item)
        searchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                SharedData.searchContent = query.toString()
                if (SharedData.searchContent.isNotEmpty()) {
                    SharedData.backFrom = "search"
                    val intent = Intent(this@MainActivity, SearchActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
        return super.onCreateOptionsMenu(menu)
    }

    @SuppressLint("WrongConstant")
    override fun onBackPressed() {
        when {
            drawerLayout.isDrawerOpen(Gravity.START) -> {
                drawerLayout.closeDrawer(Gravity.START)
            }
            searchView.isSearchOpen -> {
                searchView.closeSearch()
            }
            else -> {
                super.onBackPressed()
            }
        }
    }
}

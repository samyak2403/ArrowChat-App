package com.samyak2403.arrowchatapp

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.samyak2403.arrowchatapp.Adapter.PagerAdapter

class ChatActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private lateinit var pagerAdapter: PagerAdapter
    private lateinit var mtoolbar: androidx.appcompat.widget.Toolbar

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Initialize views
        tabLayout = findViewById(R.id.include)
        viewPager = findViewById(R.id.fragmentcontainer)
        mtoolbar = findViewById(R.id.toolbar)

        // Initialize Firebase
        firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        // Setup Toolbar
        setSupportActionBar(mtoolbar)
        val drawable: Drawable? = ContextCompat.getDrawable(applicationContext, R.drawable.ic_baseline_more_vert_24)
        mtoolbar.overflowIcon = drawable

        // Setup ViewPager and TabLayout
        pagerAdapter = PagerAdapter(supportFragmentManager, tabLayout.tabCount)
        viewPager.adapter = pagerAdapter

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
                if (tab.position in 0..2) {
                    pagerAdapter.notifyDataSetChanged()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.profiles -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStart() {
        super.onStart()
        updateUserStatus("Online")
    }

    override fun onStop() {
        super.onStop()
        updateUserStatus("Offline")
    }

    private fun updateUserStatus(status: String) {
        firebaseAuth.uid?.let { uid ->
            val documentReference: DocumentReference = firebaseFirestore.collection("Users").document(uid)
            documentReference.update("status", status).addOnSuccessListener {
                val message = if (status == "Online") "Now User is Online" else "Now User is Offline"
                Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(applicationContext, "Failed to update status", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

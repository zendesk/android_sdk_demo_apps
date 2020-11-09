package com.zendesk.talk.sample

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.zendesk.talk.sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.setMissingCredentials(SampleApplication.isMissingCredentials())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (!SampleApplication.isMissingCredentials()) {
            menuInflater.inflate(R.menu.main_activity_menu, menu)
            return true
        }
        return false
    }

    private fun ActivityMainBinding.setMissingCredentials(missing: Boolean) {
        val views = listOf(chatBubble, line1, queuePosition, line2, messageDivider, chatForm, send)
        val viewsVisibility = if (missing) View.GONE else View.VISIBLE
        views.forEach { view -> view.visibility = viewsVisibility }
        missingCredentials.visibility = if (missing) View.VISIBLE else View.GONE
    }
}
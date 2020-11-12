package com.zendesk.talk.sample

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.zendesk.talk.sample.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val lineStatusChecker = LineStatusChecker()

    private var lineAvailable: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.setMissingCredentials(SampleApplication.isMissingCredentials())

        binding.checkLineStatus.setOnClickListener {
            Toast.makeText(this, R.string.line_status_checking, Toast.LENGTH_SHORT).show()
            lifecycleScope.launch {
                lineAvailable = lineStatusChecker.lineStatus()
                invalidateOptionsMenu()
                val message = if (lineAvailable) R.string.line_available else R.string.line_not_available
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (!SampleApplication.isMissingCredentials()
                && lineAvailable
                && SampleApplication.DIGITAL_LINE.isNotEmpty()
        ) {
            menuInflater.inflate(R.menu.main_activity_menu, menu)
            return true
        }
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.call_agent -> {
            SampleApplication.talk?.startCallSetupFlow(
                    context = this,
                    digitalLine = SampleApplication.DIGITAL_LINE,
                    successIntent = null
            )
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun ActivityMainBinding.setMissingCredentials(missing: Boolean) {
        val views = listOf(chatBubble, line1, queuePosition, line2, messageDivider, chatForm, send, checkLineStatus)
        val viewsVisibility = if (missing) View.GONE else View.VISIBLE
        views.forEach { view -> view.visibility = viewsVisibility }
        missingCredentials.visibility = if (missing) View.VISIBLE else View.GONE
    }
}
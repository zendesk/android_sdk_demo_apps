package bfahy.support_override_styles

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import zendesk.support.guide.HelpCenterActivity
import zendesk.support.requestlist.RequestListActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Global.isMissingCredentials()) {
            setContentView(R.layout.missing_credentials)
            return
        }
        setContentView(R.layout.activity_main)

        helpCenterBtn.setOnClickListener {
            HelpCenterActivity
                    .builder()
                    .show(this@MainActivity)
        }

        requestListBtn.setOnClickListener {
            RequestListActivity
                    .builder()
                    .show(this@MainActivity)
        }
    }
}

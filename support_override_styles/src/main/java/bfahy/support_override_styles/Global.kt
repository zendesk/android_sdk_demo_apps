package bfahy.support_override_styles

import android.app.Application
import com.zendesk.logger.Logger
import com.zendesk.util.StringUtils
import zendesk.core.AnonymousIdentity
import zendesk.core.JwtIdentity
import zendesk.core.Zendesk
import zendesk.support.Support

class Global : Application() {

    private val SUBDOMAIN_URL = "https://z3nadvocate84.zendesk.com"
    private val APPLICATION_ID = "daf8af259b13b56f4a8d0594dafa9d8b7fcbe9f3934a9940"
    private val OAUTH_CLIENT_ID = "mobile_sdk_client_96ccbde88735e328e182"

    companion object {
        private var missingCredentials = false
        fun isMissingCredentials(): Boolean {
            return missingCredentials
        }
    }


    override fun onCreate() {
        super.onCreate()

        // Enable logging
        Logger.setLoggable(true)

        if (StringUtils.isEmpty(SUBDOMAIN_URL)
                || StringUtils.isEmpty(APPLICATION_ID)
                || StringUtils.isEmpty(OAUTH_CLIENT_ID)) {
            missingCredentials = true
            return
        }

        /**
         * Initialize the SDK with your Zendesk subdomain, mobile SDK app ID, and client ID.
         *
         * Get these details from your Zendesk dashboard: Admin -> Channels -> MobileSDK.
         */
        Zendesk.INSTANCE.init(this,
                SUBDOMAIN_URL,
                APPLICATION_ID,
                OAUTH_CLIENT_ID)

        /**
         * Set an identity (authentication).
         *
         * Set either Anonymous or JWT identity, as below:
         */

        // a). Anonymous (All fields are optional)
        Zendesk.INSTANCE.setIdentity(
                AnonymousIdentity.Builder()
                        .withNameIdentifier("{optional name}")
                        .withEmailIdentifier("{optional email}")
                        .build()
        )

        // b). JWT (Must be initialized with your JWT identifier)
        Zendesk.INSTANCE.setIdentity(JwtIdentity("{JWT User Identifier}"))

        Support.INSTANCE.init(Zendesk.INSTANCE)
    }
}

package com.zendesk.talk.sample

import android.app.Application
import com.zendesk.logger.Logger
import zendesk.core.AnonymousIdentity
import zendesk.core.Zendesk
import zendesk.talk.android.Talk

class SampleApplication : Application() {

    companion object {
        private const val SUBDOMAIN_URL = ""
        private const val APPLICATION_ID = ""
        private const val OAUTH_CLIENT_ID = ""

        // set your digital line name
        const val DIGITAL_LINE = ""

        private val CREDENTIALS = listOf(SUBDOMAIN_URL, APPLICATION_ID, OAUTH_CLIENT_ID)

        var talk: Talk? = null
            private set

        fun isMissingCredentials() = CREDENTIALS.any { credential -> credential.isEmpty() }
    }

    override fun onCreate() {
        super.onCreate()

        // Enable logging
        Logger.setLoggable(true)

        if (isMissingCredentials()) {
            return
        }

        /**
         * Initialize the SDK with your Zendesk subdomain, mobile SDK app ID, and client ID.
         *
         * Get these details from your Zendesk dashboard: Admin -> Channels -> MobileSDK.
         */
        Zendesk.INSTANCE.init(
                this,
                SUBDOMAIN_URL,
                APPLICATION_ID,
                OAUTH_CLIENT_ID
        )

        /**
         * Set an identity (authentication).
         *
         * Talk SDK supports only Anonymous identity
         */
        Zendesk.INSTANCE.setIdentity(
                AnonymousIdentity.Builder()
                        .withNameIdentifier("{optional name}")
                        .withEmailIdentifier("{optional email}")
                        .build()
        )

        talk = Talk.create(Zendesk.INSTANCE)
    }
}
package com.zendesk.talk.sample

import zendesk.talk.android.LineStatusResult

class LineStatusChecker {

    suspend fun lineStatus(): Boolean {
        return when (val lineStatus = SampleApplication.talk?.lineStatus(SampleApplication.DIGITAL_LINE)) {
            is LineStatusResult.Success -> lineStatus.agentAvailable
            is LineStatusResult.Failure -> false
            null -> false
        }
    }
}
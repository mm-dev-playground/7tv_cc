package com.a7tv.codingchallenge.codingchallengefor7tv.util

data class LinkHeaderParseException(val reason: String, val content: String?) : Throwable() {

    companion object Reason {
        const val WRONG_SIZE = "Illegal size of header values"
        const val NO_ID_FOUND = "No value could be found"
        const val KEY_NOT_PRESENT = "No Link header is present"
    }

}
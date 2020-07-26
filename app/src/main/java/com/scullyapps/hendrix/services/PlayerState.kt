package com.scullyapps.hendrix.services

enum class PlayerState(val sentenceString: String) {
    PLAYING("playing"),
    PAUSED("paused"),
    STOPPED("stopped"),
    ERROR("in an error state")
}
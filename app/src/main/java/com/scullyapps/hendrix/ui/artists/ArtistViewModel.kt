package com.scullyapps.hendrix.ui.artists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ArtistViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Artists"
    }
    val text: LiveData<String> = _text
}
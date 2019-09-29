package com.scullyapps.hendrix.ui.albums

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AlbumViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Albums"
    }
    val text: LiveData<String> = _text
}
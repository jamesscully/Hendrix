package com.scullyapps.hendrix.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.scullyapps.hendrix.R

class SetupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        var tlbr = supportActionBar

        tlbr?.hide()

    }

}

package com.scullyapps.hendrix

import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ServiceTestRule
import androidx.test.runner.AndroidJUnit4
import com.scullyapps.hendrix.models.song.Song
import com.scullyapps.hendrix.services.SoundService

import org.junit.Test
import org.junit.runner.RunWith

//import org.junit.Assert.*
import org.junit.Rule

@get:Rule
val serviceRule = ServiceTestRule()

@RunWith(AndroidJUnit4::class)
class SoundServiceTest {

    private val TAG = "SoundServiceTest"

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        org.junit.Assert.assertEquals("com.scullyapps.hendrix", appContext.packageName)
    }

    @Test
    fun testAddToQueue() {
        val sIntent = Intent(ApplicationProvider.getApplicationContext(), SoundService::class.java)
        val binder : IBinder = serviceRule.bindService(sIntent)
        val service: SoundService = (binder as SoundService.SoundBinder).getService()

        val songs = getExampleSongs()

        // disable playback
        service.TEST_MODE = true

        Log.d(TAG, songs.size.toString())

        for(s in songs) {
            service.addToQueue(s)
        }

        for(s in songs) {
            Log.d(TAG, "Testing that ${service.song?.title} == ${s.title}")
            Log.d(TAG, service.strStats())
            org.junit.Assert.assertTrue(service.song == s)
            service.next()
        }
    }


    fun queueBackAndForth() {

    }

    /**
     * Return list of dummy songs to use for testing
     */
    private fun getExampleSongs() : ArrayList<Song> {
        val r = ArrayList<Song>()

        for(x in 1..10) {
            print("Adding")
            val s = Song()
            s.title = x.toString()
            s.artist = x.toString()
            s.path = x.toString()
            s.id = x

            r.add(s)
        }
        return r
    }
}

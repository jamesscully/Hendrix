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
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@get:Rule
val serviceRule = ServiceTestRule()

@RunWith(AndroidJUnit4::class)
class SoundServiceTest {

    private val TAG = "SoundServiceTest"

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.scullyapps.hendrix", appContext.packageName)
    }

    @After
    fun dtor() {
        serviceRule.unbindService()
    }

    @Test
    fun testAddToQueue() {
        val sIntent = Intent(ApplicationProvider.getApplicationContext(), SoundService::class.java)
        val binder : IBinder = serviceRule.bindService(sIntent)
        val service: SoundService = (binder as SoundService.SoundBinder).getService()

        val songs = getExampleSongs()

        // disable playback
        service.TEST_MODE = true

        for(s in songs) {
            service.addToQueue(s)
        }

        for(s in songs) {
            Log.d(TAG, "Testing that ${service.song?.title} == ${s.title}")
            Log.d(TAG, service.strStats())
            assertTrue(service.song == s)
            service.next()
        }
    }

    @Test
    fun queueBackAndForth() {
        val sIntent = Intent(ApplicationProvider.getApplicationContext(), SoundService::class.java)
        val binder : IBinder = serviceRule.bindService(sIntent)
        val service: SoundService = (binder as SoundService.SoundBinder).getService()

        val songs = getExampleSongs()

        // disable playback
        service.TEST_MODE = true

        for(s in songs) {
            service.addToQueue(s)
        }

        /*
            v
            1 2 3 4 5 6 7 8 9 10
                  v (>3)
            1 2 3 4 5 6 7 8 9 10
              v     (<2)
            1 2 3 4 5 6 7 8 9 10
                              v (>8)
            1 2 3 4 5 6 7 8 9 10
         */

        assertEquals("1", service.song?.title)

        Log.d(TAG, "${service.strStats()}")
        for(x in 1..3)
            service.next()
        Log.d(TAG, "${service.strStats()}")

        assertEquals("4", service.song?.title)

        for(x in 1..2)
            service.prev()

        Log.d(TAG, "${service.strStats()}")

        assertEquals("2", service.song?.title)

        for(x in 1..8)
            service.next()

        assertEquals("10", service.song?.title)
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

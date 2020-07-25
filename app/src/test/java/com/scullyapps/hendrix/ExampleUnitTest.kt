package com.scullyapps.hendrix

import com.scullyapps.hendrix.models.song.Song
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    //         x y  z
    // convert 1:30:15 to millis
    // milliseconds per:
    //
    // h = 3600000
    // m = 60000
    // s = 1000

    @Test
    fun durationToTimestampTest() {
        assertEquals("00:30", Song.millisToTimestamp(30000))
        assertEquals("50:02", Song.millisToTimestamp(3002000))
        assertEquals("10:02", Song.millisToTimestamp(602000))
        assertEquals("00:02", Song.millisToTimestamp(2000))
        assertEquals("01:05:02", Song.millisToTimestamp(3902000))
        assertEquals("02:00:00", Song.millisToTimestamp(7200000))
        assertEquals("00:00", Song.millisToTimestamp(0))
    }
}

package com.scullyapps.hendrix.data

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.scullyapps.hendrix.GlobalApp
import com.scullyapps.hendrix.models.song.Bookmark
import com.scullyapps.hendrix.models.song.Song

object BookmarkDB : SQLiteOpenHelper(GlobalApp.getAppContext(), "BookmarkDB", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE \"Bookmarks\" (\n" +
                "\t\"Hash\"\tTEXT NOT NULL,\n" +
                "\t\"Timestamp\"\tINTEGER NOT NULL,\n" +
                "\t\"Caption\"\tTEXT,\n" +
                "\tFOREIGN KEY(\"Hash\") REFERENCES \"Songs\"(\"Hash\"),\n" +
                "\tPRIMARY KEY(\"Caption\")\n" +
                ")")

        db?.execSQL("CREATE TABLE \"Songs\" (\n" +
                "\t\"Hash\"\tTEXT NOT NULL UNIQUE,\n" +
                "\t\"Count\"\tINTEGER DEFAULT 0,\n" +
                "\tPRIMARY KEY(\"Hash\")\n" +
                ")")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    val BOOKMARKS_TABLE : String = "Bookmarks"
    val SONGS_TABLE : String = "Songs"

    fun createBookmark(b : Bookmark) {
        val hash = b.hash

        val cv = ContentValues()

        cv.put("Timestamp", b.timestamp)
        cv.put("Caption", b.caption)

        writableDatabase.insert(BOOKMARKS_TABLE, null, cv)
    }

    fun updateBookmark(b: Bookmark) {
        val hash = b.hash

        val cv = ContentValues()

        cv.put("Timestamp", b.timestamp)
        cv.put("Caption", b.caption)

//        writableDatabase.update(BOOKMARKS_TABLE, cv, "")

    }

    fun deleteBookmark(b: Bookmark) {
        val hash = b.hash

        // writableDatabase.delete(BOOKMARKS_TABLE, "")

    }

    fun getBookmarks() : ArrayList<Bookmark> {

        val bookmarks = ArrayList<Bookmark>(16)

        return bookmarks
    }

    fun bookmarkCount(s: Song) {
        val hash = s.calculateMD5()

    }


}
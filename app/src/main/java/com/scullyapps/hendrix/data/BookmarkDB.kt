package com.scullyapps.hendrix.data

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.scullyapps.hendrix.GlobalApp
import com.scullyapps.hendrix.models.song.Bookmark
import com.scullyapps.hendrix.models.song.Song

object BookmarkDB : SQLiteOpenHelper(GlobalApp.getAppContext(), "BookmarkDB", null, 1) {

    private val TAG = "BookmarkDB"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE \"Bookmarks\" (\n" +
                "\t\"Hash\"\tTEXT NOT NULL,\n" +
                "\t\"Timestamp\"\tINTEGER NOT NULL,\n" +
                "\t\"Caption\"\tTEXT,\n" +
                "\tFOREIGN KEY(\"Hash\") REFERENCES \"Songs\"(\"Hash\")\n" +
                ")")

        // not sure if this is particularly needed; we'll see when it comes to counting bookmarks
        db?.execSQL("CREATE TABLE \"Songs\" (\n" +
                "\t\"Hash\"\tTEXT NOT NULL UNIQUE,\n" +
                "\t\"Count\"\tINTEGER DEFAULT 0,\n" +
                "\tPRIMARY KEY(\"Hash\")\n" +
                ")")
        Log.d(TAG, "Created database ${db?.version}")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
        Log.d(TAG, "Upgraded database v$oldVersion to v$newVersion")
    }

    private val BOOKMARKS_TABLE : String = "Bookmarks"
    private val SONGS_TABLE : String = "Songs"

    fun createBookmark(b : Bookmark) {
        val hash = b.hash

        val cv = ContentValues()

        cv.put("Hash", hash)
        cv.put("Timestamp", b.timestamp)
        cv.put("Caption", b.caption)

        writableDatabase.insert(BOOKMARKS_TABLE, null, cv)

        Log.d(TAG, "Inserted Bookmark: $b")
    }

    fun updateBookmark(b: Bookmark) {
        val hash = b.hash

        val cv = ContentValues()

        cv.put("Hash", hash)
        cv.put("Timestamp", b.timestamp)
        cv.put("Caption", b.caption)

        Log.w(TAG, "Updating $b, untested update code. Check!")
        writableDatabase.update(BOOKMARKS_TABLE, cv, "Hash=$hash", null)

        Log.d(TAG, "Updated Bookmark: $b")
    }

    fun deleteBookmark(b: Bookmark) {
        val hash = b.hash

        writableDatabase.delete(BOOKMARKS_TABLE, "Hash=$hash", null)

        Log.d(TAG, "Deleted Bookmark: $b")
    }

    fun getBookmarks(hash : String) : ArrayList<Bookmark> {

        val bookmarks = ArrayList<Bookmark>(16)
        val columns = arrayOf("Hash", "Timestamp", "Caption")

        Log.d(TAG, "Querying database for bookmarks with hash: $hash")
        val cursor : Cursor = readableDatabase.query(BOOKMARKS_TABLE, columns, "", null, null, null, "Timestamp ASC")

        if(cursor.count == 0) {
            Log.d(TAG, "No bookmarks found for hash $hash")
            return bookmarks
        }

        cursor.moveToFirst()

        Log.d(TAG, "Found ${cursor.count} bookmarks:")
        while(!cursor.isAfterLast) {
            val b = Bookmark.fromCursor(cursor)
            Log.d(TAG, "\tBookmark: $b")
            bookmarks.add(b)

            cursor.moveToNext()
        }

        return bookmarks
    }

    fun getBookmarks(s: Song) : ArrayList<Bookmark> {
        return getBookmarks(s.calculateMD5())
    }

    fun bookmarkCount(s: Song) {
        val hash = s.calculateMD5()

    }


}
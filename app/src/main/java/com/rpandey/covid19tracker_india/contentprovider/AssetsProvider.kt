package com.rpandey.covid19tracker_india.contentprovider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.res.AssetFileDescriptor
import android.database.Cursor
import android.net.Uri
import java.io.FileNotFoundException


class AssetsProvider : ContentProvider() {

    override fun openAssetFile(uri: Uri, mode: String): AssetFileDescriptor? {
        var fileDescriptor: AssetFileDescriptor? = null
        try {
            val fileName = uri.lastPathSegment ?: throw FileNotFoundException()
            fileDescriptor = context?.assets?.openFd(fileName)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return fileDescriptor
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {
        return super.query(uri, projection, selection, selectionArgs, sortOrder, null)
    }

    override fun onCreate(): Boolean {
        return false
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun getType(uri: Uri): String? {
        return null
    }
}
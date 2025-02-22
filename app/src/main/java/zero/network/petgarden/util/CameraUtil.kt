package zero.network.petgarden.util

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore


fun getPath(context: Context, uri: Uri): String? {

    // DocumentProvider
    if (DocumentsContract.isDocumentUri(context, uri)) {
        // ExternalStorageProvider
        if (uri.isExternalStorageDocument()) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":").toTypedArray()
            val type = split[0]
            if ("primary".equals(type, ignoreCase = true)) {
                return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
            }
        } else if (uri.isDownloadsDocument()) {
            val id = DocumentsContract.getDocumentId(uri)
            val contentUri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
            )
            return getDataColumn(context, contentUri, null, null)
        } else if (uri.isMediaDocument()) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":").toTypedArray()
            val contentUri: Uri = when (split[0]) {
                "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI

                "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI

                "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

                else -> throw Exception("Not support media")
            }
            val selection = "_id=?"
            val selectionArgs = arrayOf(
                split[1]
            )
            return getDataColumn(context, contentUri, selection, selectionArgs)
        }
    } else if ("content".equals(uri.scheme, ignoreCase = true)) {
        return getDataColumn(context, uri, null, null)
    } else if ("file".equals(uri.scheme, ignoreCase = true)) {
        return uri.path
    }
    return null
}

fun getDataColumn(
    context: Context,
    uri: Uri,
    selection: String?,
    selectionArgs: Array<String>?
): String? {
    var cursor: Cursor? = null
    val column = "_data"
    val projection = arrayOf(
        column
    )
    try {
        cursor = context.contentResolver.query(
            uri, projection, selection, selectionArgs,
            null
        )
        if (cursor != null && cursor.moveToFirst()) {
            val column_index = cursor.getColumnIndexOrThrow(column)
            return cursor.getString(column_index)
        }
    } finally {
        cursor?.close()
    }
    return null
}


fun Uri.isExternalStorageDocument(): Boolean {
    return "com.android.externalstorage.documents" == authority
}

fun Uri.isDownloadsDocument(): Boolean {
    return "com.android.providers.downloads.documents" == authority
}

fun Uri.isMediaDocument(): Boolean {
    return "com.android.providers.media.documents" == authority
}
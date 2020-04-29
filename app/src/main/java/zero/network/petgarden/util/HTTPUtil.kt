@file:Suppress("BlockingMethodInNonBlockingContext", "Unused")

/**
 * @author CarlosEduardoL
 */
package zero.network.petgarden.util

import android.util.Log
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection


/**
 * Execute an HTTP Get in the [IO] context
 */
suspend fun getRequest(url: String): String = withContext(IO) {
    val connection = url.connection
    connection.connect()

    return@withContext connection.inputStream?.readString()
        ?: throw IOException("Error when try to connect to $url")
}

/**
 * Execute an HTTP Post in the [IO] context
 */
suspend fun postRequest(url: String, json: String): String = withContext(IO) {
    val connection = url.connection
    connection.requestMethod = "POST"
    connection.setRequestProperty("Content-Type", "application/json-patch+json")
    connection.setRequestProperty("accept", "application/json")
    connection.doInput = true
    connection.doOutput = true

    val os: OutputStream = connection.outputStream
    os.writeString(json)

    return@withContext connection.inputStream?.readString()
        ?: throw IOException("Error when try to connect to $url")
}

/**
 * Execute an HTTP Put in the [IO] context
 */
suspend fun putRequest(url: String, json: String): String = withContext(IO) {
    val connection = url.connection
    connection.requestMethod = "PUT"
    connection.setRequestProperty("Content-Type", "application/json")
    connection.doOutput = true

    val os: OutputStream = connection.outputStream
    os.writeString(json)

    return@withContext connection.inputStream?.readString()
        ?: throw IOException("Error when try to connect to $url")
}

/**
 * Execute an HTTP Delete in the [IO] context
 */
suspend fun deleteRequest(url: String): String = withContext(IO) {
    val connection = url.connection
    connection.requestMethod = "DELETE"
    connection.setRequestProperty("Content-Type", "application/json")
    connection.doOutput = true

    return@withContext connection.inputStream?.readString()
        ?: throw IOException("Error when try to connect to $url")
}

suspend fun saveURLImageOnFile(url: String, file: File) {
    withContext(IO) {
        val page = URL(url)
        val connection = page.openConnection() as HttpsURLConnection
        val inputStream = connection.inputStream
        val fos = FileOutputStream(file)
        val buffer = ByteArray(4096)
        var bytesRead: Int
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            fos.write(buffer, 0, bytesRead)
        }
        inputStream.close()
        fos.close()
        connection.disconnect()
    }
}

private fun InputStream.readString(): String {
    val bufferedReader = BufferedReader(InputStreamReader(this))

    var line: String? = bufferedReader.readLine()
    val result = StringBuilder()

    while (line != null) {
        result.append(line)
        line = bufferedReader.readLine()
    }

    this.close()
    return result.toString()
}

private fun OutputStream.writeString(data: String) {
    val writer = BufferedWriter(OutputStreamWriter(this, "UTF-8"))
    writer.write(data)
    writer.flush()
}

// create HttpURLConnection
private val String.connection
    get() = URL(this).openConnection() as HttpURLConnection
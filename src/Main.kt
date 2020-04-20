import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import java.io.BufferedInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.net.URLConnection

@Suppress("BlockingMethodInNonBlockingContext")
fun main() = runBlocking  {
    saveUrlFlow(
        "Mont.mp3",
        "http://www.tarona.net/load/0-0-1-9992-20"
    ).collect {
        println("$it %")
    }
}

@Throws(MalformedURLException::class, IOException::class)
fun saveUrl(filename: String, urlString: String, callback: (Int) -> Unit) {

    var inputStream: BufferedInputStream? = null
    var fileOutputStream: FileOutputStream? = null

    var downloadedSize = 0
    val byteSize = 1024

    try {

        val url = URL(urlString)
        val connection: URLConnection = url.openConnection()
        connection.connect()

        val fileLength: Int = connection.contentLength
        println(fileLength)


        inputStream = BufferedInputStream(url.openStream())
        fileOutputStream = FileOutputStream(filename)

        println()

        val data = ByteArray(1024)
        var count: Int

        while (inputStream.read(data, 0, 1024).also { count = it } != -1) {
            fileOutputStream.write(data, 0, count)

            downloadedSize += byteSize
            val tt = (downloadedSize) * 100 / fileLength
            callback(tt)
        }
    } finally {
        inputStream?.close()
        fileOutputStream?.close()
    }
}


@Suppress("BlockingMethodInNonBlockingContext")
@Throws(MalformedURLException::class, IOException::class)
fun saveUrlFlow(filename: String, urlString: String): Flow<Int> = flow {

    var inputStream: BufferedInputStream? = null
    var fileOutputStream: FileOutputStream? = null

    var downloadedSize = 0
    val byteSize = 1024

    try {

        val url = URL(urlString)
        val connection: URLConnection = url.openConnection()
        connection.connect()

        val fileLength: Int = connection.contentLength
        println(fileLength)


        inputStream = BufferedInputStream(url.openStream())
        fileOutputStream = FileOutputStream(filename)

        println()

        val data = ByteArray(byteSize)
        var count: Int

        while (inputStream.read(data, 0, byteSize).also { count = it } != -1) {
            fileOutputStream.write(data, 0, count)

            downloadedSize += byteSize
            val tt = (downloadedSize) * 100 / fileLength
            emit(tt)
        }
    } finally {
        inputStream?.close()
        fileOutputStream?.close()
    }
}
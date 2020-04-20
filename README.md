# Kotlin Flow

![](https://devcrew.io/wp-content/uploads/2017/07/kotlin-cover-image-1-614x346.jpg)

Assalomu alaykum. Hozir men Android dasturchisi hamda o'rganuvchisi sifatida barcha yangi texnologiyalarni o'rganishga majburman. Albatta bu kasb moxirligi uchundir. Keling bugun o'zimiz bilmagan texnologiyani o'zimizcha tushunishga xarakat qilamiz. Bugungi tanlagan texnologiyamiz Kotlin Flow.

Kotlin async ishlar uchun o'zining konsepsiyasi Coroutine ni ishlab chiqdi. Bu ajoyib texnologiya. Async ishlar huddi asyncron kabi ishlatamiz. Ammo bu texnologiya yakka element qaytarishi kerak edi va shunaqa ishlaydi ham.

```kotlin
suspend fun downloadFile() : File {
    val file = // TODO()
    return file
}

fun main() = runBlocking {
    val file = downloadFile()

    // yoki io oqimda
    // val file = withContext(Dispatchers.IO) { downloadFile() }

    file. // TODO
}
```

Keling aytaylik bizga Bir tipdagi ko'plab elementlar qaytishi kerak. Va bu ishni biz qanday qilamiz. Bu ishda ham kotlin yordam beradi.

```kotlin

fun timer(delayMillis: Long): Flow<Unit> = flow {
    while (true) {
        delay(delayMillis)
        emit(Unit)
    }
}

fun main() = runBlocking {
    timer(1000).collect {
        println("Hi")
    }
}
```

Har bir sekundda signal beruvchi flow. Albatta bu ishni async bajartirish ham mumkin. Men o'ylashimcha flow haqida dastlabki ma'lumotga hech bo'lmaganda nima ekanligi haqida ma'lumotga ega bo'ldik.

Keling yana bir misol ko'ramiz. Bizga file yuklovchi yuklanganligini foizini ko'rsatuvchi kotlin funksiya yozamiz:

```kotlin

@Throws(MalformedURLException::class, IOException::class)
fun saveUrl(filename: String, urlString: String, callback: (Int) -> Unit)

```

[Namuna uchun kode shu yerdan olindi](https://stackoverflow.com/a/921408/13336527)

```kotlin
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
            val percentage = (downloadedSize) * 100 / fileLength
            callback(percentage)
        }
    } finally {
        inputStream?.close()
        fileOutputStream?.close()
    }
}

fun main() {
    saveUrl(
        "Mont.mp3",
        "http://www.tarona.net/load/0-0-1-9992-20"
    ) {
        println("$it %")
    }
}

```

Keling endi shu ishni Kotlin flow da amalga oshiramiz.

```kotlin
@Suppress("BlockingMethodInNonBlockingContext")
@Throws(MalformedURLException::class, IOException::class)
fun saveUrlFlow(filename: String, urlString: String): Flow<Int>
```

```kotlin

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
            val percentage = (downloadedSize) * 100 / fileLength
            emit(percentage)
        }
    } finally {
        inputStream?.close()
        fileOutputStream?.close()
    }
}

@Suppress("BlockingMethodInNonBlockingContext")
fun main() = runBlocking  {
    saveUrlFlow(
        "Mont.mp3",
        "http://www.tarona.net/load/0-0-1-9992-20"
    ).collect {
        println("$it %")
    }
}
```

Ikkala holat ham deyarli bir xil. Ammo o'ziga yarasha farqlanishi bor. Kotlin flowda scope o'z ichidagi Coroutine larni cancel qilganda bizning flow ham o'z ishini to'xtadi. Oqimlarni boshqarish qulay, suspend metodlarni flow ichida chaqirish mumkin. Lekin shuni hisobga olishimiz kerak bu misollar eng optimal misollar emas sababi:

```kotlin
@Suppress("BlockingMethodInNonBlockingContext")
```

* Biz ishlatgan BufferedInputStream va FileOutputStream lar suspending emas. Ular Coroutine contextdan tashqarida ishlashi ham mumkin. Ammo realroq misollar uchun shu holat yaxshi deb o'yladim.

* Yana bir xolatga ko'zingiz tushishi mumkin bu 100 % dan oshib ketgan natijalardir. Bu natijaning sababi biz har siklda 1024 baytdan yuklayapmiz va file hajmida qoldiq qolib ketishi mumkin. Shu sabab bizda ko'rsatgich 100% dan ham oshishi kuzatiladi. Biz buni tuzatishni sizga qoldirdik 😜

- O'ylaymanki kimgadir foydasi tegdi. Hozirgi maqolacha Kotlin flow haqida to'liq emas. Sekin sekin to'ldirib boriladi. Har xolda niyat shunaqa 😁.



[Github](https://github.com/MrAdkhambek/Kotlin-Flow-Example.git)
[Dasturlash_net](https://t.me/dasturlash_net) kanali siz uyda qoling biz sizga dasturlashni o'rgatamiz.

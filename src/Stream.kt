import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


interface Stream<T> {

    fun subscribe(callback: Callback<T>)

    fun unsubscribe(callback: Callback<T>)

    interface Callback<T> {

        fun onNext(item: T) {}

        fun onComplete() {}

        fun onCanceled() {}

        fun onError(error: Throwable) {}
    }
}


fun <T> Stream<T>.asFlow(): Flow<T> = callbackFlow {
    val callback = object : Stream.Callback<T> {

        override fun onNext(item: T) {
            offer(item)
        }

        override fun onComplete() {
            close()
        }

        override fun onCanceled() {
            cancel()
        }

        override fun onError(error: Throwable) {
            close(error)
        }
    }

    subscribe(callback)
    awaitClose { unsubscribe(callback) }
}
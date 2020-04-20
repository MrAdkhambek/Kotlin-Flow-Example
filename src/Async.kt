import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


interface Async<T> {

    fun execute(callback: Callback<T>)

    interface Callback<T> {

        fun onComplete(value: T) {}

        fun onCanceled() {}

        fun onError(error: Throwable) {}
    }
}


suspend fun <T> Async<T>.await(): T = suspendCancellableCoroutine { cont: CancellableContinuation<T> ->
    execute(object : Async.Callback<T> {
        override fun onComplete(value: T) {
            cont.resume(value)
        }

        override fun onCanceled() {
            cont.cancel()
        }

        override fun onError(error: Throwable) {
            cont.resumeWithException(error)
        }
    })
}
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class CustomScope : CoroutineScope {
    private var parentJob = Job()
    private val coroutineErrorHandler = CoroutineExceptionHandler { _, error ->
        println("Problems with Coroutine: ${error}") // we just print the error here
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + parentJob + coroutineErrorHandler


    fun onStart() {
        parentJob = Job()
    }

    fun onStop() {
        parentJob.cancel()
        // You can also cancel the whole scope
        // with `cancel(cause: CancellationException)`
    }
}
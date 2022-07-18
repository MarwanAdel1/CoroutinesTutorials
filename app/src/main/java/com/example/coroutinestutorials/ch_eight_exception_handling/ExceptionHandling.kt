package com.example.coroutinestutorials.ch_eight_exception_handling

import kotlinx.coroutines.*
import java.io.IOException
import kotlin.coroutines.suspendCoroutine

fun main() {
    //createException()
    //invokeException()
    //createCoroutineExceptionHandler()
    //tryCatchRescue()
    //multipleChildCoroutineException()
    callbackWrapping()
}

private fun createException() = runBlocking {
    val asyncJob = GlobalScope.launch/*(start = CoroutineStart.LAZY)*/ {
        println("1. Exception created via launch coroutine")
        // Will be printed to the console by
        // Thread.defaultUncaughtExceptionHandler
        throw IndexOutOfBoundsException()
    }

    delay(10000)
    //asyncJob.join()
    println("2. Joined failed job")
    val deferred = GlobalScope.async {
        println("3. Exception created via async coroutine")
        // Nothing is printed, relying on user to call await
        throw ArithmeticException()
    }
    try {
        deferred.await()
        println("4. Unreachable, this statement is never executed")
    } catch (e: Exception) {
        println("5. Caught ${e.javaClass.simpleName}")
    }
}

private fun invokeException() {
    runBlocking {
        val job = GlobalScope.launch {
            println("1. Exception created via launch coroutine")
            // Will NOT be handled by
            // Thread.defaultUncaughtExceptionHandler
            // since it is being handled later by `invokeOnCompletion`
            throw IndexOutOfBoundsException()
        }

        // Handle the exception thrown from `launch` coroutine builder  **VI -- Used for exception handling
        job.invokeOnCompletion { exception ->
            println("2. Caught $exception")
        }
        // This suspends coroutine until this job is complete.
        job.join()

//        while (job.isActive){
//
//        }
//        delay(1000)
    }
}

private fun createCoroutineExceptionHandler() {
    runBlocking {
        // 1
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            println("Caught $exception")
        }
        // 2
        val job = GlobalScope.launch(exceptionHandler) {
            throw AssertionError("My Custom Assertion Error!")
        }
        // 3
        val deferred = GlobalScope.async(exceptionHandler) {
            // Nothing will be printed,
            // relying on user to call deferred.await()
            delay(1000)
            throw ArithmeticException()
        }
        // 4
        // This suspends current coroutine until all given jobs are complete.
        joinAll(job, deferred.await())
    }
}

private fun tryCatchRescue() = runBlocking {
    // Set this to ’true’ to call await on the deferred variable
    val callAwaitOnDeferred = true
    val deferred = GlobalScope.async {
        // This statement will be printed with or without
        // a call to await()
        println("Throwing exception from async")
        throw ArithmeticException("Something Crashed")
        // Nothing is printed, relying on a call to await()
    }
    if (callAwaitOnDeferred) {
        try {
            deferred.await()
        } catch (e: ArithmeticException) {
            println("Caught ArithmeticException - $e")
        }
    }
}

private fun multipleChildCoroutineException() {
    runBlocking {
        // Global Exception Handler
        val handler = CoroutineExceptionHandler { _, exception ->
            println(
                "Caught $exception with suppressed " +
                        // Get the suppressed exception
                        "${exception.suppressed?.contentToString()}"
            )
        }
        // Parent Job
        val parentJob = GlobalScope.launch(handler) {
            // Child Job 1
            launch {
                try {
                    delay(Long.MAX_VALUE)
                } catch (e: Exception) {
                    println("${e.javaClass.simpleName} in Child Job 1")
                } finally {
                    throw ArithmeticException()
                }
            }
            // Child Job 2
            launch {
                delay(100)
                throw IllegalStateException()
            }
            // Delaying the parentJob
            delay(Long.MAX_VALUE)
        }
        // Wait until parentJob completes
        parentJob.join()
    }
}

private fun callbackWrapping() = runBlocking {
    try {
        val data = getDataAsync()
        println("Data received: $data")
    } catch (e: Exception) {
        println("Caught ${e.javaClass.simpleName}")
    }
}

// Callback Wrapping using Coroutine
private suspend fun getDataAsync(): String {
    return suspendCoroutine { cont ->
        getData(object : AsyncCallback {
            override fun onSuccess(result: String) {
                cont.resumeWith(Result.success(result))
            }

            override fun onError(e: Exception) {
                cont.resumeWith(Result.failure(e))
            }
        })
    }
}

// Method to simulate a long running task
private fun getData(asyncCallback: AsyncCallback) {
    // Flag used to trigger an exception
    val triggerError = false
    try {
        // Delaying the thread for 3 seconds
        Thread.sleep(3000)
        if (triggerError) {
            throw IOException()
        } else {
            // Send success
            asyncCallback.onSuccess("[Beep.Boop.Beep]")
        }
    } catch (e: Exception) {
        // send error
        asyncCallback.onError(e)
    }
}
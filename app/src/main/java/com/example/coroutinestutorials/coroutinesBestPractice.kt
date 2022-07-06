package com.example.coroutinestutorials

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.concurrent.thread
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

fun main() {
//    myFirstGlobalScope()
//    globalScopeBestPractice()
//    cancelTheCurrentJob()
//    jobHierarchy()
//    repeatJob()
//    print(getUserStandard("123").userName)

//    getUserFromNetworkCallback("101") { user ->
//        println(user)
//    }
//    println("main end")

//    getUserFromNetworkCallbackExceptionHandling("101") { user, error ->
//        user?.run(::println)
//        error?.printStackTrace()
//    }


//    GlobalScope.launch {
//        val user = getUserSuspend("101")
//        println(user)
//    }

//    GlobalScope.launch {
//        println(readFileSuspend("Marwan"))
//    }

    GlobalScope.launch {
        println(readFileSuspend(null))
    }

    Thread.sleep(5000)
}

private fun myFirstGlobalScope() {
    (1..10000).forEach {
        GlobalScope.launch {
            val threadName = Thread.currentThread().name
            println("myCoroutines: $it printed on thread $threadName")
        }
    }
}

private fun globalScopeBestPractice() {
    GlobalScope.launch {
        (1..10000).forEach {
            val threadName = Thread.currentThread().name
            println("globalScopeBestPractice: $it printed on thread $threadName")
        }
    }
}

private fun cancelTheCurrentJob() {
    val job = GlobalScope.launch {
        (1..10000).forEach {
            val threadName = Thread.currentThread().name
            println("cancelTheCurrentJob: $it printed on thread $threadName")
        }
    }
    println("main: I am waiting!")
    Thread.sleep(200)
    println("main: I am tired of waiting!")
    job.cancel() // cancels the job
    println("main: Now I can quit.")
}

private fun jobHierarchy() {
    with(GlobalScope) {
        val parentJob = launch {
            delay(200)
            println("I’m the parent")
//            delay(200)
        }
//        Thread.sleep(1000)
        launch(context = parentJob) {
            delay(200)
            println("I’m a child")
//            delay(200)
        }
        if (parentJob.children.iterator().hasNext()) {
            println("The Job has children ${parentJob.children}")
        } else {
            println("The Job has NO children")
        }
    }
}

private fun repeatJob() {
    var isDoorOpen = false
    println("Unlocking the door... please wait.\n")
    GlobalScope.launch {
        delay(3000)
        isDoorOpen = true
    }
    GlobalScope.launch {
        repeat(4) {
            println("Trying to open the door...\n")
            delay(800)
            if (isDoorOpen) {
                println("Opened the door!\n")
            } else {
                println("The door is still locked\n")
            }
        }
    }
}

private fun getUserStandard(userId: String): User {
    Thread.sleep(1000)
    return User(userId, "Filip")
}

private fun getUserFromNetworkCallback(
    userId: String,
    onUserReady: (User) -> Unit
) {
    thread {
        Thread.sleep(1000)
        val user = User(userId, "Filip")
        onUserReady(user)
    }
    println("end")
}

private fun getUserFromNetworkCallbackExceptionHandling(
    userId: String,
    onUserResponse: (User?, Throwable?) -> Unit
) {
    thread {
        try {
            Thread.sleep(1000)
            val user = User(userId, "Filip")
            onUserResponse(user, null)
        } catch (error: Throwable) {
            onUserResponse(null, error)
        }
    }
}

suspend fun getUserSuspend(userId: String): User {
    delay(1000)
    return User(userId, "Filip")
}

suspend fun readFileSuspend(path: String?): String =
    suspendCoroutine {
        readData(path) { file ->
            try {
                it.resume(file!!)  //// resume with exception ----
            }catch (error: Throwable){
                it.resumeWithException(error)
            }
        }
    }

private fun readData(someString: String?, value: (String?) -> Unit) {
    value(someString)
}

package com.example.coroutinestutorials.ch_eight_exception_handling

interface AsyncCallback {
    fun onSuccess(result: String)
    fun onError(e: Exception)
}
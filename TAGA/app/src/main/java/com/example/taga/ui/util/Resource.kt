package com.example.taga.ui.util

sealed class Resource<out T:Any> {
    data class Success<out T:Any>(val data:Any):Resource<T>()
    data class Error(val errorMessage:String):Resource<Nothing>()
    data class Loading<out T:Any>(val data:T? = null, val message:String? = null):Resource<T>()
}
package com.waldo121.pongstats.ui.utils


// represents an asynchronous query
sealed interface QueryState<out T> {
    object Loading: QueryState<Nothing>
    data class Success<T>(val data: T): QueryState<T>
    data class Error(val message: String): QueryState<Nothing>
}
package com.mrf.tghost.domain.model

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

sealed class Result<out T> {
    object Loading : Result<Nothing>()
    data class Success<T>(val data: T) : Result<T>()
    data class Failure(val errorMessage: String?) : Result<Nothing>()
}

@OptIn(ExperimentalContracts::class)
fun <T> Result<T>.isSuccess(): Boolean {
    contract {
        returns(true) implies (this@isSuccess is Result.Success<T>)
    }
    return this is Result.Success<T>
}

@OptIn(ExperimentalContracts::class)
fun <T> Result<T>.isFailure(): Boolean {
    contract {
        returns(true) implies (this@isFailure is Result.Failure)
    }
    return this is Result.Failure
}

@OptIn(ExperimentalContracts::class)
fun <T> Result<T>.isLoading(): Boolean {
    contract {
        returns(true) implies (this@isLoading is Result.Loading)
    }
    return this is Result.Loading
}
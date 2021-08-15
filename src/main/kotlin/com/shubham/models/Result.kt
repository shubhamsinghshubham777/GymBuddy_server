package com.shubham.models

sealed class Result<T> {
    class Success<T>: Result<T>()
    class FailureWithMsg<T>(val msg: String): Result<T>()
    class SuccessWithData<T>(val data: T): Result<T>()
    class SuccessWithListOfData<T>(val data: List<T>): Result<T>()
}

package com.jandroid.pngreduce.extension



sealed class BooleanExt<out T>

object Otherwise : BooleanExt<Nothing>()

class TransferData<T>(val data : T) : BooleanExt<T>()


inline fun <T> Boolean.yes(block : () -> T) : BooleanExt<T> = when {
    this -> TransferData(block.invoke())
    else -> Otherwise
}


inline  fun <T> Boolean.no(block : () -> T) : BooleanExt<T> = when{
    this -> Otherwise
    else -> TransferData(block.invoke())
}

inline fun <T> BooleanExt<T>.otherwise(block: () -> T): T = when (this) {
    is Otherwise ->
        block()
    is TransferData ->
        this.data
}





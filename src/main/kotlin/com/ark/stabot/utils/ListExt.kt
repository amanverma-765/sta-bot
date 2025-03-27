package com.ark.stabot.utils

fun <T> List<T>.toOppString(): String {
    return this.joinToString(",")
}

fun String.toOppList(): MutableList<String> {
    return if (this.isBlank()) mutableListOf() else this.split(",").toMutableList()
}

package com.ark.stabot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class StaBotApplication

fun main(args: Array<String>) {
    runApplication<StaBotApplication>(*args)
}

package com.ark.stabot.utils

import java.util.Base64

fun encodeTmNumber(applicationNumber: String): String {
    // Encode each digit by adding 39 and converting to a character
    val encodedMiddle = applicationNumber.map { ch ->
        val digit = ch.digitToInt()
        (digit + 39).toChar()
    }.joinToString("")

    // Define the wrapper
    val wrapper = "XYZ[\\]"

    // Combine the wrapper, the encoded middle, and the wrapper again
    val combined = wrapper + encodedMiddle + wrapper

    // Base64 encode the final string
    val encodedBytes = Base64.getEncoder().encode(combined.toByteArray(Charsets.UTF_8))
    return String(encodedBytes, Charsets.UTF_8)
}



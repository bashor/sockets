package helloserver

import java.util.Random

val SERVER = "localhost"
val UDP_PORT = 1234
val TCP_PORT = 1235

val MESSAGE_COUNT = 10
val THREAD_MAX_COUNT = 101
val THREAD_MIN_COUNT = 1
val THREAD_STEP = 10

private val random = Random()

fun Random.nextNormal(): Double {
    val s = 1.0
    val s3 = s * 7.0

    val result = (this.nextGaussian() + s3) / (2 * s3)

    if (result > 1.0) {
        System.err.println("bad result == $result")
        return 1.0
    } else if (result < 0.0) {
        System.err.println("bad result == $result")
        return 0.0
    }

    return result
}

fun delay(min: Long, max: Long) = (min + random.nextNormal() * (max - min)).toLong()

package helloserver

import java.io.Closeable
import java.util.ArrayList
import java.util.concurrent.CountDownLatch

private val MAX_ERROR_COUNT = MESSAGE_COUNT * 2;
abstract class HelloClient(val delay: () -> Long,
                           val startLatch: CountDownLatch,
                           val stopLatch: CountDownLatch): Runnable, Closeable {

    val responseTime: ArrayList<Long> = ArrayList<Long>()
    abstract public fun send()
    abstract public fun receive()

    override fun run() {
        startLatch.countDown()
        startLatch.await()

        var errorCount = 0
        var filled = false
        while(true) {
            try {
                send()

                val start = System.currentTimeMillis()
                receive()
                val end = System.currentTimeMillis()

                if (responseTime.size() < MESSAGE_COUNT) {
                    responseTime.add(end - start)
                } else if (!filled) {
                    filled = true
                    stopLatch.countDown()
                }

                try {
                    Thread.sleep(delay())
                } catch (e: InterruptedException) {
                    break;
                }
            } catch (e: Exception) {
                if (++errorCount > MAX_ERROR_COUNT) {
                    System.err.println("beda beda: ${e.getMessage()}")
                    break
                }
            }
        }
    }
}

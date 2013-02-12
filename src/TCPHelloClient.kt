package helloserver.tcp.client

import helloserver.HelloClient
import java.net.Socket

import helloserver.*
import kotlin.concurrent.latch
import kotlin.concurrent.thread
import java.util.Random
import java.util.ArrayList
import java.io.Closeable
import java.util.concurrent.CountDownLatch

class TCPHelloClient(val address: String,
                     val port: Int,
                     delay: () -> Long,
                     startLatch: CountDownLatch,
                     stopLatch: CountDownLatch) : HelloClient(delay, startLatch, stopLatch)
{
    var socket: Socket? = null

    public override fun send() {
        socket = Socket(address, port)
        val output = socket?.getOutputStream()!!.writer()

        output.write("${System.currentTimeMillis()}\n")
        output.flush()
    }

    public override fun receive() {
        val input = socket?.getInputStream()!!.reader().buffered()
        input.readLine()
        socket?.close()
    }

    public override fun close() {}
}

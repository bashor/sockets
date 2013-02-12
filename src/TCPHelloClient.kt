package helloserver.tcp.client

import helloserver.*
import java.net.Socket
import java.util.concurrent.CountDownLatch

class TCPHelloClient(val address: String,
                     val port: Int,
                     delay: () -> Long,
                     startLatch: CountDownLatch,
                     stopLatch: CountDownLatch): HelloClient(delay, startLatch, stopLatch)
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
}

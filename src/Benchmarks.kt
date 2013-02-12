package helloserver

import helloserver.*
import helloserver.tcp.client.TCPHelloClient
import helloserver.tcp.server.TCPHelloServer
import helloserver.udp.client.UDPHelloClient
import helloserver.udp.server.UDPHelloServer
import java.util.concurrent.CountDownLatch

fun benchmark<T: HelloClient>(clientCount: Int,
                              clientCreator: (startLatch: CountDownLatch, stopLatch: CountDownLatch) -> T)
{
    val startLatch = CountDownLatch(clientCount)
    val stopLatch = CountDownLatch(clientCount)

    val clients = Array(clientCount, { clientCreator(startLatch, stopLatch) })
    val threads = Array(clientCount, { Thread(clients[it]) })
    threads forEach { it.start() }

    stopLatch.await()

    threads forEach { it.interrupt() }

    fun Collection<Long>.avg() = if (size == 0) 0 else fold(0: Long) { a, b -> a + b } / size

    val avg = clients.flatMap { it.responseTime }.avg()
    println("$clientCount\t$avg")
}

fun benchmark<S: Runnable, T: HelloClient>(serverCreator: () -> S,
                                           clientCreator: (startLatch: CountDownLatch, stopLatch: CountDownLatch) -> T)
{
    val server = Thread(serverCreator())
    server.start()

    for (i in THREAD_MIN_COUNT..THREAD_MAX_COUNT step THREAD_STEP) {
        benchmark(i, clientCreator)
        Thread.sleep(100)
    }

    server.interrupt()
}

fun main(args: Array<String>) {
    val delay = { delay(50, 150) }
    val delay0 = { 0.toLong() }

    val tcpServer0 = { TCPHelloServer(TCP_PORT, delay0) }
    val tcpServer = { TCPHelloServer(TCP_PORT, delay) }
    println("TCP server delay(0) | client delay(0)")
    benchmark(tcpServer0, { startLatch, stopLatch -> TCPHelloClient(SERVER, TCP_PORT, delay0, startLatch, stopLatch) })
    println("TCP server delay(50..150) | client delay(0)")
    benchmark(tcpServer, { startLatch, stopLatch -> TCPHelloClient(SERVER, TCP_PORT, delay0, startLatch, stopLatch) })
    println("TCP server delay(50..150) | client delay(50..150)")
    benchmark(tcpServer, { startLatch, stopLatch -> TCPHelloClient(SERVER, TCP_PORT, delay, startLatch, stopLatch) })

    val udpServer0 = { UDPHelloServer(UDP_PORT, delay0) }
    val udpServer = { UDPHelloServer(UDP_PORT, delay) }
    println("UDP server delay(0) | client delay(0)")
    benchmark(udpServer0, { startLatch, stopLatch -> UDPHelloClient(SERVER, UDP_PORT, delay0, startLatch, stopLatch) })
    println("UDP server delay(50..150) | client delay(0)")
    benchmark(udpServer, { startLatch, stopLatch -> UDPHelloClient(SERVER, UDP_PORT, delay0, startLatch, stopLatch) })
    println("UDP server delay(50..150) | client delay(50..150)")
    benchmark(udpServer, { startLatch, stopLatch -> UDPHelloClient(SERVER, UDP_PORT, delay, startLatch, stopLatch) })
}
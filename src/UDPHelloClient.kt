package helloserver.udp.client

import helloserver.*
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.concurrent.CountDownLatch

class UDPHelloClient(val address: String,
                     val port: Int,
                     delay: () -> Long,
                     startLatch: CountDownLatch,
                     stopLatch: CountDownLatch): HelloClient(delay, startLatch, stopLatch) {

    var socket = DatagramSocket()
    val buffer = ByteArray(1024)

    public override fun send() {
        val packet = DatagramPacket(buffer, buffer.size, InetAddress.getByName(address), port)
        packet.setData(System.currentTimeMillis().toString().getBytes())
        socket.send(packet)
    }
    public override fun receive() {
        val respPacket = DatagramPacket(buffer, buffer.size)
        socket.receive(respPacket)
    }
}

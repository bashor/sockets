package helloserver.udp.server

import helloserver.*
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.util.Random

class UDPHelloServer(val port: Int, val delay: () -> Long) : Runnable {
    override fun run() {
        val socket = DatagramSocket(port)
        while (true)
        {
            val data = ByteArray(1024)
            val packet = DatagramPacket(data, (data.size))
            socket.receive(packet)
            val message = String(packet.getData()!!)

            try {
                Thread.sleep(delay())
            } catch (e: InterruptedException) {
                break;
            }

            val clientAddress = packet.getAddress()
            val clientPort: Int = packet.getPort()
            val response = "Hello ${message.split(" ")[0]} ${System.currentTimeMillis()}".getBytes()

            socket.send(DatagramPacket(response, response.size, clientAddress, clientPort))
        }

        socket.close()
    }
}


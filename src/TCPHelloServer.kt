package helloserver.tcp.server

import javax.net.ServerSocketFactory

class TCPHelloServer(val port: Int, val delay: () -> Long): Runnable {
    override fun run() {
        val serverSocket = ServerSocketFactory.getDefault()!!.createServerSocket(port)
        while (true) {
            val socket = serverSocket.accept()

            val input = socket.getInputStream()!!.reader().buffered()
            val output = socket.getOutputStream()!!.writer()

            val message = input.readLine()!!

            try {
                Thread.sleep(delay())
            } catch (e: InterruptedException) {
                break;
            }

            output.write("Hello ${message.split(" ")[0]} ${System.currentTimeMillis()}\n")
            output.flush()

            socket.close()
        }

        serverSocket.close()
    }
}


import java.io.File
import java.nio.charset.Charset
import kotlin.concurrent.thread

fun main(args: Array<String>) {
//    val keepAlive = KeepAlive()
////    keepAlive.changeMousePos(0, 0, 500, 500)
////    keepAlive.mimic()
//
//    if(System.getProperty("os.name").lowercase(Locale.getDefault()) == "linux") {
//        println("Program running on Linux.")
//    }
//
//    keepAlive.shutDown()
    KeyEventHandler().runKeyEventThread()
    thread {
        Thread.sleep(5000)
        val fifoOutput = File("/tmp/keyevent.fifo").readLines(Charset.defaultCharset()).toString()
        println("Fifo output: $fifoOutput")
    }
}
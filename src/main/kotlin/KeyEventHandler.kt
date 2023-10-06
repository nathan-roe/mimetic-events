import java.io.File
import java.nio.charset.Charset
import kotlin.concurrent.thread

class KeyEventHandler(val pollIntervalInMillis: Long = 5000) {
    private val fifoPath = "/tmp/keyevent.fifo"
    private var keyCodes = ArrayList<String>()

    init {
        System.loadLibrary("keyevent")
    }
    private external fun captureKeyEvents()

    fun getKeyEvents() {
        thread {
            KeyEventHandler().captureKeyEvents()
        }
        pollFifoFile()

    }

    private fun pollFifoFile() {
        thread {
            Thread.sleep(pollIntervalInMillis)
            val fifoOutput = File(fifoPath).readLines(Charset.defaultCharset()).toString()
            println("Fifo output: $fifoOutput")
            if(fifoOutput.length != keyCodes.size) {
                keyCodes = fifoOutput.split("\\s.*".toRegex()) as ArrayList<String>
            }
        }
    }

}
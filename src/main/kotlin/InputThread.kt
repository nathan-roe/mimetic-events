import java.util.*


internal class InputThread() {
    fun run() {
        val sc = Scanner(System.`in`)
        while (sc.hasNextLine()) {
            // blocks for input, but won't block the server's thread
        }
    }
}
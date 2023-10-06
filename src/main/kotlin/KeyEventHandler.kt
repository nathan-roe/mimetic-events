import kotlin.concurrent.thread

class KeyEventHandler {
    init {
        System.loadLibrary("keyevent")
    }
    private external fun captureKeyEvents()

    fun runKeyEventThread() {
        thread {
            KeyEventHandler().captureKeyEvents()
        }
    }

}
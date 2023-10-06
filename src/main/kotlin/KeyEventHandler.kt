import kotlin.concurrent.thread

class KeyEventHandler {
    private val keyEvents: Array<KeyEvent> = emptyArray()

    init {
        System.loadLibrary("keyevent")
    }
    private external fun captureKeyEvents()
    private external fun retrieveKeyEvents()

    fun getKeyEvents() {
        thread {
            println("Capturing key events...")
            captureKeyEvents()
        }

        thread {
            Thread.sleep(5000)
            println("Retrieving key events")
            retrieveKeyEvents()
            Thread.sleep(2000)
            keyEvents.map {
                println(String.format("key code: %s, key state: %s, event time: %s", it.keyCode, it.keyState, it.eventTime))
            }
        }
    }
}
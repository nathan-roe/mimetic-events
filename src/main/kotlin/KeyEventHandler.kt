import kotlin.concurrent.thread

class KeyEventHandler : EventHandler {
    override val events = ArrayList<KeyEvent>()

    init {
        System.loadLibrary("keyevent")
    }
    external override fun captureEvents(startTimeMillis: Long)
    private external fun retrieveKeyEvents()

    override fun getEvents() {
        thread {
            Thread.sleep(5000)
            println("Retrieving key events")
            retrieveKeyEvents()
            Thread.sleep(2000)
            events.map {
                println(String.format("key code: %s, key state: %s, event time: %s", it.keyCode, it.keyState, it.eventTime))
            }
        }
    }
}
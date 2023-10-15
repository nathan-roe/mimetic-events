class KeyEventHandler : EventHandler {
    private var capturingKeys = true
    private var keyEvents = arrayOf<KeyEvent>()

    init {
        System.loadLibrary("keyevent")
    }
    external override fun captureEvents(startTimeMillis: Long)
    private external fun retrieveKeyEvents()

    override fun retrieveEvents(): List<Event> {
        retrieveKeyEvents()
        capturingKeys = false
        println(String.format("Retrieved %s key events", keyEvents.size))
        keyEvents.map {
            println(String.format("key code: %s, key state: %s, event time: %s", it.keyCode, it.keyState, it.eventTime))
        }
        return keyEvents.asList()
    }
}
class KeyEventHandler : EventHandler {
    override val events: List<KeyEvent> = mutableListOf()

    init {
        System.loadLibrary("keyevent")
    }
    external override fun captureEvents(startTimeMillis: Long)
    private external fun retrieveKeyEvents()

    override fun retrieveEvents(): List<Event> {
        retrieveKeyEvents()
        println(String.format("Retrieved %s key events", events.size))
        events.map {
            println(String.format("key code: %s, key state: %s, event time: %s", it.keyCode, it.keyState, it.eventTime))
        }
        return events
    }
}
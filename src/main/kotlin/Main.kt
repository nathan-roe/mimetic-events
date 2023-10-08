import kotlin.concurrent.thread

fun main() {
    val currentTimeMillis = System.currentTimeMillis()
    val keyEventHandler = KeyEventHandler()
    val mouseEventHandler = MouseEventHandler()

    println("Started listening for 5 seconds: ")

    thread { keyEventHandler.captureEvents(currentTimeMillis) }
    thread { mouseEventHandler.captureEvents(currentTimeMillis) }

    thread {
        Thread.sleep(5000)

        println("Getting key events")
        val keyEvents = keyEventHandler.retrieveEvents()
        println("Getting mouse events")
        val mouseEvents = mouseEventHandler.retrieveEvents()
        val capturedEvents = (keyEvents + mouseEvents).sortedBy { it.eventTime }

        println(String.format("Captured %s events", capturedEvents.size))
        var prevEventTime = 0L
        capturedEvents.forEach {
            Thread.sleep(it.eventTime - prevEventTime)
            it.mimic()
            prevEventTime = it.eventTime
        }
        println(String.format("key events: %s", keyEventHandler.events.size))
        println(String.format("mouse events: %s", mouseEventHandler.events.size))
    }
}
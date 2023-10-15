import kotlin.concurrent.thread

fun main() {
    val currentTimeMillis = System.currentTimeMillis()
    val keyEventHandler = KeyEventHandler()
    val mouseEventHandler = MouseEventHandler()

    println("Started listening for 5 seconds: ")

    thread(name = "Capture kbd event") { keyEventHandler.captureEvents(currentTimeMillis) }
    thread(name = "Capture mouse event") { mouseEventHandler.captureEvents(currentTimeMillis) }

    thread(name = "Mimetic event") {
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

        println(String.format("key events: %s", keyEvents.size))
        println(String.format("mouse events: %s", mouseEvents.size))
    }
}
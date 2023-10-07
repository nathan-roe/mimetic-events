import kotlin.concurrent.thread

fun main(args: Array<String>) {
    System.setProperty("java.awt.headless", "false");
    val currentTimeMillis = System.currentTimeMillis()
    thread {
        val keyEventHandler = KeyEventHandler()
        keyEventHandler.captureEvents(currentTimeMillis)
        Thread.sleep(5000)
        keyEventHandler.getEvents()
    }
    thread {
        val mouseEventHandler = MouseEventHandler()
        mouseEventHandler.captureEvents(currentTimeMillis)
        Thread.sleep(5000)
        mouseEventHandler.getEvents()
    }
}
import java.awt.MouseInfo
import java.awt.Robot
import java.util.ArrayList

class MouseEventHandler : EventHandler {
    private var robot: Robot = Robot()
    override val events = ArrayList<MouseEvent>()
    private var trackingMouseEvents: Boolean = true

    fun changeMousePos(startX: Int, startY: Int, endX: Int, endY: Int, delayInMs: Int = 2) {
        robot.mouseMove(startX, startY)
        var posX = startX
        var posY = startY
        val xStep = if(endX > posX) +1 else -1
        val yStep = if(endY > posY) +1 else -1
        while(startX != endX && startY != endY) {
            posX += xStep
            posY += yStep
            robot.delay(delayInMs)
            robot.mouseMove(posX, posY)
            println(String.format("Mouse position is at: x: %s, y: %s", posX, posY))
            if(posX == endX && posY == endY) {
                break;
            }
        }
        println("Finished moving the mouse")
    }

    override fun captureEvents(startTimeMillis: Long) {
        var mouseLocation = MouseInfo.getPointerInfo().location
        events.add(MouseEvent(mouseLocation.x, mouseLocation.y, System.currentTimeMillis()))

        while(trackingMouseEvents) {
            println("In while loop...")
            mouseLocation = MouseInfo.getPointerInfo().location
            println(String.format("posX: %s, x: %s, posY: %s, y: %s", events.last().posX, mouseLocation.x, events.last().posY, mouseLocation.y))
            if(
                events.last().posX != mouseLocation.x
                || events.last().posY != mouseLocation.y
            ) {
                println("adding coordinates...")
                events.add(MouseEvent(mouseLocation.x, mouseLocation.y, System.currentTimeMillis()))
            }
        }
    }

    override fun getEvents() {
        events.forEach {
            println(String.format("posX: %s, posY: %s", it.posX, it.posY))
        }
        println("Repeating previous mouse movements: ")
        events.forEach {
            println(String.format("Mouse position at: x: %s, y: %s", it.posX, it.posY))
            robot.mouseMove(it.posX, it.posY)
            val delayInMs = it.eventTime - events[(events.indexOf(it) - 1).coerceAtLeast(0)].eventTime
            robot.delay(delayInMs.toInt())
        }
    }
}
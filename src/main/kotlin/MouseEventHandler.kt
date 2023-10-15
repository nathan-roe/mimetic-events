import java.awt.MouseInfo
import java.awt.Robot

class MouseEventHandler : EventHandler {
    private var robot: Robot = Robot()
    private var mouseEvents: List<MouseEvent> = mutableListOf()
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
        mouseEvents.plus(MouseEvent(mouseLocation.x, mouseLocation.y, System.currentTimeMillis()))

        while(trackingMouseEvents) {
            mouseLocation = MouseInfo.getPointerInfo().location
            if(
                mouseEvents.isEmpty()
                || mouseEvents.last().posX != mouseLocation.x
                || mouseEvents.last().posY != mouseLocation.y
            ) {
                mouseEvents = mouseEvents.plus(MouseEvent(mouseLocation.x, mouseLocation.y, System.currentTimeMillis()))
            }
        }
    }

    override fun retrieveEvents(): List<MouseEvent> {
        println(String.format("Retrieved %s mouse events", mouseEvents.size))
        trackingMouseEvents = false
        mouseEvents.map {
            println(String.format("posX: %s, posY: %s, event time: %s", it.posX, it.posY, it.eventTime))
        }
        return mouseEvents
    }
}
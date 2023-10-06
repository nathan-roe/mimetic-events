import java.awt.MouseInfo
import java.awt.Robot
import java.awt.event.InputEvent
import java.util.*


class KeepAlive {
    private var robot: Robot = Robot()

    fun shutDown() {
        robot.mouseMove(79, 797)
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
    }


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

    fun mimic() {
        val mouseRoute = ArrayList<MouseCoordinates>()
        var mouseLocation = MouseInfo.getPointerInfo().location
        mouseRoute.add(MouseCoordinates(mouseLocation.x, mouseLocation.y, System.currentTimeMillis()))

        while(true) {
            println("In while loop...")
            mouseLocation = MouseInfo.getPointerInfo().location
            println(String.format("posX: %s, x: %s, posY: %s, y: %s", mouseRoute.last().posX, mouseLocation.x, mouseRoute.last().posY, mouseLocation.y))
            if(
                mouseRoute.last().posX != mouseLocation.x
                || mouseRoute.last().posY != mouseLocation.y
            ) {
                println("adding coordinates...")
                mouseRoute.add(MouseCoordinates(mouseLocation.x, mouseLocation.y, System.currentTimeMillis()))
            }

            if(mouseRoute.size >= 100) {
                break
            }
        }

        mouseRoute.forEach {
            println(String.format("posX: %s, posY: %s", it.posX, it.posY))
        }
        println("Repeating previous mouse movements: ")
        mouseRoute.forEach {
            println(String.format("Mouse position at: x: %s, y: %s", it.posX, it.posY))
            robot.mouseMove(it.posX, it.posY)
            val delayInMs = it.timeRecorded - mouseRoute[(mouseRoute.indexOf(it) - 1).coerceAtLeast(0)].timeRecorded
            robot.delay(delayInMs.toInt())
        }


    }
}
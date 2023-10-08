import java.awt.Robot

class KeyEvent (
    val keyCode: Int,
    val keyState: Int,
    override val eventTime: Long
) : Event {
    override val robot = Robot()
    override fun mimic() {
        println(String.format("Mimic key event: keyCode: %s, keyState: %s, eventTime: %s", keyCode, keyState, eventTime))
        if(keyState != 0) robot.keyPress(keyCode)
        else robot.keyRelease(keyCode)
    }
}
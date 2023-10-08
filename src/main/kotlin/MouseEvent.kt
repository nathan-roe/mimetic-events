import java.awt.Robot

class MouseEvent(
    val posX: Int,
    val posY: Int,
    override val eventTime: Long
) : Event {
    override val robot = Robot()
    override fun mimic() {
        println(String.format("Mimic mouse event: x: %s, y: %s", this.posX, this.posY))
        robot.mouseMove(this.posX, this.posY)
    }
}
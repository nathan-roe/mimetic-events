import java.awt.Robot

interface Event {
    val eventTime: Long
    val robot: Robot

    fun mimic()
}
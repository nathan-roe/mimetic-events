import Event

class KeyEvent (
    val keyCode: Int,
    val keyState: Int,
    override val eventTime: Long
) : Event
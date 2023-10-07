import Event

class MouseEvent(
    val posX: Int,
    val posY: Int,
    override val eventTime: Long
) : Event
interface EventHandler {
    val events: Collection<Event>
    fun captureEvents(startTimeMillis: Long)
    fun retrieveEvents(): Collection<Event>
}
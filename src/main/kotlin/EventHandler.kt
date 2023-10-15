interface EventHandler {
    fun captureEvents(startTimeMillis: Long)
    fun retrieveEvents(): Collection<Event>
}
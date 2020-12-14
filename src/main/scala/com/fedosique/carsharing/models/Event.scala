package com.fedosique.carsharing.models

import java.time.Instant
import java.util.UUID

case class Event(id: UUID, event: String, carId: UUID, userId: UUID, location: Location, timestamp: Instant)

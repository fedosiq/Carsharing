package com.fedosique.carsharing.logic

import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.stream.{Materializer, OverflowStrategy}
import akka.stream.scaladsl.{BroadcastHub, Keep, Source}

import scala.concurrent.duration.DurationInt

class CarServiceImpl(implicit materializer: Materializer) {
  val (queue, source) =
    Source
      .queue[ServerSentEvent](100, OverflowStrategy.fail)
      .toMat(BroadcastHub.sink)(Keep.both)
      .run()

  val eventSource = source.keepAlive(2.second, () => ServerSentEvent("ping", "eventName", "id"))
}

package com.fedosique.carsharing

import com.fedosique.carsharing.model.Location

import scala.math._


object DistanceCalculator {

  private val RADIUS_OF_EARTH_KM = 6371

  def calculateDistanceInKM(loc1: Location, loc2: Location): Double = {
    val latDistance = toRadians(loc1.lat - loc2.lat)

    val lngDistance = toRadians(loc1.lon - loc2.lon)

    val sinLat = sin(latDistance / 2)

    val sinLng = sin(lngDistance / 2)

    val a = sinLat * sinLat + (cos(toRadians(loc1.lat)) * cos(toRadians(loc2.lat)) * sinLng * sinLng)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    RADIUS_OF_EARTH_KM * c

  }
}

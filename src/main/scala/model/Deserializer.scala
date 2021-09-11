package it.unibo.pps.caw.model

/* mock deserializer */
object Deserializer {
  def deserializeLevel(jsonStringLevel: String): Either[IllegalArgumentException, Level] = Right(Level(jsonStringLevel))
}

package it.unibo.pps.caw.game

import it.unibo.pps.caw.game.model.Level

import java.nio.file.Path
import scala.io.Source
import scala.util.{Try, Using}

/** Module containing an utility method for loading [[Level]] files.
  *
  * This module contains only one helper method which is useful when deserializing a file containing a [[Level]] for trasforming
  * it into an instance of that class.
  */
object LevelLoader {

  /** Deserializes the file associated to the given [[Path]] producing a [[Level]] object which represents the [[Level]] contained
    * into the file itself. This operation can fail and, as such, the object is wrapped inside a [[Try]].
    *
    * @param path
    *   the [[Path]] to the file to deserialize
    * @return
    *   the [[Level]] result from deserializing the given file wrapped in a [[Try]]
    */
  def load(path: Path): Try[Level] =
    for {
      f <- Using(Source.fromFile(path.toFile))(_.getLines.mkString)
      l <- Deserializer.deserializeLevel(f)
    } yield l
}

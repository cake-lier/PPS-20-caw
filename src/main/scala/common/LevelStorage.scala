package it.unibo.pps.caw.common

import it.unibo.pps.caw.common.model.Level
import it.unibo.pps.caw.editor.model.LevelBuilder as EditorLevel
import it.unibo.pps.caw.common.model.cell.BaseCell

import java.io.File
import java.nio.file.{Files, Path, Paths}
import scala.io.Source
import scala.util.{Try, Using}

/** Module containing an utility method for loading [[Level]] files.
  *
  * This module contains only one helper method which is useful when deserializing a file containing a [[Level]] for trasforming
  * it into an instance of that class.
  */
trait LevelStorage {
  /** Deserializes the file associated to the given [[Path]] producing a [[Level]] object which represents the [[Level]] contained
    * into the file itself. This operation can fail and, as such, the object is wrapped inside a [[Try]].
    *
    * @param path
    *   the [[Path]] to the file to deserialize
    * @return
    *   the [[Level]] result from deserializing the given file wrapped in a [[Try]]
    */
  def loadLevel(path: String): Try[Level[BaseCell]]

  /** Save the [[Level]] to disk, in the specified path.
    *
    * @param path
    *   the path of the level file to save to
    * @param level
    *   the [[Level]] instance to be saved
    * @return
    *   an [[java.io.IOException]] if the file can't be saved
    */
  def saveLevel(path: String, level: Level[BaseCell]): Try[Unit]
}

object LevelStorage {

  private class LevelStorageImpl(levelParser: LevelParser) extends LevelStorage {
    def loadLevel(path: String): Try[Level[BaseCell]] =
      for {
        f <- Using(Source.fromFile(path))(_.getLines.mkString)
        l <- levelParser.deserializeLevel(f)
      } yield l

    def saveLevel(path: String, level: Level[BaseCell]): Try[Unit] =
      for {
        s <- levelParser.serializeLevel(level)
        _ <- Try(Files.writeString(Paths.get(path), s))
      } yield ()
  }

  def apply(levelParser: LevelParser): LevelStorage = LevelStorageImpl(levelParser)

}

package it.unibo.pps.caw.game

import it.unibo.pps.caw.editor.controller.Serializer
import it.unibo.pps.caw.editor.controller.Deserializer as EditorDeserializer
import it.unibo.pps.caw.game.controller.Deserializer as GameDeserializer
import it.unibo.pps.caw.editor.model.{Level as EditorLevel}
import it.unibo.pps.caw.game.model.Level as GameLevel

import java.io.File
import java.nio.file.{Files, Path}
import scala.io.Source
import scala.util.{Failure, Try, Using}

/** Module containing an utility method for loading [[Level]] files.
  *
  * This module contains only one helper method which is useful when deserializing a file containing a [[Level]] for trasforming
  * it into an instance of that class.
  */
object LevelManager {

  /** Deserializes the file associated to the given [[Path]] producing a [[Level]] object which represents the [[Level]] contained
    * into the file itself. This operation can fail and, as such, the object is wrapped inside a [[Try]].
    *
    * @param path
    *   the [[Path]] to the file to deserialize
    * @return
    *   the [[Level]] result from deserializing the given file wrapped in a [[Try]]
    */
  def load(path: Path): Try[GameLevel] =
    for {
      f <- Using(Source.fromFile(path.toFile))(_.getLines.mkString)
      l <- GameDeserializer.deserializeLevel(f)
    } yield l

  def loadLevelLevelEditor(level: File): Try[EditorLevel] =
    for {
      f <- Using(Source.fromFile(level))(_.getLines().mkString)
      l <- EditorDeserializer.deserializeLevel(f)
    } yield l

  def writeLevel(file: File, level: EditorLevel): Try[Unit] =
    for {
      s <- Serializer.serializeLevel(level)
      _ <- Try(Files.write(file.toPath, s.getBytes))
    } yield ()

}

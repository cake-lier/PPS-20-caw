package it.unibo.pps.caw.common.storage

import it.unibo.pps.caw.common.model.Level
import it.unibo.pps.caw.common.LevelParser
import it.unibo.pps.caw.common.model.cell.BaseCell

import java.io.File
import java.nio.file.{Files, Path, Paths}
import scala.util.Try

/** Represents the storage of [[Level]] files to disk.
  *
  * This component allows to load from and save to disk files containing game levels. This means that not only it retrieve or
  * store them on disk, but it also performs the serialization operation. It must be constructed through its companion object.
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
    *   an exception if it occurs during IO operations
    */
  def saveLevel(path: String, level: Level[BaseCell]): Try[Unit]
}

/** Companion object to the [[LevelStorage]] trait, containing its factory method. */
object LevelStorage {

  /* Default implementation of the LevelStorage trait. */
  private class LevelStorageImpl(fileStorage: FileStorage, levelParser: LevelParser) extends LevelStorage {

    override def loadLevel(path: String): Try[Level[BaseCell]] =
      for {
        f <- fileStorage.loadFile(path)
        l <- levelParser.deserializeLevel(f)
      } yield l

    override def saveLevel(path: String, level: Level[BaseCell]): Try[Unit] =
      fileStorage.writeFile(path, levelParser.serializeLevel(level))
  }

  /** Returns a new instance of the [[LevelStorage]] trait given the [[FileStorage]] from which retrieving and storing the files
    * on disk and the [[LevelParser]] to be used for serializing and deserializing the contents of those files.
    *
    * @param fileStorage
    *   the storage to be used for retrieving or storing files on disk
    * @param levelParser
    *   the parser to be used for serializing or deserializing the contents of the files
    * @return
    *   a new [[LevelStorage]] instance
    */
  def apply(fileStorage: FileStorage, levelParser: LevelParser): LevelStorage = LevelStorageImpl(fileStorage, levelParser)
}

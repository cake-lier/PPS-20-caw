package it.unibo.pps.caw.common.storage

import java.nio.file.{Files, OpenOption, Paths}
import scala.io.Source
import scala.util.{Try, Using}

/** Represents the storage of files on disk.
  *
  * This entity allows to load project resources or external files from disk or to store external files from disk. It must be
  * constructed through its companion object.
  */
trait FileStorage {

  /** Loads a resource from disk. It needs the path to the resource and returns a [[scala.util.Try]] with the content of the file
    * in a string if the loading was successful, an exception otherwise.
    *
    * @param path
    *   the path to the resource file to load
    * @return
    *   a [[scala.util.Try]] with the contents of the file in a string
    */
  def loadResource(path: String): Try[String]

  /** Loads a file from disk. It needs the absolute path to the file and returns a [[scala.util.Try]] with the content of the file
    * in a string if the loading was successful, an exception otherwise.
    *
    * @param path
    *   the path to the file to load
    * @return
    *   a [[scala.util.Try]] with the contents of the file in a string
    */
  def loadFile(path: String): Try[String]

  /** Writes a file to disk. It needs the absolute path to the file and returns an exception if it occurs. If the file does not
    * exist, it is created. If the file already exists, it gets overwritten.
    *
    * @param path
    *   the path to the file to be written
    * @param content
    *   the content of the file to be written
    * @return
    *   an exception if it occurs during IO operations
    */
  def writeFile(path: String, content: String): Try[Unit]
}

/** Companion object to the [[FileStorage]] trait, containing its factory method. */
object FileStorage {

  /* Default implementation of the FileStorage trait. */
  private class FileStorageImpl extends FileStorage {

    override def loadResource(path: String): Try[String] = Using(Source.fromResource(path))(_.getLines.mkString(" "))

    override def loadFile(path: String): Try[String] = Using(Source.fromFile(path))(_.getLines.mkString(" "))

    override def writeFile(path: String, content: String): Try[Unit] =
      Try(Files.writeString(Paths.get(path), content))
  }

  /** Returns a new instance of the [[FileStorage]] trait. */
  def apply(): FileStorage = FileStorageImpl()
}

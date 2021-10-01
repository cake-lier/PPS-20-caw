package it.unibo.pps.caw.common

import scala.io.Source
import scala.util.{Try, Using}

/** Represents the storage of files to disk */
trait FileStorage {
  /** Loads a resource from disk. It needs the path to the resource and returns a [[Try]] with the content of the file
    * in a string if the loading was successful, an exception otherwise.
    *
    * @param path
    *   the path to the resource file to load
    * @return
    *   a [[Try]] with the contents of the file in a string
    */
  def loadResource(path: String): Try[String]

  /** Loads a file from disk. It needs the absolute path to the file and returns a [[Try]] with the content of the file
    * in a string if the loading was successful, an exception otherwise.
    *
    * @param path
    *   the path to the file to load
    * @return
    *   a [[Try]] with the contents of the file in a string
    */
  def loadFile(path: String): Try[String]
}

object FileStorage {

  private class FileStorageImpl() extends FileStorage {

    def loadResource(path: String): Try[String] = Using(Source.fromResource(path))(_.getLines.mkString)

    def loadFile(path: String): Try[String] = Using(Source.fromFile(path))(_.getLines.mkString)

  }

  def apply():FileStorage = FileStorageImpl()
}

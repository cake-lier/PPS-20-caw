package it.unibo.pps.caw

import scala.io.Source
import scala.util.{Try, Using}

/** Module containing utility methods for loading a resource file from disk.
  *
  * This object is a module containing useful methods to be used when loading a resource from disk, factorizing the calls of the
  * same methods.
  */
object Loader {

  /** Loads a resource from disk. It needs the path to the resource and returns a [[Try]] with the content of the file in a string
    * if the loading was successful, an exception otherwise.
    *
    * @param path
    *   the path to the resource file to load
    * @return
    *   a [[Try]] with the contents of the file in a string
    */
  def load(path: String): Try[String] =
    Using(Source.fromInputStream(ClassLoader.getSystemResourceAsStream(path)))(_.getLines.mkString)

  //TODO: rename load -> loadResource, and loadAbsolute -> load

  /** Loads a file from disk. It needs the absolute path to the file and returns a [[Try]] with the content of the file in a string
    * if the loading was successful, an exception otherwise.
    *
    * @param path
    *   the path to the file to load
    * @return
    *   a [[Try]] with the contents of the file in a string
    */
  def loadAbsolute(path: String): Try[String] =
    Using(Source.fromFile(path))(_.getLines.mkString)
}

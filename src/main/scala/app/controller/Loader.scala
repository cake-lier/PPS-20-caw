package it.unibo.pps.caw.app.controller

import it.unibo.pps.caw.game.model.{Deserializer, Level}

import java.io.File
import scala.io.Source
import scala.util.{Failure, Success, Try, Using}

object Loader {
  def loadLevel(file: File): Try[Level] = Using(Source.fromFile(file))(_.getLines.mkString) match {
    case Success(stringLevel) =>
      Deserializer.deserializeLevel(stringLevel) match {
        case Right(level) => Success(level)
        case Left(e) => {
          Console.err.println("Failed to deserialize level")
          Failure(e)
        }
      }
    case Failure(e) => {
      Console.err.println("Failed to load level")
      Failure(e)
    }
  }
}

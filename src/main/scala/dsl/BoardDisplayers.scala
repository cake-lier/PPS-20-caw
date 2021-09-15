package it.unibo.pps.caw.dsl

import it.unibo.pps.caw.dsl.entities.{BoardBuilder, Board}

import scala.collection.mutable.ListBuffer

/** Adds all methods to the DSL that are able to display a built [[Board]].
  *
  * The methods in this module are capable of displaying a [[Board]], after building it, in different ways. They can display it on
  * a console or saving it to a file, but always after checking the correctness of the data stored by the user and serializing it
  * in JSON format.
  */
trait BoardDisplayers {
  import it.unibo.pps.caw.dsl.errors.ErrorChecker

  /* Generalizes the behavior of the methods in this module. */
  private def executeAction(ops: ListBuffer[BoardBuilder => BoardBuilder])(action: Board => Unit): Unit =
    ops += (b => {
      ErrorChecker.checkBuilderData(b) match {
        case Right(v) => action(v)
        case Left(l)  => Console.err.print(l.map(_.message).mkString("\n"))
      }
      b
    })

  import it.unibo.pps.caw.dsl.BoardSerializer

  /** Prints a built [[Board]] on the standard output after checking the correctness of the stored data and serializing it in JSON
    * format.
    *
    * @param ops
    *   the list of operations to which add this specific operation
    */
  def printIt(using ops: ListBuffer[BoardBuilder => BoardBuilder]): Unit =
    executeAction(ops)(b => print(BoardSerializer.serialize(b)))

  import java.nio.file.Paths
  import java.nio.file.{Files, StandardOpenOption}

  /** Saves a built [[Board]] on the file which path is given after checking the correctness of the stored data and serializing it
    * in JSON format. If the file already exists, for safeness reasons this operation will fail.
    *
    * @param path
    *   the path of the file to which save the built [[Board]]
    * @param ops
    *   the list of operations to which add this specific operation
    */
  def saveIt(path: String)(using ops: ListBuffer[BoardBuilder => BoardBuilder]): Unit =
    executeAction(ops)(b => Files.writeString(Paths.get(path), BoardSerializer.serialize(b), StandardOpenOption.CREATE_NEW))
}
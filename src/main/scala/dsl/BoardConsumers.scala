package it.unibo.pps.caw.dsl

import it.unibo.pps.caw.dsl.entities.{BoardBuilder, Board}

import scala.collection.mutable.ListBuffer

trait BoardConsumers {
  import it.unibo.pps.caw.dsl.errors.ErrorChecker

  private def executeAction(ops: ListBuffer[BoardBuilder => BoardBuilder])(action: Board => Unit): Unit =
    ops += (b => {
      ErrorChecker.checkBoard(b) match {
        case Right(v) => action(v)
        case Left(e)  => Console.err.print(e.message)
      }
      b
    })

  import it.unibo.pps.caw.dsl.BoardSerializer

  def printIt(using ops: ListBuffer[BoardBuilder => BoardBuilder]): Unit =
    executeAction(ops)(b => print(BoardSerializer.serialize(b)))

  import java.nio.file.Paths
  import java.nio.file.{Files, StandardOpenOption}

  def saveIt(path: String)(using ops: ListBuffer[BoardBuilder => BoardBuilder]): Unit =
    executeAction(ops)(b => Files.writeString(Paths.get(path), BoardSerializer.serialize(b), StandardOpenOption.CREATE_NEW))
}

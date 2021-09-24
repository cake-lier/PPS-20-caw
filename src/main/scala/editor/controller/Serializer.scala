package it.unibo.pps.caw.editor.controller

import it.unibo.pps.caw.common.Position
import it.unibo.pps.caw.editor.model.*
import play.api.libs.json.*

import scala.util.Try

object Serializer {
  def serializeLevel(level: Level): Try[String] =
    Try {
      Json.prettyPrint(
        JsObject(
          Seq(
            "width" -> JsNumber(level.width),
            "height" -> JsNumber(level.height),
            "playableArea" -> JsObject(
              Seq(
                "width" -> JsNumber(level.playableArea.get.width),
                "height" -> JsNumber(level.playableArea.get.height),
                "x" -> JsNumber(level.playableArea.get.position.x),
                "y" -> JsNumber(level.playableArea.get.position.y)
              )
            ),
            "cells" -> parseCells(level.board.cells)
          )
        )
      )
    }

  def parseCell(cell: SetupCell): JsObject = {
    val position: Position = cell.position
    val attributes: Seq[(String, JsValue)] =
      Seq("x" -> JsNumber(position.x), "y" -> JsNumber(position.y)) :++ (cell match {
        case SetupRotatorCell(_, r, _)   => Seq("rotation" -> JsString(r.rotation))
        case SetupGeneratorCell(_, o, _) => Seq("orientation" -> JsString(o.orientation))
        case SetupMoverCell(_, d, _)     => Seq("orientation" -> JsString(d.orientation))
        case SetupBlockCell(_, p, _)     => Seq("push" -> JsString(p.push))
        case _                           => Seq.empty
      })
    JsObject(attributes)
  }

  def parseCells(cell: Set[SetupCell]): JsObject = JsObject(
    cell
      .groupBy(_ match {
        case _: SetupWallCell      => CellTypes.Wall.cellType
        case _: SetupEnemyCell     => CellTypes.Enemy.cellType
        case _: SetupMoverCell     => CellTypes.Mover.cellType
        case _: SetupRotatorCell   => CellTypes.Rotator.cellType
        case _: SetupBlockCell     => CellTypes.Block.cellType
        case _: SetupGeneratorCell => CellTypes.Generator.cellType
      })
      .map(t => t._1 -> JsArray(t._2.map(parseCell).toSeq))
      .toSeq
  )
}

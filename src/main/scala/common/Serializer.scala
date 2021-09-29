package it.unibo.pps.caw.common

import it.unibo.pps.caw.common.model.{Level, Position}
import it.unibo.pps.caw.common.model.cell.*

import play.api.libs.json.*

import scala.util.{Success, Try}

object Serializer {
  def serializeLevel(level: Level[BaseCell]): Try[String] =
    Success(
      Json.prettyPrint(
        JsObject(
          Seq(
            "width" -> JsNumber(level.dimensions.width),
            "height" -> JsNumber(level.dimensions.height),
            "playableArea" -> JsObject(
              Seq(
                "width" -> JsNumber(level.playableArea.dimensions.width),
                "height" -> JsNumber(level.playableArea.dimensions.height),
                "x" -> JsNumber(level.playableArea.position.x),
                "y" -> JsNumber(level.playableArea.position.y)
              )
            ),
            "cells" -> parseCells(level.board.cells)
          )
        )
      )
    )

  def parseCell(cell: BaseCell): JsObject = {
    val position: Position = cell.position
    val attributes: Seq[(String, JsValue)] =
      Seq("x" -> JsNumber(position.x), "y" -> JsNumber(position.y)) :++ (cell match {
        case BaseRotatorCell(_, r)   => Seq("rotation" -> JsString(r.name))
        case BaseGeneratorCell(_, o) => Seq("orientation" -> JsString(o.name))
        case BaseMoverCell(_, d)     => Seq("orientation" -> JsString(d.name))
        case BaseBlockCell(_, p)     => Seq("push" -> JsString(p.name))
        case _                       => Seq.empty
      })
    JsObject(attributes)
  }

  def parseCells(cells: Set[BaseCell]): JsObject = JsObject(
    cells
      .groupBy(_ match {
        case _: BaseWallCell      => CellType.Wall.name
        case _: BaseEnemyCell     => CellType.Enemy.name
        case _: BaseMoverCell     => CellType.Mover.name
        case _: BaseRotatorCell   => CellType.Rotator.name
        case _: BaseBlockCell     => CellType.Block.name
        case _: BaseGeneratorCell => CellType.Generator.name
      })
      .map(t => t._1 -> JsArray(t._2.map(parseCell).toSeq))
      .toSeq
  )
}

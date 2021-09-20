package it.unibo.pps.caw.editor.model

import com.fasterxml.jackson.annotation.JsonValue
import com.sun.javafx.collections.NonIterableChange.GenericAddRemoveChange
import play.api.libs.json.{JsArray, JsNumber, JsObject, JsString, JsValue}

import scala.util.Try

object Serializer {
  def serializeLevel(level: Level): Option[String] =
    level.playableArea.map(_ =>
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
      ).toString
    )

  def parseCell(cell: Cell): JsObject = {
    val position: Position = cell.position
    val attributes: Seq[(String, JsValue)] =
      Seq("x" -> JsNumber(position.x), "y" -> JsNumber(position.y)) :++ (cell match {
        case RotatorCell(_, r)   => Seq("rotation" -> JsString(r.rotation))
        case GeneratorCell(_, o) => Seq("orientation" -> JsString(o.orientation))
        case MoverCell(_, d)     => Seq("orientation" -> JsString(d.orientation))
        case BlockCell(_, p)     => Seq("push" -> JsString(p.push))
        case _                   => Seq.empty
      })
    JsObject(attributes)
  }

  def parseCells(cell: Set[Cell]): JsObject = JsObject(
    cell
      .groupBy(_ match {
        case _: WallCell      => CellTypes.Wall.cellType
        case _: EnemyCell     => CellTypes.Enemy.cellType
        case _: MoverCell     => CellTypes.Mover.cellType
        case _: RotatorCell   => CellTypes.Rotator.cellType
        case _: BlockCell     => CellTypes.Block.cellType
        case _: GeneratorCell => CellTypes.Generator.cellType
      })
      .map(t => t._1 -> JsArray(t._2.map(parseCell).toSeq))
      .toSeq
  )
}

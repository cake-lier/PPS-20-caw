package it.unibo.pps.caw.editor.model

import com.fasterxml.jackson.annotation.JsonValue
import com.sun.javafx.collections.NonIterableChange.GenericAddRemoveChange
import play.api.libs.json.{JsArray, JsNumber, JsObject, JsString, JsValue}

object Serializer {
  def serializeLevel(level: Level): String =
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
        "cells" -> parseCells(level.cells)
      )
    ).toString

  def parseCell(cell: Cell): JsObject = {
    val position: Position = cell.position
    val attributes: Seq[(String, JsValue)] =
      Seq("x" -> JsNumber(position.x), "y" -> JsNumber(position.y)) :++ (cell match {
        case RotatorCell(_, _, d)   => Seq("direction" -> JsString(d.getDirection))
        case GeneratorCell(_, _, o) => Seq("orientation" -> JsString(o.getOrientation))
        case MoverCell(_, _, d)     => Seq("direction" -> JsString(d.getOrientation))
        case _                      => Seq.empty
      })
    JsObject(attributes)
  }

  def parseCells(cell: Set[Cell]): JsObject = JsObject(
    cell
      .groupBy(_ match {
        case _: WallCel       => CellTypes.Wall.getType
        case _: EnemyCell     => CellTypes.Enemy.getType
        case _: MoverCell     => CellTypes.Mover.getType
        case _: RotatorCell   => CellTypes.Rotator.getType
        case _: BlockCell     => CellTypes.Block.getType
        case _: GeneratorCell => CellTypes.Generator.getType
      })
      .map(t => t._1 -> JsArray(t._2.map(parseCell).toSeq))
      .toSeq
  )
}

package it.unibo.pps.caw.dsl

import it.unibo.pps.caw.dsl.entities.{Board, Cell, Dimensions, OrientableCell, Position, PushableCell, RotatableCell}
import play.api.libs.json.{JsArray, JsNumber, JsObject, JsString, JsValue, Json}

object BoardSerializer {
  private object ElementsSerializers {
    def serializeDimensions(dimensions: Dimensions): JsObject =
      JsObject(
        Seq(
          "width" -> JsNumber(dimensions.width),
          "height" -> JsNumber(dimensions.height)
        )
      )

    def serializePosition(position: Position): JsObject =
      JsObject(
        Seq(
          "x" -> JsNumber(position.x),
          "y" -> JsNumber(position.y)
        )
      )

    def serializeOrientableCell(cell: OrientableCell): JsObject =
      JsObject(Seq("orientation" -> JsString(cell.orientation.toString.toLowerCase))) ++ serializePosition(cell.position)

    def serializeRotatableCell(cell: RotatableCell): JsObject =
      JsObject(Seq("rotation" -> JsString(cell.rotation.toString.toLowerCase))) ++ serializePosition(cell.position)

    def serializePushableCell(cell: PushableCell): JsObject =
      JsObject(Seq("push" -> JsString(cell.push.toString.toLowerCase))) ++ serializePosition(cell.position)

    def serializeCell(cell: Cell): JsObject = serializePosition(cell.position)
  }

  import ElementsSerializers.*

  def serialize(board: Board): String =
    Json.prettyPrint(
      serializeDimensions(board.dimensions) ++
        JsObject(
          Seq(
            "playableArea" -> (
              serializeDimensions(board.playableArea.dimensions) ++ serializePosition(board.playableArea.position)
            ),
            "cells" -> JsObject(
              Seq(
                "mover" -> JsArray(board.moverCells.map(serializeOrientableCell(_)).toIndexedSeq),
                "generator" -> JsArray(board.generatorCells.map(serializeOrientableCell(_)).toIndexedSeq),
                "rotator" -> JsArray(board.rotatorCells.map(serializeRotatableCell(_)).toIndexedSeq),
                "block" -> JsArray(board.blockCells.map(serializePushableCell(_)).toIndexedSeq),
                "enemy" -> JsArray(board.enemyCells.map(serializeCell(_)).toIndexedSeq),
                "wall" -> JsArray(board.wallCells.map(serializeCell(_)).toIndexedSeq)
              )
            )
          )
        )
    )
}

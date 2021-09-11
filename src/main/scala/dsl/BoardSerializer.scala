package it.unibo.pps.caw.dsl

import it.unibo.pps.caw.dsl.entities.{Board, Cell, Dimensions, OrientableCell, Position, PushableCell, RotatableCell}
import play.api.libs.json.{JsArray, JsNumber, JsObject, JsString, JsValue, Json}

/** Contains the method for serializing a built [[Board]].
  *
  * The serialization serializes the instance in a JSON format which is compatible with the defined schema. The only method
  * exposed by this module is the [[BoardSerializer.serialize]] one, which can perform the whole serialization operation.
  */
object BoardSerializer {

  /* Module containing all helper methods for the serialization operation. */
  private object ElementsSerializers {
    /* Serializes a Dimensions instance. */
    def serializeDimensions(dimensions: Dimensions): JsObject =
      JsObject(
        Seq(
          "width" -> JsNumber(dimensions.width),
          "height" -> JsNumber(dimensions.height)
        )
      )

    /* Serializes a Position instance. */
    def serializePosition(position: Position): JsObject =
      JsObject(
        Seq(
          "x" -> JsNumber(position.x),
          "y" -> JsNumber(position.y)
        )
      )

    /* Serializes an OrientableCell instance. */
    def serializeOrientableCell(cell: OrientableCell): JsObject =
      JsObject(Seq("orientation" -> JsString(cell.orientation.toString.toLowerCase))) ++ serializePosition(cell.position)

    /* Serializes a RotatableCell instance. */
    def serializeRotatableCell(cell: RotatableCell): JsObject =
      JsObject(Seq("rotation" -> JsString(cell.rotation.toString.toLowerCase))) ++ serializePosition(cell.position)

    /* Serializes a PushableCell instance. */
    def serializePushableCell(cell: PushableCell): JsObject =
      JsObject(Seq("push" -> JsString(cell.push.toString.toLowerCase))) ++ serializePosition(cell.position)

    /* Serializes a Cell instance. */
    def serializeCell(cell: Cell): JsObject = serializePosition(cell.position)
  }

  import ElementsSerializers.*

  /** Serializes a [[Board]] instance to a [[String]] in a JSON format compatible with the defined schema.
    *
    * @param board
    *   the [[Board]] instance to serialize
    * @return
    *   a [[String]] in a JSON format with the serialized [[Board]]
    */
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

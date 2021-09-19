package it.unibo.pps.caw
package model

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.json.schema.{SchemaParser, SchemaRouter, SchemaRouterOptions}
import play.api.libs.json.{JsArray, JsObject, JsValue, Json}
import model.Board
import scala.io.Source

/** Object for deserialization of [[Level]] components written in JSON format. */
object Deserializer {

  /** deserialization method which return the [[Level]] filled by data in the JSON The JSON file must follow a schema for the
    * validation, if not a [[IllegalArgumentException]] is thrown
    *
    * @param jsonStringLevel
    *   : the level infos written in json format
    * @return
    *   [[Level]] instance filled by the data read from inside the file
    */

  def deserializeLevel(jsonStringLevel: String): Either[IllegalArgumentException, Level] = {
    if (jsonStringLevel.isEmpty || !isValidJson(jsonStringLevel)) {
      return Left(IllegalArgumentException())
    }
    val jsonLevel = Json.parse(jsonStringLevel)
    val jsonPlayableArea = (jsonLevel \ "playableArea").as[JsObject]
    val playableAreaWidth = (jsonPlayableArea \ "width").as[Int]
    val playableAreaHeight = (jsonPlayableArea \ "height").as[Int]
    val playableAreaPosition = extractPosition(jsonPlayableArea)
    Right(
      Level(
        (jsonLevel \ "width").as[Int],
        (jsonLevel \ "height").as[Int],
        Board(
          deserializeCells((jsonLevel \ "cells").as[JsObject], playableAreaPosition, playableAreaWidth, playableAreaHeight)
        ),
        PlayableArea(playableAreaPosition, playableAreaWidth, playableAreaHeight)
      )
    )
  }

  /* deserialize all cells in their specific types, grouping them into a Set */
  private def deserializeCells(
      jsonLevels: JsObject,
      playableAreaPoint: Position,
      playableAreaWidth: Int,
      playableAreaHeight: Int
  ): Set[SetupCell] = {
    jsonLevels.value
      .flatMap((cellType, jsCell) =>
        jsCell
          .as[JsArray]
          .value
          .map(jsCell =>
            val position = extractPosition(jsCell)
            val isInside = insideArea(position, playableAreaPoint, playableAreaWidth, playableAreaHeight)
            EnumHelper.toCellTypes(cellType).get match {
              case CellTypes.Mover =>
                SetupMoverCell(position, EnumHelper.toOrientation((jsCell \ "orientation").as[String]).get, isInside)
              case CellTypes.Block =>
                SetupBlockCell(position, EnumHelper.toMovement((jsCell \ "allowedMovement").as[String]).get, isInside)
              case CellTypes.Enemy => SetupEnemyCell(position, isInside)
              case CellTypes.Rotator =>
                SetupRotatorCell(position, EnumHelper.toRotation((jsCell \ "direction").as[String]).get, isInside)
              case CellTypes.Wall => SetupWallCell(position, isInside)
              case CellTypes.Generator =>
                SetupGeneratorCell(position, EnumHelper.toOrientation((jsCell \ "orientation").as[String]).get, isInside)
            }
          )
          .toSet
      )
      .toSet
  }

  /* Validate the provided JSON in string format with the schema. If success true is returned, otherwise false*/
  private def isValidJson(jsonString: String): Boolean = {
    val schemaRouter: SchemaRouter = SchemaRouter.create(Vertx.vertx(), new SchemaRouterOptions());
    val schemaParser: SchemaParser = SchemaParser.createDraft201909SchemaParser(schemaRouter);
    try {
      schemaParser
        .parseFromString(Source.fromResource("board_schema.json").getLines.mkString)
        .validateSync(JsonObject(jsonString))
      true
    } catch {
      case e: Exception => print(e); false
    }
  }

  /* Extract and return the position of specific item from JSON  */
  private def extractPosition(jsCell: JsValue): Position = {
    Position((jsCell \ "x").as[Int], (jsCell \ "y").as[Int])
  }

  /* Check if a point in within a specific area*/
  private def insideArea(point: Position, startingAreaPoint: Position, width: Int, height: Int): Boolean = {
    point.x >= startingAreaPoint.x && point.x <= (startingAreaPoint.x + width) &&
    point.y >= startingAreaPoint.y && point.y <= (startingAreaPoint.y + height)
  }
}
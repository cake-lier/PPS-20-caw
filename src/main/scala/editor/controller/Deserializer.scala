package it.unibo.pps.caw.editor.controller

import it.unibo.pps.caw.editor.model.*
import it.unibo.pps.caw.common.{Dimensions, PlayableArea, Position}
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.json.schema.{SchemaParser, SchemaRouter, SchemaRouterOptions}
import play.api.libs.json.{JsArray, JsObject, Json, JsValue}

import scala.io.Source
import scala.util.{Try, Using}

/** Object for deserialization of [[Level]] components written in JSON format. */
object Deserializer {

  /** deserialization method which return the [[Level]] filled by data in the JSON The JSON file must follow a schema for the
    * validation, if not a [[IllegalArgumentException]] is thrown
    *
    * @param jsonStringLevel
    *   the level infos written in json format
    * @return
    *   [[Level]] instance filled by the data read from inside the file
    */
  def deserializeLevel(jsonStringLevel: String): Try[Level] = {
    for {
      _ <- Try(if (jsonStringLevel.isEmpty) throw IllegalArgumentException())
      _ <- isValidJson(jsonStringLevel)
    } yield {
      val jsonLevel = Json.parse(jsonStringLevel)
      val jsonPlayableArea = (jsonLevel \ "playableArea").as[JsObject]
      val playableAreaDimensions = extractDimensions(jsonPlayableArea)
      val playableAreaPosition = extractPosition(jsonPlayableArea)
      Level(
        (jsonLevel \ "width").as[Int],
        (jsonLevel \ "height").as[Int],
        deserializeCells((jsonLevel \ "cells").as[JsObject], playableAreaPosition, playableAreaDimensions),
        PlayableArea(playableAreaPosition, playableAreaDimensions)
      )
    }
  }

  /* deserialize all cells in their specific types, grouping them into a Set */
  private def deserializeCells(
    jsonLevels: JsObject,
    playableAreaPoint: Position,
    playableAreaDimensions: Dimensions
  ): Board[SetupCell] = {
    Board(
      jsonLevels
        .value
        .flatMap((t, a) =>
          a
            .as[JsArray]
            .value
            .map(c =>
              val position = extractPosition(c)
              val isInside = insideArea(position, playableAreaPoint, playableAreaDimensions)
              EnumHelper.toCellTypes(t).get match {
                case CellTypes.Mover =>
                  SetupMoverCell(position, EnumHelper.toOrientation((c \ "orientation").as[String]).get, true)
                case CellTypes.Block =>
                  SetupBlockCell(position, EnumHelper.toPush((c \ "push").as[String]).get, true)
                case CellTypes.Enemy => SetupEnemyCell(position, true)
                case CellTypes.Rotator =>
                  SetupRotatorCell(position, EnumHelper.toRotation((c \ "rotation").as[String]).get, true)
                case CellTypes.Wall => SetupWallCell(position, true)
                case CellTypes.Generator =>
                  SetupGeneratorCell(position, EnumHelper.toOrientation((c \ "orientation").as[String]).get, true)
              }
            )
            .toSet
        )
        .toSet
    )
  }

  /* Validate the provided JSON in string format with the schema. If success true is returned, otherwise false*/
  private def isValidJson(jsonString: String): Try[Unit] = {
    val vertx: Vertx = Vertx.vertx()
    val validationTry: Try[Unit] = for {
      s <- Using(Source.fromResource("board_schema.json"))(_.getLines.mkString)
      _ <- Try {
        SchemaParser
          .createDraft201909SchemaParser(SchemaRouter.create(vertx, SchemaRouterOptions()))
          .parseFromString(s)
          .validateSync(JsonObject(jsonString))
      }.recover(_ => throw IllegalArgumentException())
    } yield ()
    vertx.close()
    validationTry
  }

  /* Extract and return the position of specific item from JSON  */
  private def extractPosition(jsCell: JsValue): Position = {
    Position((jsCell \ "x").as[Int], (jsCell \ "y").as[Int])
  }

  /* Extract the Dimensions of specific JSON item. */
  private def extractDimensions(dimensioned: JsValue): Dimensions =
    ((dimensioned \ "width").as[Int], (dimensioned \ "height").as[Int])

  /* Check if a point in within a specific area*/
  private def insideArea(point: Position, startingAreaPoint: Position, dimensions: Dimensions): Boolean = {
    point.x >= startingAreaPoint.x && point.x <= (startingAreaPoint.x + dimensions.width) &&
    point.y >= startingAreaPoint.y && point.y <= (startingAreaPoint.y + dimensions.height)
  }
}

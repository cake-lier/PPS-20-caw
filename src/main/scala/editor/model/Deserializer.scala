package it.unibo.pps.caw.editor.model

import it.unibo.pps.caw.editor.model._
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.json.schema.{SchemaParser, SchemaRouter, SchemaRouterOptions}
import play.api.libs.json.{JsArray, JsObject, JsValue, Json}

import scala.io.Source
import scala.util.{Failure, Success, Try, Using}

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
      val playableAreaWidth = (jsonPlayableArea \ "width").as[Int]
      val playableAreaHeight = (jsonPlayableArea \ "height").as[Int]
      val playableAreaPosition = extractPosition(jsonPlayableArea)
      Level(
        (jsonLevel \ "width").as[Int],
        (jsonLevel \ "height").as[Int],
        deserializeCells((jsonLevel \ "cells").as[JsObject], playableAreaPosition, playableAreaWidth, playableAreaHeight),
        PlayableArea(playableAreaPosition, playableAreaWidth, playableAreaHeight)
      )
    }
  }

  /* deserialize all cells in their specific types, grouping them into a Set */
  private def deserializeCells(
      jsonLevels: JsObject,
      playableAreaPoint: Position,
      playableAreaWidth: Int,
      playableAreaHeight: Int
  ): Board[Cell] = {
    Board[Cell](
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
                  MoverCell(position, EnumHelper.toOrientation((jsCell \ "orientation").as[String]).get)
                case CellTypes.Block =>
                  BlockCell(position, EnumHelper.toPush((jsCell \ "push").as[String]).get)
                case CellTypes.Enemy => EnemyCell(position)
                case CellTypes.Rotator =>
                  RotatorCell(position, EnumHelper.toRotation((jsCell \ "rotation").as[String]).get)
                case CellTypes.Wall => WallCell(position)
                case CellTypes.Generator =>
                  GeneratorCell(position, EnumHelper.toOrientation((jsCell \ "orientation").as[String]).get)
                case _ => throw IllegalStateException()
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

  /* Check if a point in within a specific area*/
  private def insideArea(point: Position, startingAreaPoint: Position, width: Int, height: Int): Boolean = {
    point.x >= startingAreaPoint.x && point.x <= (startingAreaPoint.x + width) &&
    point.y >= startingAreaPoint.y && point.y <= (startingAreaPoint.y + height)
  }
}

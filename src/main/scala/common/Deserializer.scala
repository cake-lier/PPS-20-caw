package it.unibo.pps.caw.common

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.json.schema.{SchemaParser, SchemaRouter, SchemaRouterOptions}
import it.unibo.pps.caw.common.model.{Board, Dimensions, Level, PlayableArea, Position}
import it.unibo.pps.caw.common.model.cell.{
  BaseBlockCell,
  BaseCell,
  BaseEnemyCell,
  BaseGeneratorCell,
  BaseMoverCell,
  BaseRotatorCell,
  BaseWallCell,
  CellType,
  Orientation,
  Push,
  Rotation
}
import play.api.libs.json.{JsArray, JsObject, Json, JsValue}

import scala.util.Try

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
  def deserializeLevel(jsonStringLevel: String): Try[Level[BaseCell]] = {
    for {
      _ <- Try(if (jsonStringLevel.isEmpty) throw IllegalArgumentException())
      _ <- isValidJson(jsonStringLevel)
    } yield {
      val jsonLevel = Json.parse(jsonStringLevel)
      val jsonPlayableArea = (jsonLevel \ "playableArea").as[JsObject]
      val playableAreaDimensions = extractDimensions(jsonPlayableArea)
      val playableAreaPosition = extractPosition(jsonPlayableArea)
      Level(
        extractDimensions(jsonLevel),
        deserializeCells((jsonLevel \ "cells").as[JsObject], playableAreaPosition, playableAreaDimensions),
        PlayableArea(playableAreaPosition, playableAreaDimensions)
      )
    }
  }

  /* deserialize all cells in their specific types, grouping them into a Set */
  private def deserializeCells(
    jsonLevel: JsObject,
    playableAreaPosition: Position,
    playableAreaDimensions: Dimensions
  ): Board[BaseCell] =
    Board(
      jsonLevel
        .value
        .flatMap((t, a) =>
          a
            .as[JsArray]
            .value
            .map(c =>
              val position = extractPosition(c)
              CellType.fromName(t) match {
                case Some(CellType.Mover) =>
                  Orientation.fromName((c \ "orientation").as[String]) match {
                    case Some(o) => Some(BaseMoverCell(position, o))
                    case _       => None
                  }
                case Some(CellType.Block) =>
                  Push.fromName((c \ "push").as[String]) match {
                    case Some(p) => Some(BaseBlockCell(position, p))
                    case _       => None
                  }
                case Some(CellType.Enemy) => Some(BaseEnemyCell(position))
                case Some(CellType.Rotator) =>
                  Rotation.fromName((c \ "rotation").as[String]) match {
                    case Some(r) => Some(BaseRotatorCell(position, r))
                    case _       => None
                  }
                case Some(CellType.Wall) => Some(BaseWallCell(position))
                case Some(CellType.Generator) =>
                  Orientation.fromName((c \ "orientation").as[String]) match {
                    case Some(o) => Some(BaseGeneratorCell(position, o))
                    case _       => None
                  }
                case _ => None
              }
            )
            .filter(_.isDefined)
            .map(_.get)
            .toSet
        )
        .toSet
    )

  /* Validate the provided JSON in string format with the schema. */
  private def isValidJson(json: String): Try[Unit] = {
    val vertx: Vertx = Vertx.vertx()
    val validationTry: Try[Unit] = for {
      s <- Loader.loadResource("board_schema.json")
      _ <- Try {
        SchemaParser
          .createDraft201909SchemaParser(SchemaRouter.create(vertx, SchemaRouterOptions()))
          .parseFromString(s)
          .validateSync(JsonObject(json))
      }.recover(_ => throw IllegalArgumentException())
    } yield ()
    vertx.close()
    validationTry
  }

  /* Extract the Dimensions of specific JSON item. */
  private def extractDimensions(dimensioned: JsValue): Dimensions =
    ((dimensioned \ "width").as[Int], (dimensioned \ "height").as[Int])

  /* Extract the Position of specific JSON item. */
  private def extractPosition(positioned: JsValue): Position = ((positioned \ "x").as[Int], (positioned \ "y").as[Int])
}

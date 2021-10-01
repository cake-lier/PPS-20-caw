package it.unibo.pps.caw.common

import model.{Board, Dimensions, Level, PlayableArea, Position}
import model.cell.{BaseBlockCell, BaseCell, BaseEnemyCell, BaseGeneratorCell, BaseMoverCell, BaseRotatorCell, BaseWallCell, CellType, Orientation, Push, Rotation}

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.json.schema.{SchemaParser, SchemaRouter, SchemaRouterOptions}
import play.api.libs.json.{JsArray, JsNumber, JsObject, JsString, JsValue, Json}

import scala.util.{Success, Try}

/** Parser for serialization and deserialization of [[Level]] components written in JSON format. */
trait LevelParser {

  /** Serializes a [[Level]] instance, returns the level data as a string.
    *
    * @param level
    *   the [[Level]] instance to serialize
    * @return
    *   the string of the level data, in JSON format
    */
  def serializeLevel(level: Level[BaseCell]): Try[String]

  /** Deserializes a JSON string of a level, returns the corresponding [[Level]].
    * The JSON string must follow a schema for the validation, if not a [[IllegalArgumentException]] is thrown.
    *
    * @param jsonStringLevel
    *   the level data written in JSON format
    * @return
    *   [[Level]] instance filled with the data from the JSON string
    */
  def deserializeLevel(jsonStringLevel: String): Try[Level[BaseCell]]

}

object LevelParser {

  private class LevelParserImpl(fileStorage: FileStorage) extends LevelParser {

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
        s <- fileStorage.loadResource("board_schema.json")
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

    private def parseCell(cell: BaseCell): JsObject = {
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

    private def parseCells(cells: Set[BaseCell]): JsObject = JsObject(
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

  def apply(fileStorage: FileStorage): LevelParser = LevelParserImpl(fileStorage)
}

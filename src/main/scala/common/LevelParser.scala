package it.unibo.pps.caw.common

import it.unibo.pps.caw.common.model.{Board, Dimensions, Level, PlayableArea, Position}
import it.unibo.pps.caw.common.model.cell.*
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.json.schema.{SchemaParser, SchemaRouter, SchemaRouterOptions}
import it.unibo.pps.caw.common.storage.FileStorage
import play.api.libs.json.{JsArray, JsNumber, JsObject, JsString, JsValue, Json}

import scala.util.Try

/** Parser for serialization and deserialization of [[Level]] components written in JSON format. */
trait LevelParser {

  /** Serializes a [[Level]] instance, returns the level data as a string.
    *
    * @param level
    *   the [[Level]] instance to serialize
    * @return
    *   the string of the level data, in JSON format
    */
  def serializeLevel(level: Level[BaseCell]): String

  /** Deserializes a JSON string of a level, returns the corresponding [[Level]]. The JSON string must follow a schema for the
    * validation, if not a [[IllegalArgumentException]] is thrown.
    *
    * @param jsonStringLevel
    *   the level data written in JSON format
    * @return
    *   [[Level]] instance filled with the data from the JSON string
    */
  def deserializeLevel(jsonStringLevel: String): Try[Level[BaseCell]]

}

/** Companion object to the [[LevelParser]] trait */
object LevelParser {

  private class LevelParserImpl(fileStorage: FileStorage) extends LevelParser {

    override def serializeLevel(level: Level[BaseCell]): String =
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

    override def deserializeLevel(jsonStringLevel: String): Try[Level[BaseCell]] = {
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
          PlayableArea(playableAreaDimensions)(playableAreaPosition)
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
                      case Some(o) => Some(BaseMoverCell(o)(position))
                      case _       => None
                    }
                  case Some(CellType.Block) =>
                    Push.fromName((c \ "push").as[String]) match {
                      case Some(p) => Some(BaseBlockCell(p)(position))
                      case _       => None
                    }
                  case Some(CellType.Enemy) => Some(BaseEnemyCell(position))
                  case Some(CellType.Rotator) =>
                    Rotation.fromName((c \ "rotation").as[String]) match {
                      case Some(r) => Some(BaseRotatorCell(r)(position))
                      case _       => None
                    }
                  case Some(CellType.Wall) => Some(BaseWallCell(position))
                  case Some(CellType.Generator) =>
                    Orientation.fromName((c \ "orientation").as[String]) match {
                      case Some(o) => Some(BaseGeneratorCell(o)(position))
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

    private def parseCell(cell: BaseCell): JsObject =
      JsObject(Seq("x" -> JsNumber(cell.position.x), "y" -> JsNumber(cell.position.y))) ++
        (cell match {
          case BaseRotatorCell(r, _)   => JsObject(Seq("rotation" -> JsString(r.name)))
          case BaseGeneratorCell(o, _) => JsObject(Seq("orientation" -> JsString(o.name)))
          case BaseMoverCell(d, _)     => JsObject(Seq("orientation" -> JsString(d.name)))
          case BaseBlockCell(p, _)     => JsObject(Seq("push" -> JsString(p.name)))
          case _                       => JsObject.empty
        })

    private def parseCells(cells: Set[BaseCell]): JsObject =
      JsObject(
        cells
          .groupBy(_ match {
            case _: BaseWallCell      => CellType.Wall.name
            case _: BaseEnemyCell     => CellType.Enemy.name
            case _: BaseMoverCell     => CellType.Mover.name
            case _: BaseRotatorCell   => CellType.Rotator.name
            case _: BaseBlockCell     => CellType.Block.name
            case _: BaseGeneratorCell => CellType.Generator.name
            case _: BaseDeleterCell   => CellType.Deleter.name
          })
          .map(t => t._1 -> JsArray(t._2.map(parseCell).toSeq))
          .toSeq
      )

  }

  /** Returns a new instance of the [[LevelParser]] trait. */
  def apply(fileStorage: FileStorage): LevelParser = LevelParserImpl(fileStorage)
}

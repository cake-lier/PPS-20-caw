package it.unibo.pps.caw
package common

import common.model.*
import common.model.cell.*
import common.storage.FileStorage

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.json.schema.*
import play.api.libs.json.*

import scala.util.{Failure, Success, Try}

/** Parser for serialization and deserialization of [[it.unibo.pps.caw.common.model.Level]].
  *
  * The strings received to be deserialized and the strings produced after serialization must follow be in JSON format, which is
  * the only supported format for serialization. It must be constructed through its companion object.
  */
trait LevelParser {

  /** Serializes a [[it.unibo.pps.caw.common.model.Level]] instance, returns the level data as a string.
    *
    * @param level
    *   the [[it.unibo.pps.caw.common.model.Level]] instance to serialize
    * @return
    *   the string of the level data, in JSON format
    */
  def serializeLevel(level: Level[BaseCell]): String

  /** Deserializes a JSON string of a level, returns the corresponding [[it.unibo.pps.caw.common.model.Level]]. The JSON string
    * must follow a schema for the validation, if not a [[IllegalArgumentException]] is thrown.
    *
    * @param json
    *   the level data written in JSON format
    * @return
    *   a [[it.unibo.pps.caw.common.model.Level]] instance filled with the data from the JSON string
    */
  def deserializeLevel(json: String): Try[Level[BaseCell]]
}

/** Companion object to the [[LevelParser]] trait, containing its factory method. */
object LevelParser {

  /* Default implementation of the FileStorage trait. */
  private case class LevelParserImpl(fileStorage: FileStorage) extends LevelParser {

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
            "cells" -> serializeCells(level.board.cells)
          )
        )
      )

    override def deserializeLevel(json: String): Try[Level[BaseCell]] = {
      for {
        _ <- Try(if (json.isEmpty) throw IllegalArgumentException())
        _ <- isValidJson(json)
      } yield {
        val jsonLevel = Json.parse(json)
        val jsonPlayableArea = (jsonLevel \ "playableArea").as[JsObject]
        val playableAreaDimensions = extractDimensions(jsonPlayableArea)
        val playableAreaPosition = extractPosition(jsonPlayableArea)
        Level(
          extractDimensions(jsonLevel),
          deserializeCells((jsonLevel \ "cells").as[JsObject]),
          PlayableArea(playableAreaDimensions)(playableAreaPosition)
        )
      }
    }

    /* Deserializes all cells in their specific types, grouping them into a Set. */
    private def deserializeCells(jsonLevel: JsObject): Board[BaseCell] =
      Board(
        jsonLevel
          .value
          .flatMap((t, a) =>
            a
              .as[JsArray]
              .value
              .map(c => {
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
                  case Some(CellType.Deleter) => Some(BaseDeleterCell(position))
                  case _                      => None
                }
              })
              .filter(_.isDefined)
              .map(_.get)
              .toSet
          )
          .toSet
      )

    /* Validate the provided JSON in string format with the schema. */
    private def isValidJson(json: String): Try[Unit] =
      fileStorage
        .loadResource("board_schema.json")
        .flatMap(s =>
          if (
            Validator
              .create(
                JsonSchema.of(JsonObject(s)),
                JsonSchemaOptions().setDraft(Draft.DRAFT201909).setBaseUri("https://github.com/cake-lier/PPS-20-caw")
              )
              .validate(JsonObject(json))
              .getValid
          )
            Success(())
          else
            Failure(new IllegalArgumentException())
        )

    /* Extract the Dimensions of a specific JSON item. */
    private def extractDimensions(dimensioned: JsValue): Dimensions =
      ((dimensioned \ "width").as[Int], (dimensioned \ "height").as[Int])

    /* Extract the Position of a specific JSON item. */
    private def extractPosition(positioned: JsValue): Position = ((positioned \ "x").as[Int], (positioned \ "y").as[Int])

    /* Serializes a given BaseCell into a JSON item. */
    private def serializeCell(cell: BaseCell): JsObject =
      JsObject(Seq("x" -> JsNumber(cell.position.x), "y" -> JsNumber(cell.position.y))) ++
        (cell match {
          case BaseRotatorCell(r, _)   => JsObject(Seq("rotation" -> JsString(r.name)))
          case BaseGeneratorCell(o, _) => JsObject(Seq("orientation" -> JsString(o.name)))
          case BaseMoverCell(d, _)     => JsObject(Seq("orientation" -> JsString(d.name)))
          case BaseBlockCell(p, _)     => JsObject(Seq("push" -> JsString(p.name)))
          case _                       => JsObject.empty
        })

    /* Serializes a set of BaseCell into a JSON item. */
    private def serializeCells(cells: Set[BaseCell]): JsObject =
      JsObject(
        cells
          .groupBy {
            case _: BaseWallCell      => CellType.Wall.name
            case _: BaseEnemyCell     => CellType.Enemy.name
            case _: BaseMoverCell     => CellType.Mover.name
            case _: BaseRotatorCell   => CellType.Rotator.name
            case _: BaseBlockCell     => CellType.Block.name
            case _: BaseGeneratorCell => CellType.Generator.name
            case _: BaseDeleterCell   => CellType.Deleter.name
          }
          .map(t => t._1 -> JsArray(t._2.map(serializeCell).toSeq))
          .toSeq
      )
  }

  /** Returns a new instance of the [[LevelParser]] trait given the storage from which retrieving the file containing the JSON
    * schema of a [[it.unibo.pps.caw.common.model.Level]].
    *
    * @param fileStorage
    *   the storage from which retrieving the file containing the JSON schema to be used
    * @return
    *   a new [[LevelParser]] instance
    */
  def apply(fileStorage: FileStorage): LevelParser = LevelParserImpl(fileStorage)
}

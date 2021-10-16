package it.unibo.pps.caw.game.model.engine

import it.unibo.pps.caw.common.model.{Board, Position}
import it.unibo.pps.caw.common.model.cell.{Orientation, Push, Rotation}
import it.unibo.pps.caw.game.model._

import alice.tuprolog.Term

import scala.util.matching.Regex

/* An utility object for prolog serialization and deserialization. */
private object PrologParser {

  /* Returns a Prolog cell(id, cellType, x, y) given its Scala cell. */

  def serializeCell(cell: UpdateCell): String = {
    val cellType: String = cell match {
      case _: UpdateWallCell           => "wall"
      case _: UpdateEnemyCell          => "enemy"
      case UpdateMoverCell(_, o, _, _) => "mover_" + o.name
      case UpdateBlockCell(_, p, _, _) =>
        "block" + (p match {
          case Push.Horizontal => "_hor"
          case Push.Vertical   => "_ver"
          case Push.Both       => ""
        })
      case UpdateGeneratorCell(_, o, _, _) => "generator_" + o.name
      case UpdateRotatorCell(_, r, _, _)   => "rotator_" + r.name
      case _: UpdateDeleterCell            => "deleter"
    }
    "cell" + Seq(cell.id, cellType, cell.position.x, cell.position.y).mkString("(", ",", ")")
  }

  /* Returns the Prolog clause given its cell.

     If the cell is a mover or a rotator, it returns mover/rotator_next_state[board, x, y, NB].
     If the cell is a generator, it returns generator_next_state[board, maxId, x, y, NB] */
  def createSerializedPredicate(board: Board[UpdateCell], maxId: Long, cell: UpdateCell): Option[String] = {
    var seq = Seq(board.map(serializeCell).mkString("[", ",", "]"), cell.position.x, cell.position.y)
    if (cell.isInstanceOf[UpdateGeneratorCell]) {
      seq :+= maxId.toString
    }
    seq :+= "NB"
    (cell match {
      case UpdateMoverCell(_, o, _, _)     => Some("mover_" + o.name)
      case UpdateGeneratorCell(_, o, _, _) => Some("generator_" + o.name)
      case UpdateRotatorCell(_, r, _, _)   => Some("rotator_" + r.name)
      case _                               => None
    }).map(_ + "_next_state" + seq.mkString("(", ",", ")"))
  }

  /* Returns the Scala Board of UpdateCell given a Prolog Board. */
  def deserializeBoard(stringBoard: String): Board[UpdateCell] = {
    val regex: Regex =
      ("cell\\(\\d+,(?:mover_right|mover_left|mover_top|mover_down|generator_right|generator_left|generator_top|generator_down" +
        "|rotator_clockwise|rotator_counterclockwise|block|block_hor|block_ver|enemy|wall|deleter),\\d+,\\d+\\)").r
    Board(
      regex
        .findAllMatchIn(stringBoard)
        .map(_.toString)
        .map(PrologParser.deserializeCell)
        .toSet
    )
  }

  /* Returns a Scala UpdateCell given its Prolog cell. */
  def deserializeCell(stringCell: String): UpdateCell = {
    val s"cell($id,$cellType,$stringX,$stringY)" = stringCell
    val cellId: Int = id.toInt
    val position: Position = Position(stringX.toInt, stringY.toInt)
    val updated: Boolean = false // default value, properly set in nextState()

    cellType match {
      case s"mover_$orientation" => UpdateMoverCell(position)(Orientation.fromName(orientation).get)(cellId)(updated)
      case "enemy"               => UpdateEnemyCell(position)(cellId)(updated)
      case "wall"                => UpdateWallCell(position)(cellId)(updated)
      case s"block$movement" =>
        UpdateBlockCell(position)(movement match {
          case "_hor" => Push.Horizontal
          case "_ver" => Push.Vertical
          case _      => Push.Both
        })(cellId)(updated)
      case s"generator_$orientation" =>
        UpdateGeneratorCell(position)(Orientation.fromName(orientation).get)(cellId)(updated)
      case s"rotator_$rotation" => UpdateRotatorCell(position)(Rotation.fromName(rotation).get)(cellId)(updated)
      case "deleter"            => UpdateDeleterCell(position)(cellId)(updated)
    }
  }
}

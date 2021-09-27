package it.unibo.pps.caw.game.model.engine
import alice.tuprolog.{Prolog, Struct, Term, Theory}
import com.google.gson.{Gson, JsonArray}
import _root_.engine.{Clause, PrologEngine}
import _root_.engine.PrologEngine.extractTerm
import it.unibo.pps.caw.common.{Board, Position}
import it.unibo.pps.caw.game.model.*
import it.unibo.pps.caw.game.model.CellTypes.*
import it.unibo.pps.caw.game.model.Push.*
import it.unibo.pps.caw.game.model.Orientation.*
import it.unibo.pps.caw.game.model.Rotation.*

import scala.annotation.tailrec
import scala.io.Source
import scala.reflect.ClassTag
import scala.util.Using
import scala.util.matching.Regex

/** Engine of game rules */
sealed trait RulesEngine {

  /** Calculate the next [[Board]] starting from the current [[Board]] and the [[Cell]] to be updated */
  def nextState(board: Board[IdCell], cell: IdCell): Board[IdCell]
}

/** Companion object for trait [[RulesEngine]] */
object RulesEngine {

  private case class RulesEngineImpl() extends RulesEngine {
    private val engine: Term => Term =
      Using(Source.fromResource("cellmachine.pl")) { c => PrologEngine(Clause(c.getLines.mkString(" "))) }.get

    def nextState(board: Board[IdCell], cell: IdCell): Board[IdCell] = {
      val cellState: Map[Int, Boolean] = board
        .cells
        .map(c => if (c.id == cell.id) (c.id, true) else (c.id, c.updated))
        .toMap

      /* Update cell returned by deserializer with correct field IdCell.updated */
      def updateCell(cell: IdCell): IdCell = cell match {
        case c if (c.id > cellState.keySet.max) => CellConverter.toUpdated(c, true) // new cell created by a generator
        case c                                  => CellConverter.toUpdated(c, cellState(c.id))
      }
      val resBoard = PrologParser.deserializeBoard(
        extractTerm(
          engine(PrologParser.createSerializedPredicate(getPartialBoard(board, cell), cellState.keySet.max + 1, cell))
        ).toString
      )
      updateGloabalBoard(board, Board[IdCell](resBoard.cells.map(updateCell)), cell)
    }
  }

  def apply(): RulesEngine = RulesEngineImpl()

  private def updateGloabalBoard(globalboard: Board[IdCell], partialBoard: Board[IdCell], cell: IdCell): Board[IdCell] =
    cell match {
      case IdGeneratorCell(position, orientation, _, _) =>
        orientation match {
          case Right | Left => Board[IdCell](globalboard.cells.filter(_.position.y != position.y).toSet ++ partialBoard.cells)
          case Top | Down   => Board[IdCell](globalboard.cells.filter(_.position.x != position.x).toSet ++ partialBoard.cells)
        }
      case IdMoverCell(position, orientation, _, _) =>
        orientation match {
          case Right | Left => Board[IdCell](globalboard.cells.filter(_.position.y != position.y).toSet ++ partialBoard.cells)
          case Top | Down   => Board[IdCell](globalboard.cells.filter(_.position.x != position.x).toSet ++ partialBoard.cells)
        }
      case IdRotatorCell(position, _, _, _) =>
        Board[IdCell](
          globalboard
            .cells
            .filter(c =>
              c.position != Position(position.x, position.y) && c.position != Position(position.x, position.y - 1) &&
                c.position != Position(position.x, position.y + 1) &&
                c.position != Position(position.x - 1, position.y) && c.position != Position(position.x + 1, position.y)
            )
            .toSet ++ partialBoard.cells
        )

      case _ => globalboard
    }

  private def getPartialBoard(board: Board[IdCell], cell: IdCell): Board[IdCell] = cell match {
    case IdGeneratorCell(position, orientation, _, _) =>
      orientation match {
        case Right | Left => Board[IdCell](board.cells.filter(_.position.y == position.y))
        case Top | Down   => Board[IdCell](board.cells.filter(_.position.x == position.x))
      }
    case IdMoverCell(position, orientation, _, _) =>
      orientation match {
        case Right | Left => Board[IdCell](board.cells.filter(_.position.y == position.y))
        case Top | Down   => Board[IdCell](board.cells.filter(_.position.x == position.x))
      }
    case IdRotatorCell(position, _, _, _) =>
      Board[IdCell](
        board
          .cells
          .filter(c =>
            c.position == Position(position.x, position.y - 1) || c.position == Position(position.x, position.y + 1) ||
              c.position == Position(position.x - 1, position.y) || c.position == Position(position.x + 1, position.y)
          ) + cell
      )
    case _ => board
  }
}

/* An utility object for prolog serialization and deserialization */
private object PrologParser {

  /* Returns a Prolog cell(id, cellType, x, y) given its Scala cell */
  def serializeCell(cell: IdCell): Term = {
    val cellType: String = cell match {
      case _: IdWallCell  => "wall"
      case _: IdEnemyCell => "enemy"
      case m: IdMoverCell => "mover_" + m.orientation.orientation
      case b: IdBlockCell =>
        "block" + (b.push match {
          case Horizontal => "_hor"
          case Vertical   => "_ver"
          case _          => ""
        })
      case g: IdGeneratorCell => "generator_" + g.orientation.orientation
      case r: IdRotatorCell   => "rotator_" + r.rotation.rotation
    }
    Term.createTerm("cell" + Seq(cell.id, cellType, cell.position.x, cell.position.y).mkString("(", ",", ")"))
  }

  /* Returns a Prolog term given its cell.

     If the cell is a mover or a rotator, it returns mover/ratator_next_state[board, x, y, NB].
     If the cell is a generator, it returns generator_next_state[board, maxId, x, y, NB] */
  def createSerializedPredicate(board: Board[IdCell], maxId: Long, cell: IdCell): Term = {
    var seq = Seq("[" + board.cells.map(serializeCell).mkString(",") + "]", cell.position.x, cell.position.y)
    val action: String = cell match {
      case m: IdMoverCell => "mover_" + m.orientation.orientation
      case g: IdGeneratorCell =>
        seq = seq :+ maxId.toString
        "generator_" + g.orientation.orientation
      case r: IdRotatorCell => "rotator_" + r.rotation.rotation
    }

    seq = seq :+ "NB"

    Term.createTerm(
      action
        + "_next_state"
        + seq.mkString("(", ",", ")")
    )
  }

  /* Returns a Scala Board of fake cells given the Prolog Board */
  def deserializeBoard(stringBoard: String): Board[IdCell] = {
    val regex: Regex =
      "cell\\(\\d+,(?:mover_right|mover_left|mover_top|mover_down|generator_right|generator_left|generator_top|generator_down|rotator_clockwise|rotator_counterclockwise|block|block_hor|block_ver|enemy|wall),\\d+,\\d+\\)".r
    Board(
      regex
        .findAllMatchIn(stringBoard)
        .map(_.toString)
        .map(PrologParser.deserializeCell)
        .toSet
    )
  }

  /* Returns a Scala fake cell given its Prolog cell*/
  def deserializeCell(stringCell: String): IdCell = {
    val s"cell($id,$cellType,$stringX,$stringY)" = stringCell
    val cellId = id.toInt
    val position = Position(stringX.toInt, stringY.toInt)
    val updated = false // default value, properly set in nextState()

    cellType match {
      case s"mover_$orientation" => IdMoverCell(position, EnumHelper.toOrientation(orientation).get, cellId, updated)
      case "enemy"               => IdEnemyCell(position, cellId, updated)
      case "wall"                => IdWallCell(position, cellId, updated)
      case s"block$movement" =>
        IdBlockCell(
          position,
          movement match {
            case "_hor" => Push.Horizontal
            case "_ver" => Push.Vertical
            case _      => Push.Both
          },
          cellId,
          updated
        )
      case s"generator_$orientation" => IdGeneratorCell(position, EnumHelper.toOrientation(orientation).get, cellId, updated)
      case s"rotator_$rotation"      => IdRotatorCell(position, EnumHelper.toRotation(rotation).get, cellId, updated)
    }
  }
}

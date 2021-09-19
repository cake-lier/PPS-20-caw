package engine
import alice.tuprolog.{Prolog, Struct, Term, Theory}
import com.google.gson.{Gson, JsonArray}
import it.unibo.pps.caw.dsl.entities.{Push, PushableCell}
import it.unibo.pps.caw.model.*
import _root_.engine.{Clause, PrologEngine}
import _root_.engine.PrologEngine.extractTerm

import scala.annotation.tailrec
import scala.io.Source
import it.unibo.pps.caw.model.CellTypes.*
import it.unibo.pps.caw.model.AllowedMovement.*
import it.unibo.pps.caw.model.Orientation.*
import it.unibo.pps.caw.model.Board.*

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
      val cellState: IndexedSeq[Boolean] = board.cells.toList
        .sortBy(_.id)
        .map(_.updated)
        .toIndexedSeq
        .updated(cell.id, true) // cell being currently updated to true

      /* Update cell returned by deserializer with correct field IdCell.updated */
      def updateCell(cell: IdCell): IdCell = cell match {
        case c if (c.id >= cellState.length) => c // new cell created by a generator doesn't get updated
        case c                               => CellConverter.toUpdated(c, cellState(c.id))
      }

      val resBoard = PrologParser.deserializeBoard(
        extractTerm(engine(PrologParser.createSerializedPredicate(board, board.cells.size, cell))).toString
      )

      Board(resBoard.cells.map(updateCell))
    }
  }

  def apply(): RulesEngine = RulesEngineImpl()
}

/* An utility object for prolog serialization and deserialization */
private object PrologParser {

  /* Returns a Prolog cell(id, cellType, x, y) given its Scala cell */
  def serializeCell(cell: IdCell): Term = {
    val cellType: String = cell match {
      case _: IdWallCell  => "wall"
      case _: IdEnemyCell => "enemy"
      case m: IdMoverCell => "mover_" + m.orientation.getOrientation
      case b: IdBlockCell =>
        "block" + (b.allowedMovement match {
          case Horizontal => "_hor"
          case Vertical   => "_ver"
          case _          => ""
        })
      case g: IdGeneratorCell => "generator_" + g.orientation.getOrientation
      case r: IdRotatorCell   => "rotate_" + r.rotationDirection.getDirection
    }
    Term.createTerm("cell" + Seq(cell.id, cellType, cell.position.x, cell.position.y).mkString("(", ",", ")"))
  }

  /* Returns a Prolog term given its cell.

     If the cell is a mover or a rotator, it returns mover/ratator_next_state[board, x, y, NB].
     If the cell is a generator, it returns generator_next_state[board, maxId, x, y, NB] */
  def createSerializedPredicate(board: Board[IdCell], maxId: Long, cell: IdCell): Term = {
    var seq = Seq("[" + board.cells.map(serializeCell).mkString(",") + "]", cell.position.x, cell.position.y)
    val action: String = cell match {
      case m: IdMoverCell =>
        "mover_" + (m.orientation match {
          case Right => "right"
          case Left  => "left"
          case Top   => "top"
          case Down  => "down"
        })
      case g: IdGeneratorCell =>
        seq = seq :+ maxId.toString
        "generator_" + (g.orientation match {
          case Right => "right"
          case Left  => "left"
          case Top   => "top"
          case Down  => "down"
        })
      case r: IdRotatorCell =>
        "rotate_" + (r.rotationDirection match {
          case RotationDirection.Left  => "left"
          case RotationDirection.Right => "right"
        })
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
      "cell\\(\\d+,(?:mover_right|mover_left|mover_top|mover_down|generator_right|generator_left|generator_top|generator_down|rotate_right|rotate_left|block|block_hor|block_ver|enemy|wall),\\d+,\\d+\\)".r
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
    val s"cell($id, $cellType,$stringX,$stringY)" = stringCell
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
            case "_hor" => Horizontal
            case "_ver" => Vertical
            case _      => Both
          },
          cellId,
          updated
        )
      case s"generator_$orientation" => IdGeneratorCell(position, EnumHelper.toOrientation(orientation).get, cellId, updated)
      case s"rotate_$rotation"       => IdRotatorCell(position, EnumHelper.toRotation(rotation).get, cellId, updated)
    }
  }
}

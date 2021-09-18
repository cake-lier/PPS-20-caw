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
sealed trait GameEngine {

  /** Calculate the next [[Board]] starting from the current [[Board]] and the [[Cell]] to be updated */
  def nextState(board: Board[IdCell], maxId: Long, cell: IdCell): Board[IdCell]
}

/** Companion object for trait [[GameEngine]] */
object GameEngine {
  private case class GameEngineImpl() extends GameEngine {
    private val engine: Term => Term =
      Using(Source.fromResource("cellmachine.pl")) { c => PrologEngine(Clause(c.getLines.mkString(" "))) }.get

    def nextState(board: Board[IdCell], maxId: Long, cell: IdCell): Board[IdCell] =
      PrologParser.deserializeBoard(
        extractTerm(engine(PrologParser.createSerializedPredicate(board, maxId, cell)), 4).toString
      )
  }
  def apply(): GameEngine = GameEngineImpl()
}

/* An utility object for prolog serialization and deserialization */
private object PrologParser {

  /* Returns a Prolog cell(id, cellType, x, y) given its Scala cell */
  def serializeCell(cell: IdCell): Term = {
    val cellType: String = cell match {
      case _: IdWallCell  => "wall"
      case _: IdEnemyCell => "enemy"
      case m: IdMoverCell => "arrow_" + m.orientation.getOrientation
      case b: IdBlockCell =>
        "block" + (b.allowedMovement match {
          case Horizontal => "_hor"
          case Vertical   => "_ver"
          case _          => ""
        })
      case g: IdGeneratorCell => "generator_" + g.orientation.getOrientation
      case r: IdRotatorCell   => "rotate_" + r.rotationDirection.getDirection
    }
    Term.createTerm("cell" + Seq(cellType, cell.position.x, cell.position.y, cell.id, cell.updated).mkString("(", ",", ")"))
  }

  /* Returns a Prolog term given its cell.

     If the cell is an arrow or a rotator, it returns arrow/ratator_next_state[board, x, y, NB].
     If the cell is a generator, it returns generator_next_state[board, maxId, x, y, NB] */
  def createSerializedPredicate(board: Board[IdCell], maxId: Long, cell: IdCell): Term = {
    val action: String = cell match {
      case m: IdMoverCell =>
        "arrow_" + (m.orientation match {
          case Right => "right"
          case Left  => "left"
          case Top   => "top"
          case Down  => "down"
        })
      case g: IdGeneratorCell =>
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

    println(board.cells.map(serializeCell).mkString(",").toString)

    Term.createTerm(
      action
        + "_next_state"
        + Seq("[" + board.cells.map(serializeCell).mkString(",") + "]", cell.position.x, cell.position.y, maxId, "NB")
          .mkString("(", ",", ")")
    )
  }

  /* Returns a Scala Board of fake cells given the Prolog Board */
  def deserializeBoard(stringBoard: String): Board[IdCell] = {
    val regex: Regex =
      "cell\\((?:arrow_right|arrow_left|arrow_top|arrow_down|generator_right|generator_left|generator_top|generator_down|rotate_right|rotate_left|block|block_hor|block_ver|enemy|wall),\\d+,\\d+\\,\\d+,(?:true|false)\\)".r
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
    val s"cell($cellType,$stringX,$stringY,$id,$update)" = stringCell
    val cellId = id.toLong
    val position = Position(stringX.toInt, stringY.toInt)
    val updated = update.toBoolean

    cellType match {
      case s"arrow_$orientation" => IdMoverCell(position, EnumHelper.toOrientation(orientation).get, cellId, updated)
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

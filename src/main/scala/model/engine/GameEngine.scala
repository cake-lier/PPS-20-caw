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

  /** Calculate the next [[Board]] starting from the current [[Board]] and the [[Cell]] to be updated*/
  def nextState(board: Board, cell: Cell): Board
}

/** Companion object for trait [[GameEngine]] */
object GameEngine {
  private case class GameEngineImpl() extends GameEngine {

    private val engine: Term => Term =
      Using(Source.fromResource("cellmachine.pl")) { c => PrologEngine(Clause(c.getLines.mkString(" "))) }.get

    def nextState(board: Board, cell: Cell): Board =
      PrologParser.deserializeBoard({
        println(engine(PrologParser.createSerializedPredicate(board, cell)));
        extractTerm(engine(PrologParser.createSerializedPredicate(board, cell)), 3).toString
      })
  }
  def apply(): GameEngine = GameEngineImpl()
}

/* An utility object for prolog serialization and deserialization */
private object PrologParser {

  def serializeCell(cell: Cell): Term = {
    val cellType: String = cell match {
      case _: WallCell  => "wall"
      case _: EnemyCell => "enemy"
      case m: MoverCell => "arrow_" + m.orientation.getOrientation
      case b: BlockCell =>
        "block" + (b.allowedMovement match {
          case Horizontal => "_hor"
          case Vertical   => "_ver"
          case _          => ""
        })
      case g: GeneratorCell => "generator_" + g.orientation.getOrientation
      case r: RotatorCell   => "rotate_" + r.rotationDirection.getDirection
    }
    Term.createTerm("cell" + Seq(cellType, cell.position.x, cell.position.y).mkString("(", ",", ")"))
  }

  def createSerializedPredicate(board: Board, cell: Cell): Term = {
    val action: String = cell match {
      case m: MoverCell =>
        "arrow_" + (m.orientation match {
          case Right => "right"
          case Left  => "left"
          case Top   => "top"
          case Down  => "down"
        })
      case g: GeneratorCell =>
        "generator_" + (g.orientation match {
          case Right => "right"
          case Left  => "left"
          case Top   => "top"
          case Down  => "down"
        })
      case r: RotatorCell =>
        "rotate_" + (r.rotationDirection match {
          case RotationDirection.Left  => "left"
          case RotationDirection.Right => "right"
        })
    }
    Term.createTerm(
      action + "_next_state" +
        Seq("[" + board.cells.map(serializeCell).mkString(",") + "]", cell.position.x, cell.position.y, "NB")
          .mkString("(", ",", ")")
    )
  }

  def deserializeBoard(stringBoard: String): Board = {
    val regex: Regex =
      "cell\\((?:arrow_right|arrow_left|arrow_top|arrow_down|generator_right|generator_left|generator_top|generator_down|rotate_right|rotate_left|block|block_hor|block_ver|enemy|wall),\\d+,\\d+\\)".r
    Board(
      regex
        .findAllMatchIn(stringBoard)
        .map(_.toString)
        .map(PrologParser.deserializeCell)
        .toSet
    )
  }

  def deserializeCell(stringCell: String): Cell = {
    val s"cell($cellType,$stringX,$stringY)" = stringCell
    val position = Position(stringX.toInt, stringY.toInt)

    cellType match {
      case s"arrow_$orientation" => MoverCell(position, EnumHelper.toOrientation(orientation).get)
      case "enemy"               => EnemyCell(position)
      case "wall"                => WallCell(position)
      case s"block$movement" =>
        BlockCell(
          position,
          movement match {
            case "_hor" => Horizontal
            case "_ver" => Vertical
            case _      => Both
          }
        )
      case s"generator_$orientation" => GeneratorCell(position, EnumHelper.toOrientation(orientation).get)
      case s"rotate_$rotation"       => RotatorCell(position, EnumHelper.toRotation(rotation).get)
    }
  }
}

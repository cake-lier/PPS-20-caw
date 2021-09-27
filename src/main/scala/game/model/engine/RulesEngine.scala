package it.unibo.pps.caw.game.model.engine

import alice.tuprolog.{Prolog, Struct, Term, Theory}
import com.google.gson.{Gson, JsonArray}
import it.unibo.pps.caw.game.model.*
import it.unibo.pps.caw.common.{Board, Position}

import scala.annotation.tailrec
import scala.io.Source
import scala.util.Using
import scala.util.matching.Regex

/** Engine of game rules */
sealed trait RulesEngine {

  /** Calculate the next [[Board]] starting from the current [[Board]] and the [[Cell]] to be updated */
  def nextState(board: Board[UpdateCell], cell: UpdateCell): Board[UpdateCell]
}

/** Companion object for trait [[RulesEngine]] */
object RulesEngine {

  private class RulesEngineImpl extends RulesEngine {
    private val engine: Term => Term =
      Using(Source.fromResource("cellmachine.pl"))(c => PrologEngine(Clause(c.getLines.mkString(" ")))).get

    def nextState(board: Board[UpdateCell], cell: UpdateCell): Board[UpdateCell] = {
      val cellState: Map[Int, Boolean] =
        board
          .cells
          .map(c => if (c.id == cell.id) (c.id, true) else (c.id, c.updated))
          .toMap
      val resBoard = PrologParser.deserializeBoard(
        PrologEngine
          .extractTerm(
            engine(PrologParser.createSerializedPredicate(getPartialBoard(board, cell), cellState.keySet.max + 1, cell))
          )
          .toString
      )
      updateGloabalBoard(
        board,
        Board(
          resBoard
            .cells
            .map(_ match {
              case c if (c.id > cellState.keySet.max) => setUpdatedState(c, true) // new cell created by a generator
              case c                                  => setUpdatedState(c, cellState(c.id))
            })
        ),
        cell
      )
    }

    private def setUpdatedState(cell: UpdateCell, updated: Boolean): UpdateCell = cell match {
      case UpdateRotatorCell(p, r, i, _)   => UpdateRotatorCell(p, r, i, updated)
      case UpdateGeneratorCell(p, o, i, _) => UpdateGeneratorCell(p, o, i, updated)
      case UpdateEnemyCell(p, i, _)        => UpdateEnemyCell(p, i, updated)
      case UpdateMoverCell(p, o, i, _)     => UpdateMoverCell(p, o, i, updated)
      case UpdateBlockCell(p, d, i, _)     => UpdateBlockCell(p, d, i, updated)
      case UpdateWallCell(p, i, _)         => UpdateWallCell(p, i, updated)
    }

    private def updateGloabalBoard(
      globalboard: Board[UpdateCell],
      partialBoard: Board[UpdateCell],
      cell: UpdateCell
    ): Board[UpdateCell] =
      cell match {
        case UpdateGeneratorCell(position, orientation, _, _) =>
          orientation match {
            case Orientation.Right | Orientation.Left =>
              Board[UpdateCell](globalboard.cells.filter(_.position.y != position.y).toSet ++ partialBoard.cells)
            case Orientation.Top | Orientation.Down =>
              Board[UpdateCell](globalboard.cells.filter(_.position.x != position.x).toSet ++ partialBoard.cells)
          }
        case UpdateMoverCell(position, orientation, _, _) =>
          orientation match {
            case Orientation.Right | Orientation.Left =>
              Board[UpdateCell](globalboard.cells.filter(_.position.y != position.y).toSet ++ partialBoard.cells)
            case Orientation.Top | Orientation.Down =>
              Board[UpdateCell](globalboard.cells.filter(_.position.x != position.x).toSet ++ partialBoard.cells)
          }
        case UpdateRotatorCell(position, _, _, _) =>
          Board[UpdateCell](
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

    private def getPartialBoard(board: Board[UpdateCell], cell: UpdateCell): Board[UpdateCell] = cell match {
      case UpdateGeneratorCell(position, orientation, _, _) =>
        orientation match {
          case Orientation.Right | Orientation.Left => Board[UpdateCell](board.cells.filter(_.position.y == position.y))
          case Orientation.Top | Orientation.Down   => Board[UpdateCell](board.cells.filter(_.position.x == position.x))
        }
      case UpdateMoverCell(position, orientation, _, _) =>
        orientation match {
          case Orientation.Right | Orientation.Left => Board[UpdateCell](board.cells.filter(_.position.y == position.y))
          case Orientation.Top | Orientation.Down   => Board[UpdateCell](board.cells.filter(_.position.x == position.x))
        }
      case UpdateRotatorCell(position, _, _, _) =>
        Board[UpdateCell](
          board
            .cells
            .filter(c =>
              c.position == Position(position.x, position.y - 1) ||
                c.position == Position(position.x, position.y + 1) ||
                c.position == Position(position.x - 1, position.y) ||
                c.position == Position(position.x + 1, position.y)
            ) + cell
        )
      case _ => board
    }
  }

  def apply(): RulesEngine = RulesEngineImpl()
}

/* An utility object for prolog serialization and deserialization */
private object PrologParser {

  /* Returns a Prolog cell(id, cellType, x, y) given its Scala cell */
  def serializeCell(cell: UpdateCell): Term = {
    val cellType: String = cell match {
      case _: UpdateWallCell  => "wall"
      case _: UpdateEnemyCell => "enemy"
      case m: UpdateMoverCell => "mover_" + m.orientation.name
      case b: UpdateBlockCell =>
        "block" + (b.push match {
          case Push.Horizontal => "_hor"
          case Push.Vertical   => "_ver"
          case Push.Both       => ""
        })
      case g: UpdateGeneratorCell => "generator_" + g.orientation.name
      case r: UpdateRotatorCell   => "rotator_" + r.rotation.name
    }
    Term.createTerm("cell" + Seq(cell.id, cellType, cell.position.x, cell.position.y).mkString("(", ",", ")"))
  }

  /* Returns a Prolog term given its cell.

     If the cell is a mover or a rotator, it returns mover/ratator_next_state[board, x, y, NB].
     If the cell is a generator, it returns generator_next_state[board, maxId, x, y, NB] */
  def createSerializedPredicate(board: Board[UpdateCell], maxId: Long, cell: UpdateCell): Term = {
    var seq = Seq("[" + board.cells.map(serializeCell).mkString(",") + "]", cell.position.x, cell.position.y)
    val action: String = cell match {
      case m: UpdateMoverCell => "mover_" + m.orientation.name
      case g: UpdateGeneratorCell =>
        seq = seq :+ maxId.toString
        "generator_" + g.orientation.name
      case r: UpdateRotatorCell => "rotator_" + r.rotation.name
    }

    seq = seq :+ "NB"

    Term.createTerm(
      action
        + "_next_state"
        + seq.mkString("(", ",", ")")
    )
  }

  /* Returns a Scala Board of fake cells given the Prolog Board */
  def deserializeBoard(stringBoard: String): Board[UpdateCell] = {
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
  def deserializeCell(stringCell: String): UpdateCell = {
    val s"cell($id,$cellType,$stringX,$stringY)" = stringCell
    val cellId = id.toInt
    val position = Position(stringX.toInt, stringY.toInt)
    val updated = false // default value, properly set in nextState()

    cellType match {
      case s"mover_$orientation" => UpdateMoverCell(position, Orientation.fromName(orientation).get, cellId, updated)
      case "enemy"               => UpdateEnemyCell(position, cellId, updated)
      case "wall"                => UpdateWallCell(position, cellId, updated)
      case s"block$movement" =>
        UpdateBlockCell(
          position,
          movement match {
            case "_hor" => Push.Horizontal
            case "_ver" => Push.Vertical
            case _      => Push.Both
          },
          cellId,
          updated
        )
      case s"generator_$orientation" =>
        UpdateGeneratorCell(position, Orientation.fromName(orientation).get, cellId, updated)
      case s"rotator_$rotation" => UpdateRotatorCell(position, Rotation.fromName(rotation).get, cellId, updated)
    }
  }
}

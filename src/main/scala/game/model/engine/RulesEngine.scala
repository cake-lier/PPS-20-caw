package it.unibo.pps.caw.game.model.engine

import alice.tuprolog.{Prolog, Struct, Term, Theory}
import com.google.gson.{Gson, JsonArray}
import it.unibo.pps.caw.common.model._
import it.unibo.pps.caw.game.model._
import it.unibo.pps.caw.common.model.cell.{BaseCell, *}

import scala.annotation.tailrec
import scala.io.Source
import scala.util.Using
import scala.util.matching.Regex

/** Engine of game rules */
sealed trait RulesEngine {

  /** Calculate the next [[Board]] starting from the current [[Board]] and the [[Cell]] to be updated */
  def update(currentBoard: Board[BaseCell]): Board[BaseCell]
}

/** Companion object for trait [[RulesEngine]] */
object RulesEngine {

  private class RulesEngineImpl(theory: String) extends RulesEngine {
    private val engine: PrologEngine = PrologEngine(Clause(theory))
    def nextState(board: Board[UpdateCell], cell: UpdateCell): Board[UpdateCell] = {
      val cellState: Map[Int, Boolean] =
        board
          .cells
          .map(c => if (c.id == cell.id) (c.id, true) else (c.id, c.updated))
          .toMap
      val resBoard = PrologParser.deserializeBoard(
        engine
          .solve(Goal(PrologParser.createSerializedPredicate(getPartialBoard(board, cell), cellState.keySet.max + 1, cell)))
          .getLastTerm
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

    override def update(currentBoard: Board[BaseCell]): Board[BaseCell] = {
      @tailrec
      def updateBoard(cells: Seq[UpdateCell], board: Board[UpdateCell]): Board[UpdateCell] = {
        Seq(
          cells.filter(_.isInstanceOf[GeneratorCell]).toSeq.sorted,
          cells.filter(_.isInstanceOf[RotatorCell]).toSeq.sorted,
          cells.filter(_.isInstanceOf[MoverCell]).toSeq.sorted
        ).flatten match {
          case h :: t if (!h.updated) => {
            val newBoard = nextState(board, h)
            updateBoard(newBoard.toSeq, newBoard)
          }
          case h :: t => updateBoard(t, board) // ignore already updated cells
          case _      => board
        }
      }
      val originalBoard: Board[UpdateCell] =
        currentBoard
          .zipWithIndex
          .map((c, i) =>
            c match {
              case BaseMoverCell(o, p)     => UpdateMoverCell(p, o, i, false)
              case BaseGeneratorCell(o, p) => UpdateGeneratorCell(p, o, i, false)
              case BaseRotatorCell(r, p)   => UpdateRotatorCell(p, r, i, false)
              case BaseBlockCell(d, p)     => UpdateBlockCell(p, d, i, false)
              case BaseEnemyCell(p)        => UpdateEnemyCell(p, i, false)
              case BaseWallCell(p)         => UpdateWallCell(p, i, false)
            }
          )

      updateBoard(originalBoard.toSeq, originalBoard)
        .map(_ match {
          case UpdateRotatorCell(p, r, _, _)   => BaseRotatorCell(r)(p)
          case UpdateGeneratorCell(p, o, _, _) => BaseGeneratorCell(o)(p)
          case UpdateEnemyCell(p, _, _)        => BaseEnemyCell(p)
          case UpdateMoverCell(p, o, _, _)     => BaseMoverCell(o)(p)
          case UpdateBlockCell(p, d, _, _)     => BaseBlockCell(d)(p)
          case UpdateWallCell(p, _, _)         => BaseWallCell(p)
        })
    }

  }
  private case class DummyRulesEngine() extends RulesEngine {
    override def update(currentBoard: Board[BaseCell]): Board[BaseCell] = currentBoard
  }
  def apply(theory: String): RulesEngine = RulesEngineImpl(theory)
  def apply(): RulesEngine = DummyRulesEngine()
}

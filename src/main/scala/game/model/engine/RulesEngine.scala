package it.unibo.pps.caw.game.model.engine

import it.unibo.pps.caw.common.model.*
import it.unibo.pps.caw.common.model.cell.*
import it.unibo.pps.caw.game.model.*
import it.unibo.pps.caw.game.model.UpdateCell.toUpdateCell

import scala.annotation.tailrec

/** Engine of game rules */
sealed trait RulesEngine {

  /** Calculate the next [[Board]] starting from the current [[Board]] and the [[Cell]] to be updated */
  def update(currentBoard: Board[BaseCell]): Board[BaseCell]
}

/** Companion object for trait [[RulesEngine]] */
object RulesEngine {

  private class RulesEngineImpl(theory: String) extends RulesEngine {
    private val engine: PrologEngine = PrologEngine(Clause(theory))

    private def nextState(board: Board[UpdateCell], cell: UpdateCell): Board[UpdateCell] = {
      val cellState: Map[Int, Boolean] = board.map(c => if (c.id == cell.id) (c.id, true) else (c.id, c.updated)).toMap
      val resBoard = PrologParser.deserializeBoard(
        engine
          .solve(Goal(PrologParser.createSerializedPredicate(getPartialBoard(board, cell), cellState.keySet.max + 1, cell)))
          .getLastTerm
      )
      updateGlobalBoard(
        board,
        resBoard.map(_ match {
          case c if (c.id > cellState.keySet.max) => c.changeUpdatedProperty(updated = true) // new cell created by a generator
          case c                                  => c.changeUpdatedProperty(cellState(c.id))
        }),
        cell
      )
    }

    private def updateGlobalBoard(
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
      val originalBoard: Board[UpdateCell] = currentBoard.zipWithIndex.map((c, i) => c.toUpdateCell(i, false))
      updateBoard(originalBoard.toSeq, originalBoard).map(_.toBaseCell)
    }
  }

  private case class DummyRulesEngine() extends RulesEngine {

    override def update(currentBoard: Board[BaseCell]): Board[BaseCell] = currentBoard
  }

  def apply(theory: String): RulesEngine = RulesEngineImpl(theory)

  def apply(): RulesEngine = DummyRulesEngine()
}

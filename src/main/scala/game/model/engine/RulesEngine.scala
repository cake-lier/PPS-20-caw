package it.unibo.pps.caw.game.model.engine

import it.unibo.pps.caw.common.model.*
import it.unibo.pps.caw.common.model.cell.*
import it.unibo.pps.caw.game.model.*
import UpdateCell.toUpdateCell

import scala.annotation.tailrec

/** The rules engine of the game, containing all the necessary logic to apply the game rules to the game.
  *
  * It receives the Prolog theory containing all the game rules in its constructor and calculates the subsequent state of a
  * [[it.unibo.pps.caw.common.model.Board]] of cells by applying the correct rule to each ''active'' cell. We define as ''active''
  * cell the [[it.unibo.pps.caw.common.model.cell.GeneratorCell]], the [[it.unibo.pps.caw.common.model.cell.RotatorCell]] and the
  * [[it.unibo.pps.caw.common.model.cell.MoverCell]], since these are cells that are capable of changing the state of surrounding
  * cells by moving, generating or rotating other cells; the other cells, like [[it.unibo.pps.caw.common.model.cell.BlockCell]] or
  * [[it.unibo.pps.caw.common.model.cell.DeleterCell]], though may affect the current state of the board, are ''passive'' cells
  * since they are not able to execute their function until a cell is pushed in their direction, either because it was generated,
  * rotated or moved.
  */
trait RulesEngine {

  /** Calculates the next [[it.unibo.pps.caw.common.model.Board]] by applying the game rules to the current board.
    *
    * @param currentBoard
    *   the current [[it.unibo.pps.caw.common.model.Board]] of [[it.unibo.pps.caw.common.model.cell.BaseCell]]
    * @return
    *   a new updated [[it.unibo.pps.caw.common.model.Board]] of [[it.unibo.pps.caw.common.model.cell.BaseCell]]
    */
  def update(currentBoard: Board[BaseCell]): Board[BaseCell]
}

/** Companion object for the trait [[RulesEngine]], containing its factory methods. */
object RulesEngine {

  /* Default implementation of the RulesEngine trait. */
  private class RulesEngineImpl(theory: String) extends RulesEngine {
    private val engine: PrologEngine = PrologEngine(theory)

    /* Given the current board, calculates the next state of the board by applying the game rules to the given cell.*/
    private def nextState(board: Board[UpdateCell], cell: UpdateCell): Board[UpdateCell] = {
      val cellState: Map[Int, Boolean] = board.map(c => if (c.id == cell.id) (c.id, true) else (c.id, c.updated)).toMap
      val partialBoard: Board[UpdateCell] = extractNeighborhood(board, cell)
      val resBoard: Board[UpdateCell] =
        PrologParser
          .createSerializedPredicate(partialBoard, cellState.keySet.max + 1, cell)
          .map(p => PrologParser.deserializeBoard(engine.solve(p).extractLastTerm))
          .getOrElse(board)
          .map(_ match {
            case c if (c.id > cellState.keySet.max) => c.changeUpdatedProperty(updated = true) // new cell created by a generator
            case c                                  => c.changeUpdatedProperty(cellState(c.id))
          })

      board -- partialBoard ++ resBoard
    }

    private def extractNeighborhood(board: Board[UpdateCell], cell: UpdateCell): Board[UpdateCell] = cell match {
      case UpdateGeneratorCell(p, o, _, _) =>
        o match {
          case Orientation.Right => board.filter(c => c.position.y == p.y && c.position.x >= p.x - 1)
          case Orientation.Left  => board.filter(c => c.position.y == p.y && c.position.x <= p.x + 1)
          case Orientation.Top   => board.filter(c => c.position.x == p.x && c.position.y <= p.y + 1)
          case Orientation.Down  => board.filter(c => c.position.x == p.x && c.position.y >= p.y - 1)
        }
      case UpdateMoverCell(p, o, _, _) =>
        o match {
          case Orientation.Right => board.filter(c => c.position.y == p.y && c.position.x >= p.x)
          case Orientation.Left  => board.filter(c => c.position.y == p.y && c.position.x <= p.x)
          case Orientation.Top   => board.filter(c => c.position.x == p.x && c.position.y <= p.y)
          case Orientation.Down  => board.filter(c => c.position.x == p.x && c.position.y >= p.y)
        }
      case UpdateRotatorCell(p, _, _, _) =>
        board
          .filter(c =>
            c.position == Position(p.x, p.y - 1) ||
              c.position == Position(p.x, p.y + 1) ||
              c.position == Position(p.x - 1, p.y) ||
              c.position == Position(p.x + 1, p.y)
          ) + cell
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

  /* A dummy implementation of the RulesEngine trait to be used when the theory could not be deserialized. */
  private class DummyRulesEngine extends RulesEngine {

    override def update(currentBoard: Board[BaseCell]): Board[BaseCell] = currentBoard
  }

  /** Returns an instance of [[RulesEngine]] trait given the PROLOG theory to be used as rules for the game.
    *
    * @param theory
    *   the PROLOG theory in a string form
    * @return
    *   an new instance of [[RulesEngine]] that will apply the given theory
    */
  def apply(theory: String): RulesEngine = RulesEngineImpl(theory)

  /** Returns an instance of [[RulesEngine]] trait without any theory. Updates will not do anything. */
  def apply(): RulesEngine = DummyRulesEngine()
}

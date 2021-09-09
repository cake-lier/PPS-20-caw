package it.unibo.pps.caw.dsl

import it.unibo.pps.caw.dsl.entities.*
import it.unibo.pps.caw.dsl.words.{AtWord, DirectedWord, FacingWord, InAnAreaWord, MovableWord, WithDimensionsWord}

import scala.collection.mutable.ListBuffer

object CellsAtWorkDSL {
  def board(fun: ListBuffer[Board => Board] ?=> Unit): Unit = {
    given ops: ListBuffer[Board => Board] = ListBuffer()
    fun
    val built: Board = ops.foldLeft(Board())((b, op) => op(b))
    print(built)
  }

  def withDimensions(width: Int, height: Int)(using ops: ListBuffer[Board => Board]): Unit =
    ops += (_.copy(dimensions = Some(Dimensions(width, height))))

  def hasPlayableArea(using ops: ListBuffer[Board => Board]): WithDimensionsWord =
    WithDimensionsWord(d => AtWord(p => ops += (_.copy(playableArea = Some(PlayableArea(d)(p))))))

  def hasMoverCell(using ops: ListBuffer[Board => Board]): FacingWord =
    FacingWord(o => AtWord(p => ops += (b => b.copy(moverCells = b.moverCells + OrientedCell(o)(p)))))

  def hasMoverCells(using ops: ListBuffer[Board => Board]): InAnAreaWord[FacingWord] =
    InAnAreaWord(d =>
      FacingWord(o =>
        AtWord(p =>
          ops += (b =>
            b.copy(moverCells =
              b.moverCells ++ (
                for {
                  x <- 0 until d.width
                  y <- 0 until d.height
                } yield OrientedCell(o)(Position(p.x + x, p.y + y))
              )
            )
          )
        )
      )
    )

  def hasGeneratorCell(using ops: ListBuffer[Board => Board]): FacingWord =
    FacingWord(o => AtWord(p => ops += (b => b.copy(generatorCells = b.generatorCells + OrientedCell(o)(p)))))

  def hasGeneratorCells(using ops: ListBuffer[Board => Board]): InAnAreaWord[FacingWord] =
    InAnAreaWord(d =>
      FacingWord(o =>
        AtWord(p =>
          ops += (b =>
            b.copy(generatorCells =
              b.generatorCells ++ (
                for {
                  x <- 0 until d.width
                  y <- 0 until d.height
                } yield OrientedCell(o)(Position(p.x + x, p.y + y))
              )
            )
          )
        )
      )
    )

  def hasRotatorCell(using ops: ListBuffer[Board => Board]): DirectedWord =
    DirectedWord(d => AtWord(p => ops += (b => b.copy(rotatorCells = b.rotatorCells + DirectedCell(d)(p)))))

  def hasRotatorCells(using ops: ListBuffer[Board => Board]): InAnAreaWord[DirectedWord] =
    InAnAreaWord(w =>
      DirectedWord(d =>
        AtWord(p =>
          ops += (b =>
            b.copy(rotatorCells =
              b.rotatorCells ++ (
                for {
                  x <- 0 until w.width
                  y <- 0 until w.height
                } yield DirectedCell(d)(Position(p.x + x, p.y + y))
              )
            )
          )
        )
      )
    )

  def hasBlockCell(using ops: ListBuffer[Board => Board]): MovableWord =
    MovableWord(d => AtWord(p => ops += (b => b.copy(blockCells = b.blockCells + MovableCell(d)(p)))))

  def hasBlockCells(using ops: ListBuffer[Board => Board]): InAnAreaWord[MovableWord] =
    InAnAreaWord(d =>
      MovableWord(m =>
        AtWord(p =>
          ops += (b =>
            b.copy(blockCells =
              b.blockCells ++ (
                for {
                  x <- 0 until d.width
                  y <- 0 until d.height
                } yield MovableCell(m)(Position(p.x + x, p.y + y))
              )
            )
          )
        )
      )
    )

  def hasEnemyCell(using ops: ListBuffer[Board => Board]): AtWord =
    AtWord(p => ops += (b => b.copy(enemyCells = b.enemyCells + Cell(p))))

  def hasEnemyCells(using ops: ListBuffer[Board => Board]): InAnAreaWord[AtWord] =
    InAnAreaWord(d =>
      AtWord(p =>
        ops += (b =>
          b.copy(enemyCells =
            b.enemyCells ++ (
              for {
                x <- 0 until d.width
                y <- 0 until d.height
              } yield Cell(Position(p.x + x, p.y + y))
            )
          )
        )
      )
    )

  def hasWallCell(using ops: ListBuffer[Board => Board]): AtWord =
    AtWord(p => ops += (b => b.copy(wallCells = b.wallCells + Cell(p))))

  def hasWallCells(using ops: ListBuffer[Board => Board]): InAnAreaWord[AtWord] =
    InAnAreaWord(d =>
      AtWord(p =>
        ops += (b =>
          b.copy(wallCells =
            b.wallCells ++ (
              for {
                x <- 0 until d.width
                y <- 0 until d.height
              } yield Cell(Position(p.x + x, p.y + y))
            )
          )
        )
      )
    )
}

package it.unibo.pps.caw.editor

import io.vertx.core.Vertx
import it.unibo.pps.caw.common.model.{Board, Dimensions, Level, PlayableArea}
import it.unibo.pps.caw.common.model.cell.*
import it.unibo.pps.caw.common.LevelParser
import it.unibo.pps.caw.common.storage.FileStorage
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterAll, Suite}

class SerializerTest extends AnyFunSpec with Matchers with BeforeAndAfterAll { this: Suite =>
  val levelParser = LevelParser(FileStorage())
  val oneCellLevel: Level[BaseCell] =
    Level(Dimensions(20, 20), Board(BaseEnemyCell((5, 5))), PlayableArea((3, 3))((0, 0)))
  val allCellsLevel: Level[BaseCell] = Level(
    Dimensions(20, 20),
    Board(
      BaseWallCell((1, 1)),
      BaseEnemyCell((2, 2)),
      BaseRotatorCell(Rotation.Counterclockwise)((3, 3)),
      BaseRotatorCell(Rotation.Clockwise)((3, 3)),
      BaseGeneratorCell(Orientation.Right)((4, 4)),
      BaseGeneratorCell(Orientation.Left)((4, 4)),
      BaseGeneratorCell(Orientation.Top)((4, 4)),
      BaseGeneratorCell(Orientation.Down)((4, 4)),
      BaseMoverCell(Orientation.Right)((5, 5)),
      BaseMoverCell(Orientation.Left)((5, 5)),
      BaseMoverCell(Orientation.Top)((5, 5)),
      BaseMoverCell(Orientation.Down)((5, 5)),
      BaseBlockCell(Push.Both)((6, 6)),
      BaseBlockCell(Push.Vertical)((6, 6)),
      BaseBlockCell(Push.Horizontal)((6, 6))
    ),
    PlayableArea((3, 3))((0, 0))
  )

  override def afterAll(): Unit = Vertx.vertx().close()

  describe("Serializer") {
    describe("With single cell") {
      it("should produce a valid json") {
        validate(oneCellLevel)
      }
    }
    describe("With all cells") {
      it("should produce a valid json") {
        validate(allCellsLevel)
      }
    }
  }

  private def validate(level: Level[BaseCell]): Unit = levelParser.deserializeLevel(levelParser.serializeLevel(level))
}

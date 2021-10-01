package it.unibo.pps.caw.editor

import io.vertx.core.Vertx
import it.unibo.pps.caw.common.model.{Board, Dimensions, Level, PlayableArea}
import it.unibo.pps.caw.common.model.cell.*
import it.unibo.pps.caw.common.{FileStorage, LevelParser}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterAll, Suite}

class SerializerTest extends AnyFunSpec with Matchers with BeforeAndAfterAll { this: Suite =>
  val levelParser = LevelParser(FileStorage())
  val oneCellLevel: Level[BaseCell] =
    Level(Dimensions(20, 20), Board(BaseEnemyCell((5, 5))), PlayableArea((0, 0), (3, 3)))
  val allCellsLevel: Level[BaseCell] = Level(
    Dimensions(20, 20),
    Board(
      BaseWallCell((1, 1)),
      BaseEnemyCell((2, 2)),
      BaseRotatorCell((3, 3), Rotation.Counterclockwise),
      BaseRotatorCell((3, 3), Rotation.Clockwise),
      BaseGeneratorCell((4, 4), Orientation.Right),
      BaseGeneratorCell((4, 4), Orientation.Left),
      BaseGeneratorCell((4, 4), Orientation.Top),
      BaseGeneratorCell((4, 4), Orientation.Down),
      BaseMoverCell((5, 5), Orientation.Right),
      BaseMoverCell((5, 5), Orientation.Left),
      BaseMoverCell((5, 5), Orientation.Top),
      BaseMoverCell((5, 5), Orientation.Down),
      BaseBlockCell((6, 6), Push.Both),
      BaseBlockCell((6, 6), Push.Vertical),
      BaseBlockCell((6, 6), Push.Horizontal)
    ),
    PlayableArea((0, 0), (3, 3))
  )

  override def afterAll(): Unit = {
    Vertx.vertx().close()
  }

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

  private def validate(level: Level[BaseCell]): Unit =
    for {
      s <- levelParser.serializeLevel(level)
      r <- levelParser.deserializeLevel(s)
    } yield r
}

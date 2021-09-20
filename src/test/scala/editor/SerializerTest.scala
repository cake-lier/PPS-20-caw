package it.unibo.pps.caw.editor
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.json.schema.{Schema, SchemaParser, SchemaRouter, SchemaRouterOptions}
import it.unibo.pps.caw.dsl.entities.PushableCell
import it.unibo.pps.caw.editor.model._
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterAll, Suite}

import scala.io.Source
import scala.util.{Failure, Success, Try, Using}

trait ShoutDownVertx extends BeforeAndAfterAll { this: Suite =>
  override def afterAll(): Unit = {
    Vertx.vertx().close()
  }
}
class SerializerTest extends AnyFunSpec with Matchers with ShoutDownVertx {
  val oneCellLevel = Level(20, 20, Board(EnemyCell((5, 5))), PlayableArea(Position(0, 0), 3, 3))
  val allCellsLevel = Level(
    20,
    20,
    Board(
      WallCell((1, 1)),
      EnemyCell((2, 2)),
      RotatorCell((3, 3), Rotation.Counterclockwise),
      RotatorCell((3, 3), Rotation.Clockwise),
      GeneratorCell((4, 4), Orientation.Right),
      GeneratorCell((4, 4), Orientation.Left),
      GeneratorCell((4, 4), Orientation.Top),
      GeneratorCell((4, 4), Orientation.Down),
      MoverCell((5, 5), Orientation.Right),
      MoverCell((5, 5), Orientation.Left),
      MoverCell((5, 5), Orientation.Top),
      MoverCell((5, 5), Orientation.Down),
      BlockCell((6, 6), Push.Both),
      BlockCell((6, 6), Push.Vertical),
      BlockCell((6, 6), Push.Horizontal)
    ),
    PlayableArea(Position(0, 0), 3, 3)
  )

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

  private def validate(level: Level): Unit =
    Serializer
      .serializeLevel(level)
      .map(Deserializer.deserializeLevel)
      .fold(fail())(
        _ match {
          case Success(_) => succeed
          case _          => fail()
        }
      )
}

package it.unibo.pps.caw.editor
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.json.schema.{Schema, SchemaParser, SchemaRouter, SchemaRouterOptions}
import it.unibo.pps.caw.common.{Board, PlayableArea}
import it.unibo.pps.caw.dsl.entities.PushableCell
import it.unibo.pps.caw.editor.controller.{Deserializer, Serializer}
import it.unibo.pps.caw.editor.model.*
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
  val oneCellLevel = Level(20, 20, Board(SetupEnemyCell((5, 5), true)), PlayableArea((0, 0), 3, 3))
  val allCellsLevel = Level(
    20,
    20,
    Board(
      SetupWallCell((1, 1), true),
      SetupEnemyCell((2, 2), true),
      SetupRotatorCell((3, 3), Rotation.Counterclockwise, true),
      SetupRotatorCell((3, 3), Rotation.Clockwise, true),
      SetupGeneratorCell((4, 4), Orientation.Right, true),
      SetupGeneratorCell((4, 4), Orientation.Left, true),
      SetupGeneratorCell((4, 4), Orientation.Top, true),
      SetupGeneratorCell((4, 4), Orientation.Down, true),
      SetupMoverCell((5, 5), Orientation.Right, true),
      SetupMoverCell((5, 5), Orientation.Left, true),
      SetupMoverCell((5, 5), Orientation.Top, true),
      SetupMoverCell((5, 5), Orientation.Down, true),
      SetupBlockCell((6, 6), Push.Both, true),
      SetupBlockCell((6, 6), Push.Vertical, true),
      SetupBlockCell((6, 6), Push.Horizontal, true)
    ),
    PlayableArea((0, 0), 3, 3)
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
    for {
      s <- Serializer.serializeLevel(level)
      r <- Deserializer.deserializeLevel(s)
    } yield r
}

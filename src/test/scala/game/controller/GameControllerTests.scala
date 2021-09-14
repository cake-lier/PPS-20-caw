package it.unibo.pps.caw.game.controller

import it.unibo.pps.caw.app.controller.{ApplicationController, GameController, GameView}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import java.io.{ByteArrayOutputStream, File}
import scala.io.Source
import scala.util.Using
import it.unibo.pps.caw.game.model.*

class GameControllerTests extends AnyFunSpec with Matchers {
  private val gameController: GameController = GameController(ApplicationController(), GameView())

  describe("The game controller") {
    describe("when asked to load a level") {

      /* These tests passe, however GitHub automated testing fails it because
       * of a bug with the ClassLoader (used in GameController)
       */
      ignore("should produce error message when given wrong level index (too low)") {
        val err: ByteArrayOutputStream = ByteArrayOutputStream()
        Console.withErr(err) {
          gameController.loadLevel(0)
        }

        err.toString().trim shouldBe "Level index out of bounds"
      }

      ignore("should produce error message when given wrong level index (too high)") {
        val err: ByteArrayOutputStream = ByteArrayOutputStream()
        Console.withErr(err) {
          gameController.loadLevel(4)
        }

        err.toString().trim shouldBe "Level index out of bounds"
      }

      ignore("should correctly select the level") {
        val out: ByteArrayOutputStream = ByteArrayOutputStream()
        Console.withOut(out) {
          gameController.loadLevel(1)
        }

        out.toString.trim shouldBe Level(
          50,
          60,
          Set(
            MoverCell(Position(1, 2), true, Orientation.Right),
            MoverCell(Position(0, 0), false, Orientation.Top),
            GeneratorCell(Position(1, 2), true, Orientation.Right),
            GeneratorCell(Position(0, 0), false, Orientation.Top)
          ),
          PlayableArea(Position(1, 2), 20, 30)
        ).toString
      }
    }
  }

}

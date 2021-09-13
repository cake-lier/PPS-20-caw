package it.unibo.pps.caw.app.controller

import it.unibo.pps.caw.app.controller.GameController
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import java.io.{ByteArrayOutputStream, File}
import scala.io.Source
import scala.util.Using

import it.unibo.pps.caw.app.model._

class GameControllerTests extends AnyFunSpec with Matchers {
  private val gameController: GameController = GameController(GameView(), ApplicationController())

  describe("The game controller"){
    describe("when asked to load a level"){
      it("should correctly select the level"){
        val out: ByteArrayOutputStream = ByteArrayOutputStream()
        Console.withOut(out) {
          gameController.loadLevel(1)
        }

        val target: ByteArrayOutputStream = ByteArrayOutputStream()
        Console.withOut(target){
          println(Level(
            50,
            60,
            Set(
              MoverCell(Position(1,2),true, Orientation.Right),
              MoverCell(Position(0,0),false,Orientation.Top),
              GeneratorCell(Position(1,2),true,Orientation.Right),
              GeneratorCell(Position(0,0),false,Orientation.Top)),
            PlayableArea(Position(1,2),20,30)))
        }

        out.toString shouldBe target.toString

      }

      it("should produce IllegalArgumentException when given wrong level index (too low)"){
        assertThrows[IllegalArgumentException](gameController.loadLevel(0))
      }

      it("should produce IllegalArgumentException when given wrong level index (too high)"){
        assertThrows[IllegalArgumentException](gameController.loadLevel(3))
      }

    }
  }

}

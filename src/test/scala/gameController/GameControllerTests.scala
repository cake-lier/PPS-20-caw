package it.unibo.pps.caw.gameController

import it.unibo.pps.caw.controller.GameController
import it.unibo.pps.caw.view.View
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import java.io.{ByteArrayOutputStream, File}
import scala.io.Source
import scala.util.Using

class GameControllerTests extends AnyFunSpec with Matchers {
  private val view: View = View()
  private val gameController: GameController = GameController(view)

  describe("The game controller"){
    describe("when asked to load a level"){
      it("should correctly select the level"){
        val out: ByteArrayOutputStream = ByteArrayOutputStream()
        Console.withOut(out) {
          gameController.loadLevel(1)
        }

        val t : ByteArrayOutputStream = ByteArrayOutputStream()
        Console.withOut(t){
          val levelFile: File = File(ClassLoader.getSystemResource("levels/level1.json").toURI)
          val target: String = Using(Source.fromFile(levelFile))(_.mkString).get
          println(target)
        }

        out.toString shouldBe t.toString
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

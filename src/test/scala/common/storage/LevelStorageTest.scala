package it.unibo.pps.caw
package common.storage

import common.LevelParser
import common.model.*
import common.model.cell.*

import io.vertx.core.json.Json
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import java.io.{File, FileNotFoundException}
import java.nio.file.*
import scala.io.Source
import scala.util.{Failure, Using}

/** Tests for the [[LevelStorage]] trait. */
class LevelStorageTest extends AnyFunSpec with Matchers {
  private val fileStorage = FileStorage()
  private val levelParser = LevelParser(fileStorage)
  private val levelStorage = LevelStorage(fileStorage, levelParser)
  private val level01 = Level(
    (8, 5),
    Board(
      BaseMoverCell(Orientation.Right)((1, 1)),
      BaseEnemyCell((6, 3))
    ),
    PlayableArea((4, 5))((0, 0))
  )
  private val levelSave = Level(
    (10, 8),
    Board(
      BaseMoverCell(Orientation.Right)((3, 2)),
      BaseWallCell((1, 1)),
      BaseEnemyCell((4, 3))
    ),
    PlayableArea((7, 5))((1, 1))
  )
  private val levelSaveTarget =
    """
      |{
      |  "width" : 10,
      |  "height" : 8,
      |  "playableArea" : {
      |    "width" : 7,
      |    "height" : 5,
      |    "x" : 1,
      |    "y" : 1
      |  },
      |  "cells" : {
      |    "mover" : [ {
      |      "x" : 3,
      |      "y" : 2,
      |      "orientation" : "right"    
      |    } ],
      |    "enemy" : [ {
      |      "x" : 4,      
      |      "y" : 3
      |    } ],
      |    "wall" : [ {
      |      "x" : 1,
      |      "y" : 1
      |    } ]
      |  }
      |}
      |""".stripMargin

  describe("LevelStorage") {
    describe("when asked to load a level") {
      it("should correctly load the level") {
        val path = getClass.getResource("/level01.json").getPath
        levelStorage.loadLevel(path).get shouldBe level01
      }
      describe("if the level path is not valid") {
        it("should produce a FileNotFoundException") {
          val path = System.getProperty("user.home") + File.separator + "nonexistentLevel"
          levelStorage.loadLevel(path) match {
            case Failure(_: FileNotFoundException) => succeed
            case _                                 => fail("Did not produce FileNotFoundException")
          }
        }
      }
      describe("if the level is not valid") {
        it("should produce an IllegalArgumentException") {
          val path = getClass.getResource("/invalid_test_level.json").getPath
          levelStorage.loadLevel(path) match {
            case Failure(_: IllegalArgumentException) => succeed
            case _                                    => fail("Did not produce IllegalArgumentException")
          }
        }
      }
    }

    describe("when asked to save a level") {
      it("should correctly save the level") {
        val path = System.getProperty("user.home") + File.separator + "levelStorageTesting.json"
        levelStorage.saveLevel(path, levelSave)
        Json.decodeValue(Using(Source.fromFile(path))(_.getLines.mkString).get) shouldBe Json.decodeValue(levelSaveTarget)
        Files.deleteIfExists(Paths.get(path))
      }
    }
  }
}

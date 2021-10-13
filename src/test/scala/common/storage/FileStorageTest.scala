package it.unibo.pps.caw.common.storage

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import java.io.{File, FileNotFoundException}
import java.nio.file.{Files, InvalidPathException, OpenOption, Paths, StandardOpenOption}
import java.time.LocalDateTime
import scala.io.Source
import scala.util.{Failure, Success, Using}

/** Tests for the [[FileStorage]] trait. */
class FileStorageTest extends AnyFunSpec with Matchers {
  private val fileStorage = FileStorage()

  describe("FileStorage") {
    describe("when asked to load a resource") {
      it("should correctly load the resource") {
        fileStorage.loadResource("level01.json").get shouldBe level01
      }
      describe("if the resource does not exist") {
        it("should produce a FileNotFoundException") {
          fileStorage.loadResource("nonexistant") match {
            case Failure(e: FileNotFoundException) => succeed
            case _                                 => fail("Did not produce FileNotFoundException")
          }
        }
      }
    }

    describe("when asked to load a file") {
      it("should correctly load the file") {
        val testString = "Testing file loading."
        val path = System.getProperty("user.home") + File.separator + "fileStorageTesting"
        Files.writeString(Paths.get(path), testString)
        fileStorage.loadFile(path).get shouldBe testString
        Files.deleteIfExists(Paths.get(path))
      }
      describe("if the file does not exist") {
        it("should produce a FileNotFoundException") {
          val path = System.getProperty("user.home") + File.separator + "nonexistant"
          fileStorage.loadFile(path) match {
            case Failure(e: FileNotFoundException) => succeed
            case _                                 => fail("Did not produce FileNotFoundException")
          }
        }
      }
    }

    describe("when asked to write a file") {
      it("should correctly write a file") {
        val testString = "Testing file writing."
        val path = System.getProperty("user.home") + File.separator + "fileStorageTesting"
        fileStorage.writeFile(path, testString)
        Using(Source.fromFile(path))(_.getLines.mkString).get shouldBe testString
        Files.deleteIfExists(Paths.get(path))
      }
    }
  }

  private val level01 =
    "{   \"width\": 8,   \"height\": 5,   \"playableArea\": {     \"width\": 4,     \"height\": 5,     \"x\": 0,     \"y\": 0   },   \"cells\": {     \"mover\": [       {         \"orientation\": \"right\",         \"x\": 1,         \"y\": 1       }     ],     \"enemy\": [       {         \"x\": 6,         \"y\": 3       }     ]   } }"
}

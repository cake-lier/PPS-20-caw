package it.unibo.pps.caw.common.storage

import com.fasterxml.jackson.core.JsonParseException
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import scala.language.postfixOps
import java.nio.file.InvalidPathException
import java.io.File
import java.nio.file.{Files, Paths}
import scala.io.Source
import scala.util.{Failure, Success, Using}

class SettingsStorageTest extends AnyFunSpec with Matchers {
  private val fileStorage = FileStorage()
  private val settingsStorage = SettingsStorage(fileStorage)
  private val settingsFilePath = System.getProperty("user.home") + File.separator + ".settings_caw.json"

  describe("SettingsStorage") {
    describe("when asked to load the settings") {
      it("should correctly load settings file") {
        Files.writeString(Paths.get(settingsFilePath), "{\"musicVolume\":0.8,\"soundVolume\":0.9,\"solvedLevels\":[7,2]}")
        settingsStorage.load() match {
          case Success(settings: Settings) => settings shouldBe Settings(0.8, 0.9, Set(7, 2))
          case _                           => fail("Settings were not correctly loaded")
        }
      }
      describe("if the settings file didn't already exist") {
        it("should create and load default settings file") {
          Files.deleteIfExists(Paths.get(settingsFilePath))
          val defaultSettings = Settings(musicVolume = 0.5, soundVolume = 0.5, Set())
          settingsStorage.load() match {
            case Success(settings: Settings) => {
              settings shouldBe defaultSettings
              File(settingsFilePath) should exist
            }
            case _ => fail()
          }
        }
      }
      describe("if the settings file is corrupted") {
        it("should produce a JsonParseException") {
          Files.writeString(Paths.get(settingsFilePath), "corrupted settings file")
          settingsStorage.load() match {
            case Failure(e: JsonParseException) => succeed
            case _                              => fail("Did not produce JsonParseException")
          }
        }
      }
    }
    describe("when asked to save settings") {
      it("should correctly write the settings to file") {
        settingsStorage.save(Settings(musicVolume = 0.1, soundVolume = 0.1, solvedLevels = Set(5, 3, 2)))
        Using(Source.fromFile(settingsFilePath))(_.getLines.mkString(" ")) match {
          case Success(s) => s shouldBe "{\"musicVolume\":0.1,\"soundVolume\":0.1,\"solvedLevels\":[5,3,2]}"
          case _          => fail()
        }
      }
    }
  }
}

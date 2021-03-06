package it.unibo.pps.caw.dsl

import it.unibo.pps.caw.common.model.Level
import it.unibo.pps.caw.common.model.cell.BaseCell
import it.unibo.pps.caw.common.LevelParser
import it.unibo.pps.caw.common.storage.{FileStorage, LevelStorage}
import it.unibo.pps.caw.dsl.entities.LevelBuilderState

import scala.collection.mutable.ListBuffer

/* Adds all methods to the DSL that are able to display a built Level.
 *
 * The methods in this module are capable of displaying a Level, after building it, in different ways. They can display it on
 * a console or saving it to a file, but always after checking the correctness of the data stored by the user and serializing it
 * in JSON format.
 */
private trait LevelDisplayers {
  import it.unibo.pps.caw.dsl.validation.LevelBuilderStateValidator

  /* Generalizes the behavior of the methods in this module. */
  private def executeAction(ops: ListBuffer[LevelBuilderState => LevelBuilderState])(action: Level[BaseCell] => Unit): Unit =
    ops += (b => {
      LevelBuilderStateValidator.validateBuilderState(b) match {
        case Right(v) => action(v)
        case Left(l)  => Console.err.print(l.map(_.message).mkString("\n"))
      }
      b
    })

  private val fileStorage: FileStorage = FileStorage()
  private val levelParser: LevelParser = LevelParser(fileStorage)
  private val levelStorage: LevelStorage = LevelStorage(fileStorage, levelParser)

  /** Prints a built [[it.unibo.pps.caw.common.model.Level]] on the standard output after checking the correctness of the stored
    * data and serializing it in JSON format.
    *
    * @param ops
    *   the list of operations to which add this specific operation
    */
  def printIt(using ops: ListBuffer[LevelBuilderState => LevelBuilderState]): Unit =
    executeAction(ops)(l => print(levelParser.serializeLevel(l)))

  /** Saves a built [[it.unibo.pps.caw.common.model.Level]] on the file which path is given after checking the correctness of the
    * stored data and serializing it in JSON format.
    *
    * @param path
    *   the path of the file to which save the built [[it.unibo.pps.caw.common.model.Level]]
    * @param ops
    *   the list of operations to which add this specific operation
    */
  def saveIt(path: String)(using ops: ListBuffer[LevelBuilderState => LevelBuilderState]): Unit =
    executeAction(ops)(levelStorage.saveLevel(path, _))

  import java.time.LocalDateTime
  import java.nio.file.Files

  /* Launches an application with the given launcher after storing the level being currently built onto in a temporary file. */
  private def launchApplication(ops: ListBuffer[LevelBuilderState => LevelBuilderState])(launcher: Array[String] => Unit): Unit =
    executeAction(ops)(l => {
      val tempPath: String = Files.createTempFile(s"level_${LocalDateTime.now()}", ".json").toString
      levelStorage.saveLevel(tempPath, l)
      launcher(Array(tempPath))
    })

  /** Opens the application for playing a [[it.unibo.pps.caw.common.model.Level]] as created by the user through the DSL after
    * checking the correctness of the stored data and serializing it in JSON format into a temporary file. This means that, if not
    * coupled with another action intended to saving the file to a specific location, after the closing of the launched
    * application, the file containing the level will not be stored anywhere. No options for saving the file will be shown
    * in-game.
    *
    * @param ops
    *   the list of operations to which add this specific operation
    */
  def playIt(using ops: ListBuffer[LevelBuilderState => LevelBuilderState]): Unit = launchApplication(ops)(DSLGameMain.main(_))

  /** Opens the application for editing a [[it.unibo.pps.caw.common.model.Level]] as created by the user through the DSL after
    * checking the correctness of the stored data and serializing it in JSON format into a temporary file. This means that, if not
    * coupled with another action intended to saving the file to a specific location, after the closing of the launched
    * application, the file containing the level will not be stored anywhere. An option for saving the file will be shown while
    * the editor is open.
    *
    * @param ops
    *   the list of operations to which add this specific operation
    */
  def editIt(using ops: ListBuffer[LevelBuilderState => LevelBuilderState]): Unit = launchApplication(ops)(DSLEditorMain.main(_))
}

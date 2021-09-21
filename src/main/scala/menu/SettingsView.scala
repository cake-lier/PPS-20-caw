package it.unibo.pps.caw.menu

import it.unibo.pps.caw.ViewComponent.AbstractViewComponent
import it.unibo.pps.caw.menu.{LevelSelectionView, ParentMainMenuController}
import it.unibo.pps.caw.{AudioPlayer, AudioType, SoundButton, Track, ViewComponent}
import javafx.fxml.FXML
import javafx.scene.layout.{GridPane, Pane}
import scalafx.scene.Scene
import javafx.scene.control.Slider
import scalafx.stage.FileChooser

/** The "settings" page on the main menu.
  *
  * This view component represents the "settings" screen, which is part of the main menu. As such, its duty is to capture all
  * interactions with this specific part of the view and provide the expected functionalities such as applying the chosen volumes
  * for the audio and the music through the [[SettingsController]]. It must be constructed through its companion object.
  */
trait SettingsView extends ViewComponent[Pane]

/** Companion object of the [[SettingsView]] trait, containing its factory method. */
object SettingsView {

  /** Returns a new instance of the [[SettingsView]] trait. It receives the [[SettingsController]] so the constructed view can
    * provide the services which should be accessible through itself, the [[AudioPlayer]] to be used for playing sounds and music
    * and the ScalaFX's [[Scene]] in order to draw and display itself.
    *
    * @param controller
    *   the [[SettingsController]] associated to the created [[SettingsView]]
    * @param audioPlayer
    *   the [[AudioPlayer]] to be used for playing sounds and music
    * @param scene
    *   the ScalaFX's [[Scene]] on which the constructed [[SettingsView]] will draw and display itself
    * @return
    *   a new [[SettingsView]] instance
    */
  def apply(controller: SettingsController, audioPlayer: AudioPlayer, scene: Scene): SettingsView =
    SettingsViewImpl(controller, audioPlayer, scene)

  /* Default implementation of the SettingsView trait. */
  private final class SettingsViewImpl(controller: SettingsController, audioPlayer: AudioPlayer, scene: Scene)
    extends AbstractViewComponent[Pane]("settings_page.fxml")
    with SettingsView {
    @FXML
    var musicVolumeSlider: Slider = _
    @FXML
    var effectsVolumeSlider: Slider = _
    @FXML
    var backFromSettingsButton: SoundButton = _

    override val innerComponent: Pane = loader.load[GridPane]

    setupSlider(musicVolumeSlider, AudioType.Music)
    setupSlider(effectsVolumeSlider, AudioType.Sound)
    backFromSettingsButton.setOnMouseClicked(_ => controller.backToMainMenu())

    private def setupSlider(slider: Slider, audioType: AudioType): Unit = {
      slider.setValue(audioPlayer.getVolume(audioType) * slider.getMax)
      slider
        .valueProperty()
        .addListener((_, _, v) => {
          val roundedValue = Math.floor(v.doubleValue / slider.getBlockIncrement) * slider.getBlockIncrement
          slider.valueProperty.set(roundedValue)
          audioPlayer.setVolume(roundedValue / slider.getMax, audioType)
        })
    }
  }
}

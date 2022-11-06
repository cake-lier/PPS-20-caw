package it.unibo.pps.caw
package menu.view

import common.view.ViewComponent.AbstractViewComponent
import common.view.sounds.{AudioPlayer, AudioType}
import common.view.ViewComponent
import menu.controller.SettingsController

import javafx.fxml.FXML
import javafx.scene.control.{Button, Slider}
import javafx.scene.layout.{GridPane, Pane}
import scalafx.scene.Scene

/** The "settings" page on the main menu.
  *
  * This view component represents the "settings" screen, which is part of the main menu. As such, its duty is to capture all
  * interactions with this specific part of the view and provide the expected functionalities such as applying the chosen volumes
  * for the audio and the music through the [[it.unibo.pps.caw.menu.controller.SettingsController]]. It must be constructed
  * through its companion object.
  */
trait SettingsView extends ViewComponent[Pane]

/** Companion object of the [[SettingsView]] trait, containing its factory method. */
object SettingsView {

  /** Returns a new instance of the [[SettingsView]] trait. It receives the
    * [[it.unibo.pps.caw.menu.controller.SettingsController]] so the constructed view can provide the services which should be
    * accessible through itself, the [[it.unibo.pps.caw.common.view.sounds.AudioPlayer]] to be used for playing sounds and music
    * and the ScalaFX [[scalafx.scene.Scene]] in order to draw and display itself.
    *
    * @param controller
    *   the [[it.unibo.pps.caw.menu.controller.SettingsController]] associated to the created [[SettingsView]]
    * @param audioPlayer
    *   the [[it.unibo.pps.caw.common.view.sounds.AudioPlayer]] to be used for playing sounds and music
    * @param scene
    *   the ScalaFX [[scalafx.scene.Scene]] on which the constructed [[SettingsView]] will draw and display itself
    * @return
    *   a new [[SettingsView]] instance
    */
  def apply(controller: SettingsController, audioPlayer: AudioPlayer, scene: Scene): SettingsView =
    SettingsViewImpl(controller, audioPlayer)

  /* Default implementation of the SettingsView trait. */
  private final class SettingsViewImpl(controller: SettingsController, audioPlayer: AudioPlayer)
    extends AbstractViewComponent[Pane]("settings_page.fxml")
    with SettingsView {
    @FXML
    var musicVolumeSlider: Slider = _
    @FXML
    var effectsVolumeSlider: Slider = _
    @FXML
    var backFromSettingsButton: Button = _

    override val innerComponent: Pane = loader.load[GridPane]

    private var volumes: Map[AudioType, Double] =
      Map(AudioType.Music -> controller.musicVolume, AudioType.Sound -> controller.soundsVolume)

    setupSlider(musicVolumeSlider, AudioType.Music)
    setupSlider(effectsVolumeSlider, AudioType.Sound)
    backFromSettingsButton.setOnMouseClicked(_ => {
      controller.saveVolumeSettings(volumes(AudioType.Music), volumes(AudioType.Sound))
      controller.goBack()
    })

    /* Performs the setup of a generic slider given the type of audio which is related to. */
    private def setupSlider(slider: Slider, audioType: AudioType): Unit = {
      slider.setValue(volumes(audioType) * slider.getMax)
      slider
        .valueProperty()
        .addListener((_, _, v) => {
          val roundedValue = Math.floor(v.doubleValue / slider.getBlockIncrement) * slider.getBlockIncrement
          slider.valueProperty.set(roundedValue)
          audioPlayer.setVolume(roundedValue / slider.getMax, audioType)
          volumes += (audioType -> roundedValue / slider.getMax)
        })
    }
  }
}

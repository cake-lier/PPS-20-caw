package it.unibo.pps.caw.menu

import it.unibo.pps.caw.ViewComponent.AbstractViewComponent
import it.unibo.pps.caw.menu.{LevelSelectionView, ParentMainMenuController}
import it.unibo.pps.caw.{AudioPlayer, AudioType, SoundButton, Track, ViewComponent}
import javafx.fxml.FXML
import javafx.scene.layout.{GridPane, Pane}
import scalafx.scene.Scene
import javafx.scene.control.Slider
import scalafx.stage.FileChooser

/** */
trait SettingsView extends ViewComponent[Pane]

object SettingsView {
  def apply(controller: SettingsController, audioPlayer: AudioPlayer, scene: Scene): SettingsView =
    SettingsViewImpl(controller, audioPlayer, scene)

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
    backFromSettingsButton.setOnMouseClicked(_ => controller.goBack())

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

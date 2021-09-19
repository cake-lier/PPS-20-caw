package it.unibo.pps.caw
package settings

import it.unibo.pps.caw.ViewComponent.AbstractViewComponent
import it.unibo.pps.caw.menu.{LevelSelectionView, ParentMainMenuController}
import it.unibo.pps.caw.{AudioPlayer, Track, ViewComponent}
import javafx.fxml.FXML
import javafx.scene.layout.{GridPane, Pane}
import scalafx.scene.Scene
import javafx.scene.control.Slider
import scalafx.stage.FileChooser

trait SettingsView extends ViewComponent[Pane]

object SettingsView {
  def apply(parentController: ParentSettingsController, audioPlayer: AudioPlayer, scene: Scene): SettingsView =
    SettingsViewImpl(parentController, audioPlayer, scene)

  private final class SettingsViewImpl(parentController: ParentSettingsController, audioPlayer: AudioPlayer, scene: Scene)
      extends AbstractViewComponent[Pane]("settings_menu_page.fxml")
      with SettingsView {

    @FXML
    var musicVolumeSlider: Slider = _

    @FXML
    var effectsVolumeSlider: Slider = _

    @FXML
    var backFromSettingsButton: SoundButton = _

    override val innerComponent: Pane = loader.load[GridPane]

    private val controller: SettingsController = SettingsController(parentController)

    attachChangeVolume(musicVolumeSlider, AudioType.Music)
    attachChangeVolume(effectsVolumeSlider, AudioType.Sound)
    backFromSettingsButton.setOnMouseClicked(_ => controller.back())

    private def attachChangeVolume(slider: Slider, audioType: AudioType) =
      slider
        .valueProperty()
        .addListener((_, _, newValue) => {
          println(newValue.doubleValue()); audioPlayer.setVolume(newValue.doubleValue(), audioType)
        })
  }
}

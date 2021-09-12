package it.unibo.pps.caw.view.game

import it.unibo.pps.caw.model._
import it.unibo.pps.caw.view.ViewComponent
import it.unibo.pps.caw.view.ViewComponent.AbstractViewComponent
import javafx.fxml.FXML
import javafx.scene.image.{Image, ImageView}
import javafx.scene.input.{ClipboardContent, TransferMode}


object CellView {
  def apply(cell: Cell, size: Double): ViewComponent[ImageView] =
    new CellImpl(cell, size)

  private class CellImpl(cell: Cell, size: Double) extends AbstractViewComponent[ImageView]("cell.fxml") {
    override val innerComponent: ImageView = loader.load[ImageView]
    innerComponent.setFitWidth(size)
    innerComponent.setFitHeight(size)
    getImage()

    private def getImage(): Unit = {
      cell match {
        case RotatorCell(_, _, rotationDirection) =>
          setImage(CellTypes.Rotator.getType.toLowerCase())
          innerComponent.setScaleX(rotationDirection match {
            case RotationDirection.Right => 0
            case RotationDirection.Left => -1
          })
        case GeneratorCell(_, _, orientation) =>
          setImage(CellTypes.Generator.getType.toLowerCase())
          setImageViewOrientation(orientation)
        case EnemyCell(_, _) =>
          setImage(CellTypes.Enemy.getType.toLowerCase())
        case MoverCell(_, _, orientation) =>
          setImage(CellTypes.Mover.getType.toLowerCase())
          setImageViewOrientation(orientation)
        case BlockCell(_, _, allowedMovement) =>
          allowedMovement match {
            case AllowedMovement.Both => setImage(CellTypes.Block.getType.toLowerCase())
            case AllowedMovement.Vertical => setImage("slide")
            case AllowedMovement.Horizontal =>
              setImage("slide")
              innerComponent.setRotate(90)
          }
        case WallCell(_, _) =>
          setImage(CellTypes.Wall.getType.toLowerCase)
      }
    }

    private def setImageViewOrientation(orientation: Orientation): Unit = {
      innerComponent.setRotate(orientation match {
        case Orientation.Right => 0
        case Orientation.Down => 90
        case Orientation.Left => 180
        case Orientation.Top => 270
      })
    }

    private def setImage(cellType: String):Unit = {
      innerComponent.setImage(new Image("imgs/" + cellType + ".png"))
    }
  }
}

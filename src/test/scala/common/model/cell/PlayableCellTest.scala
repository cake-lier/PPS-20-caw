package it.unibo.pps.caw
package common.model.cell

import common.model.cell.PlayableCell.toPlayableCell
import common.model.Position

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/** Tests for the [[PlayableCell]] trait and all its subtypes. */
class PlayableCellTest extends AnyFunSpec with Matchers {
  private val position: Position = Position(1, 2)
  private val updatedPosition: Position = Position(2, 3)
  private def changePosition(p: Position): Position = (p.x + 1, p.y + 1)
  private val playable: Boolean = true

  describe("An enemy cell") {
    describe("when first created") {
      it("should return the given position") {
        val cell: PlayableEnemyCell = PlayableEnemyCell(position)(playable)
        cell.position shouldBe position
        cell.playable shouldBe playable
      }
    }
    describe("when asked to extract cell properties") {
      it("should return the correct properties") {
        val cell: PlayableEnemyCell = PlayableEnemyCell(position)(playable)
        PlayableEnemyCell.unapply(cell) shouldBe (position, playable)
      }
    }
    describe("when asked to change the position property") {
      it("should correctly update the position") {
        val cell: PlayableEnemyCell = PlayableEnemyCell(position)(playable)
        cell.changePositionProperty(changePosition) shouldBe PlayableEnemyCell(updatedPosition)(playable)
      }
    }
    describe("when asked to change the playable property") {
      it("should correctly update the playable property") {
        PlayableEnemyCell(position)(true).changePlayableProperty(false) shouldBe PlayableEnemyCell(position)(false)
      }
    }
    describe("when converted to its corresponding BaseCell") {
      it("should get converted correctly") {
        PlayableEnemyCell(position)(playable).toBaseCell shouldBe BaseEnemyCell(position)
      }
    }
    describe("when converted from its corresponding BaseCell") {
      it("should get converted correctly") {
        BaseEnemyCell(position).toPlayableCell(_ => playable) shouldBe PlayableEnemyCell(position)(playable)
      }
    }
  }

  describe("A wall cell") {
    describe("when first created") {
      it("should return the given position") {
        val cell: PlayableWallCell = PlayableWallCell(position)(playable)
        cell.position shouldBe position
        cell.playable shouldBe playable
      }
    }
    describe("when asked to extract cell properties") {
      it("should return the correct properties") {
        val cell: PlayableWallCell = PlayableWallCell(position)(playable)
        PlayableWallCell.unapply(cell) shouldBe (position, playable)
      }
    }
    describe("when asked to change the position property") {
      it("should correctly update the position") {
        val cell: PlayableWallCell = PlayableWallCell(position)(playable)
        cell.changePositionProperty(changePosition) shouldBe PlayableWallCell(updatedPosition)(playable)
      }
    }
    describe("when asked to change the playable property") {
      it("should correctly update the playable property") {
        PlayableWallCell(position)(true).changePlayableProperty(false) shouldBe PlayableWallCell(position)(false)
      }
    }
    describe("when converted to its corresponding BaseCell") {
      it("should get converted correctly") {
        PlayableWallCell(position)(playable).toBaseCell shouldBe BaseWallCell(position)
      }
    }
    describe("when converted from its corresponding BaseCell") {
      it("should get converted correctly") {
        BaseWallCell(position).toPlayableCell(_ => playable) shouldBe PlayableWallCell(position)(playable)
      }
    }
  }

  describe("A mover cell") {
    describe("when first created") {
      it("should return the given orientation and position") {
        val orientation: Orientation = Orientation.Right
        val cell: PlayableMoverCell = PlayableMoverCell(orientation)(position)(playable)
        cell.position shouldBe position
        cell.orientation shouldBe orientation
        cell.playable shouldBe playable
      }
    }
    describe("when asked to extract cell properties") {
      it("should return the correct properties") {
        val orientation: Orientation = Orientation.Right
        val cell: PlayableMoverCell = PlayableMoverCell(orientation)(position)(playable)
        PlayableMoverCell.unapply(cell) shouldBe (orientation, position, playable)
      }
    }
    describe("when asked to change the position property") {
      it("should correctly update the position") {
        val orientation: Orientation = Orientation.Right
        val cell: PlayableMoverCell = PlayableMoverCell(orientation)(position)(playable)
        cell.changePositionProperty(changePosition) shouldBe PlayableMoverCell(orientation)(updatedPosition)(playable)
      }
    }
    describe("when asked to change the playable property") {
      it("should correctly update the playable property") {
        val orientation: Orientation = Orientation.Right
        PlayableMoverCell(orientation)(position)(true).changePlayableProperty(false) shouldBe
          PlayableMoverCell(orientation)(position)(false)
      }
    }
    describe("when converted to its corresponding BaseCell") {
      it("should get converted correctly") {
        val orientation: Orientation = Orientation.Right
        PlayableMoverCell(orientation)(position)(playable).toBaseCell shouldBe BaseMoverCell(orientation)(position)
      }
    }
    describe("when converted from its corresponding BaseCell") {
      it("should get converted correctly") {
        val orientation: Orientation = Orientation.Right
        BaseMoverCell(orientation)(position).toPlayableCell(_ => playable) shouldBe
          PlayableMoverCell(orientation)(position)(playable)
      }
    }
  }

  describe("A generator cell") {
    describe("when first created") {
      it("should return the given orientation and position") {
        val orientation: Orientation = Orientation.Right
        val cell: PlayableGeneratorCell = PlayableGeneratorCell(orientation)(position)(playable)
        cell.position shouldBe position
        cell.orientation shouldBe orientation
        cell.playable shouldBe playable
      }
    }
    describe("when asked to extract cell properties") {
      it("should return the correct properties") {
        val orientation: Orientation = Orientation.Right
        val cell: PlayableGeneratorCell = PlayableGeneratorCell(orientation)(position)(playable)
        PlayableGeneratorCell.unapply(cell) shouldBe (orientation, position, playable)
      }
    }
    describe("when asked to change the position property") {
      it("should correctly update the position") {
        val orientation: Orientation = Orientation.Right
        val cell: PlayableGeneratorCell = PlayableGeneratorCell(orientation)(position)(playable)
        cell.changePositionProperty(changePosition) shouldBe PlayableGeneratorCell(orientation)(updatedPosition)(playable)
      }
    }
    describe("when asked to change the playable property") {
      it("should correctly update the playable property") {
        val orientation: Orientation = Orientation.Right
        PlayableGeneratorCell(orientation)(position)(true).changePlayableProperty(false) shouldBe
          PlayableGeneratorCell(orientation)(position)(false)
      }
    }
    describe("when converted to its corresponding BaseCell") {
      it("should get converted correctly") {
        val orientation: Orientation = Orientation.Right
        PlayableGeneratorCell(orientation)(position)(playable).toBaseCell shouldBe BaseGeneratorCell(orientation)(position)
      }
    }
    describe("when converted from its corresponding BaseCell") {
      it("should get converted correctly") {
        val orientation: Orientation = Orientation.Right
        BaseGeneratorCell(orientation)(position).toPlayableCell(_ => playable) shouldBe
          PlayableGeneratorCell(orientation)(position)(playable)
      }
    }
  }

  describe("A rotation cell") {
    describe("when first created") {
      it("should return the given rotation and position") {
        val rotation: Rotation = Rotation.Clockwise
        val cell: PlayableRotatorCell = PlayableRotatorCell(rotation)(position)(playable)
        cell.position shouldBe position
        cell.rotation shouldBe rotation
        cell.playable shouldBe playable
      }
    }
    describe("when asked to extract cell properties") {
      it("should return the correct properties") {
        val rotation: Rotation = Rotation.Clockwise
        val cell: PlayableRotatorCell = PlayableRotatorCell(rotation)(position)(playable)
        PlayableRotatorCell.unapply(cell) shouldBe (rotation, position, playable)
      }
    }
    describe("when asked to change the position property") {
      it("should correctly update the position") {
        val rotation: Rotation = Rotation.Clockwise
        val cell: PlayableRotatorCell = PlayableRotatorCell(rotation)(position)(playable)
        cell.changePositionProperty(changePosition) shouldBe PlayableRotatorCell(rotation)(updatedPosition)(playable)
      }
    }
    describe("when asked to change the playable property") {
      it("should correctly update the playable property") {
        val rotation: Rotation = Rotation.Clockwise
        PlayableRotatorCell(rotation)(position)(true).changePlayableProperty(false) shouldBe
          PlayableRotatorCell(rotation)(position)(false)
      }
    }
    describe("when converted to its corresponding BaseCell") {
      it("should get converted correctly") {
        val rotation: Rotation = Rotation.Clockwise
        PlayableRotatorCell(rotation)(position)(playable).toBaseCell shouldBe BaseRotatorCell(rotation)(position)
      }
    }
    describe("when converted from its corresponding BaseCell") {
      it("should get converted correctly") {
        val rotation: Rotation = Rotation.Clockwise
        BaseRotatorCell(rotation)(position).toPlayableCell(_ => playable) shouldBe
          PlayableRotatorCell(rotation)(position)(playable)
      }
    }
  }

  describe("A block cell") {
    describe("when first created") {
      it("should return the given push and position") {
        val push: Push = Push.Vertical
        val cell: PlayableBlockCell = PlayableBlockCell(push)(position)(playable)
        cell.position shouldBe position
        cell.push shouldBe push
        cell.playable shouldBe playable
      }
    }
    describe("when asked to extract cell properties") {
      it("should return the correct properties") {
        val push: Push = Push.Vertical
        val cell: PlayableBlockCell = PlayableBlockCell(push)(position)(playable)
        PlayableBlockCell.unapply(cell) shouldBe (push, position, playable)
      }
    }
    describe("when asked to change the position property") {
      it("should correctly update the position") {
        val push: Push = Push.Vertical
        val cell: PlayableBlockCell = PlayableBlockCell(push)(position)(playable)
        cell.changePositionProperty(changePosition) shouldBe PlayableBlockCell(push)(updatedPosition)(playable)
      }
    }
    describe("when asked to change the playable property") {
      it("should correctly update the playable property") {
        val push: Push = Push.Vertical
        PlayableBlockCell(push)(position)(true).changePlayableProperty(false) shouldBe
          PlayableBlockCell(push)(position)(false)
      }
    }
    describe("when converted to its corresponding BaseCell") {
      it("should get converted correctly") {
        val push: Push = Push.Vertical
        PlayableBlockCell(push)(position)(playable).toBaseCell shouldBe BaseBlockCell(push)(position)
      }
    }
    describe("when converted from its corresponding BaseCell") {
      it("should get converted correctly") {
        val push: Push = Push.Vertical
        BaseBlockCell(push)(position).toPlayableCell(_ => playable) shouldBe PlayableBlockCell(push)(position)(playable)
      }
    }
  }

  describe("A deleter cell") {
    describe("when first created") {
      it("should return the given position") {
        val cell: PlayableDeleterCell = PlayableDeleterCell(position)(playable)
        cell.position shouldBe position
        cell.playable shouldBe playable
      }
    }
    describe("when asked to extract cell properties") {
      it("should return the correct properties") {
        val cell: PlayableDeleterCell = PlayableDeleterCell(position)(playable)
        PlayableDeleterCell.unapply(cell) shouldBe (position, playable)
      }
    }
    describe("when asked to change the position property") {
      it("should correctly update the position") {
        val cell: PlayableDeleterCell = PlayableDeleterCell(position)(playable)
        cell.changePositionProperty(changePosition) shouldBe PlayableDeleterCell(updatedPosition)(playable)
      }
    }
    describe("when asked to change the playable property") {
      it("should correctly update the playable property") {
        PlayableDeleterCell(position)(true).changePlayableProperty(false) shouldBe PlayableDeleterCell(position)(false)
      }
    }
    describe("when converted to its corresponding BaseCell") {
      it("should get converted correctly") {
        PlayableDeleterCell(position)(playable).toBaseCell shouldBe BaseDeleterCell(position)
      }
    }
    describe("when converted from its corresponding BaseCell") {
      it("should get converted correctly") {
        BaseDeleterCell(position).toPlayableCell(_ => playable) shouldBe PlayableDeleterCell(position)(playable)
      }
    }
  }
}

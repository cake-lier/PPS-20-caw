package it.unibo.pps.caw.game.model

import it.unibo.pps.caw.game.model.UpdateCell.toUpdateCell
import it.unibo.pps.caw.common.model.Position
import it.unibo.pps.caw.common.model.cell._
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/** Tests for the [[UpdateCell]] trait and all its subtypes. */
class UpdateCellTest extends AnyFunSpec with Matchers {
  private val position: Position = Position(1, 2)
  private val newPosition: Position = Position(8, 8)
  private val id: Int = 1
  private val newId: Int = 3
  private val updated: Boolean = true

  describe("An enemy cell") {
    describe("when first created") {
      it("should return the given position") {
        val cell: UpdateEnemyCell = UpdateEnemyCell(position, id, updated)
        cell.position shouldBe position
        cell.id shouldBe id
        cell.updated shouldBe updated
      }
    }
    describe("when asked to extract cell properties") {
      it("should return the correct properties") {
        val cell: UpdateEnemyCell = UpdateEnemyCell(position, id, updated)
        UpdateEnemyCell.unapply(cell) shouldBe (position, id, updated)
      }
    }
    describe("when asked to produce a modified copy of itself") {
      it("should return a new cell with the same properties as the original, unless specified otherwise") {
        val cell: UpdateEnemyCell = UpdateEnemyCell(position, id, updated)
        cell.copy(id = newId) shouldBe UpdateEnemyCell(position, newId, updated)
        cell.copy(position = newPosition, id = newId) shouldBe UpdateEnemyCell(newPosition, newId, updated)
      }
    }
    describe("when asked to change the updated property") {
      it("should correctly update the updated property") {
        val cell: UpdateEnemyCell = UpdateEnemyCell(position, id, updated = true)
        cell.changeUpdatedProperty(false) shouldBe UpdateEnemyCell(position, id, false)
      }
    }
    describe("when converted to its corresponding BaseCell") {
      it("should get converted correctly") {
        UpdateEnemyCell(position, id, updated).toBaseCell shouldBe BaseEnemyCell(position)
      }
    }
    describe("when converted from its corresponding BaseCell") {
      it("should get converted correctly") {
        BaseEnemyCell(position).toUpdateCell(id, updated) shouldBe UpdateEnemyCell(position, id, updated)
      }
    }
  }

  describe("A wall cell") {
    describe("when first created") {
      it("should return the given position") {
        val cell: UpdateWallCell = UpdateWallCell(position, id, updated)
        cell.position shouldBe position
        cell.id shouldBe id
        cell.updated shouldBe updated
      }
    }
    describe("when asked to extract cell properties") {
      it("should return the correct properties") {
        val cell: UpdateWallCell = UpdateWallCell(position, id, updated)
        UpdateWallCell.unapply(cell) shouldBe (position, id, updated)
      }
    }
    describe("when asked to produce a modified copy of itself") {
      it("should return a new cell with the same properties as the original, unless specified otherwise") {
        val cell: UpdateWallCell = UpdateWallCell(position, id, updated)
        cell.copy(id = newId) shouldBe UpdateWallCell(position, newId, updated)
        cell.copy(position = newPosition, id = newId) shouldBe UpdateWallCell(newPosition, newId, updated)
      }
    }
    describe("when asked to change the updated property") {
      it("should correctly update the updated property") {
        val cell: UpdateWallCell = UpdateWallCell(position, id, updated = true)
        cell.changeUpdatedProperty(false) shouldBe UpdateWallCell(position, id, false)
      }
    }
    describe("when converted to its corresponding BaseCell") {
      it("should get converted correctly") {
        UpdateWallCell(position, id, updated).toBaseCell shouldBe BaseWallCell(position)
      }
    }
    describe("when converted from its corresponding BaseCell") {
      it("should get converted correctly") {
        BaseWallCell(position).toUpdateCell(id, updated) shouldBe UpdateWallCell(position, id, updated)
      }
    }
  }

  describe("A mover cell") {
    describe("when first created") {
      it("should return the given orientation and position") {
        val orientation: Orientation = Orientation.Right
        val cell: UpdateMoverCell = UpdateMoverCell(position, orientation, id, updated)
        cell.position shouldBe position
        cell.orientation shouldBe orientation
        cell.id shouldBe id
        cell.updated shouldBe updated
      }
    }
    describe("when asked to extract cell properties") {
      it("should return the correct properties") {
        val orientation: Orientation = Orientation.Right
        val cell: UpdateMoverCell = UpdateMoverCell(position, orientation, id, updated)
        UpdateMoverCell.unapply(cell) shouldBe (position, orientation, id, updated)
      }
    }
    describe("when asked to produce a modified copy of itself") {
      it("should return a new cell with the same properties as the original, unless specified otherwise") {
        val orientation: Orientation = Orientation.Right
        val cell: UpdateMoverCell = UpdateMoverCell(position, orientation, id, updated)
        cell.copy(id = newId) shouldBe UpdateMoverCell(position, orientation, newId, updated)
        cell.copy(position = newPosition, id = newId) shouldBe UpdateMoverCell(newPosition, orientation, newId, updated)
        cell.copy(orientation = Orientation.Top) shouldBe UpdateMoverCell(position, Orientation.Top, id, updated)
      }
    }
    describe("when asked to change the updated property") {
      it("should correctly update the updated property") {
        val orientation: Orientation = Orientation.Right
        val cell: UpdateMoverCell = UpdateMoverCell(position, orientation, id, updated = true)
        cell.changeUpdatedProperty(false) shouldBe UpdateMoverCell(position, orientation, id, false)
      }
    }
    describe("when converted to its corresponding BaseCell") {
      it("should get converted correctly") {
        val orientation: Orientation = Orientation.Right
        UpdateMoverCell(position, orientation, id, updated).toBaseCell shouldBe BaseMoverCell(orientation)(position)
      }
    }
    describe("when converted from its corresponding BaseCell") {
      it("should get converted correctly") {
        val orientation: Orientation = Orientation.Right
        BaseMoverCell(orientation)(position).toUpdateCell(id, updated) shouldBe
          UpdateMoverCell(position, orientation, id, updated)
      }
    }
  }

  describe("A generator cell") {
    describe("when first created") {
      it("should return the given orientation and position") {
        val orientation: Orientation = Orientation.Right
        val cell: UpdateGeneratorCell = UpdateGeneratorCell(position, orientation, id, updated)
        cell.position shouldBe position
        cell.orientation shouldBe orientation
        cell.id shouldBe id
        cell.updated shouldBe updated
      }
    }
    describe("when asked to extract cell properties") {
      it("should return the correct properties") {
        val orientation: Orientation = Orientation.Right
        val cell: UpdateGeneratorCell = UpdateGeneratorCell(position, orientation, id, updated)
        UpdateGeneratorCell.unapply(cell) shouldBe (position, orientation, id, updated)
      }
    }
    describe("when asked to produce a modified copy of itself") {
      it("should return a new cell with the same properties as the original, unless specified otherwise") {
        val orientation: Orientation = Orientation.Right
        val cell: UpdateGeneratorCell = UpdateGeneratorCell(position, orientation, id, updated)
        cell.copy(id = newId) shouldBe UpdateGeneratorCell(position, orientation, newId, updated)
        cell.copy(position = newPosition, id = newId) shouldBe UpdateGeneratorCell(newPosition, orientation, newId, updated)
        cell.copy(orientation = Orientation.Top) shouldBe UpdateGeneratorCell(position, Orientation.Top, id, updated)
      }
    }
    describe("when asked to change the updated property") {
      it("should correctly update the updated property") {
        val orientation: Orientation = Orientation.Right
        val cell: UpdateGeneratorCell = UpdateGeneratorCell(position, orientation, id, updated = true)
        cell.changeUpdatedProperty(false) shouldBe UpdateGeneratorCell(position, orientation, id, false)
      }
    }
    describe("when converted to its corresponding BaseCell") {
      it("should get converted correctly") {
        val orientation: Orientation = Orientation.Right
        UpdateGeneratorCell(position, orientation, id, updated).toBaseCell shouldBe BaseMoverCell(orientation)(position)
      }
    }
    describe("when converted from its corresponding BaseCell") {
      it("should get converted correctly") {
        val orientation: Orientation = Orientation.Right
        BaseMoverCell(orientation)(position).toUpdateCell(id, updated) shouldBe
          UpdateGeneratorCell(position, orientation, id, updated)
      }
    }
  }

  describe("A rotation cell") {
    describe("when first created") {
      it("should return the given rotation and position") {
        val rotation: Rotation = Rotation.Clockwise
        val cell: UpdateRotatorCell = UpdateRotatorCell(position, rotation, id, updated)
        cell.position shouldBe position
        cell.rotation shouldBe rotation
        cell.id shouldBe id
        cell.updated shouldBe updated
      }
    }
    describe("when asked to extract cell properties") {
      it("should return the correct properties") {
        val rotation: Rotation = Rotation.Clockwise
        val cell: UpdateRotatorCell = UpdateRotatorCell(position, rotation, id, updated)
        UpdateRotatorCell.unapply(cell) shouldBe (position, rotation, id, updated)
      }
    }
    describe("when asked to produce a modified copy of itself") {
      it("should return a new cell with the same properties as the original, unless specified otherwise") {
        val rotation: Rotation = Rotation.Clockwise
        val cell: UpdateRotatorCell = UpdateRotatorCell(position, rotation, id, updated)
        cell.copy(id = newId) shouldBe UpdateRotatorCell(position, rotation, newId, updated)
        cell.copy(position = newPosition, id = newId) shouldBe UpdateRotatorCell(newPosition, rotation, newId, updated)
        cell.copy(rotation = Rotation.Counterclockwise) shouldBe
          UpdateRotatorCell(position, Rotation.Counterclockwise, id, updated)
      }
    }
    describe("when asked to change the updated property") {
      it("should correctly update the updated property") {
        val rotation: Rotation = Rotation.Clockwise
        val cell: UpdateRotatorCell = UpdateRotatorCell(position, rotation, id, updated = true)
        cell.changeUpdatedProperty(false) shouldBe UpdateRotatorCell(position, rotation, id, false)
      }
    }
    describe("when converted to its corresponding BaseCell") {
      it("should get converted correctly") {
        val rotation: Rotation = Rotation.Clockwise
        UpdateRotatorCell(position, rotation, id, updated).toBaseCell shouldBe BaseRotatorCell(rotation)(position)
      }
    }
    describe("when converted from its corresponding BaseCell") {
      it("should get converted correctly") {
        val rotation: Rotation = Rotation.Clockwise
        BaseRotatorCell(rotation)(position).toUpdateCell(id, updated) shouldBe
          UpdateRotatorCell(position, rotation, id, updated)
      }
    }
  }

  describe("A block cell") {
    describe("when first created") {
      it("should return the given push and position") {
        val push: Push = Push.Vertical
        val cell: UpdateBlockCell = UpdateBlockCell(position, push, id, updated)
        cell.position shouldBe position
        cell.push shouldBe push
        cell.id shouldBe id
        cell.updated shouldBe updated
      }
    }
    describe("when asked to extract cell properties") {
      it("should return the correct properties") {
        val push: Push = Push.Vertical
        val cell: UpdateBlockCell = UpdateBlockCell(position, push, id, updated)
        UpdateBlockCell.unapply(cell) shouldBe (position, push, id, updated)
      }
    }
    describe("when asked to produce a modified copy of itself") {
      it("should return a new cell with the same properties as the original, unless specified otherwise") {
        val push: Push = Push.Vertical
        val cell: UpdateBlockCell = UpdateBlockCell(position, push, id, updated)
        cell.copy(id = newId) shouldBe UpdateBlockCell(position, push, newId, updated)
        cell.copy(position = newPosition, id = newId) shouldBe UpdateBlockCell(newPosition, push, newId, updated)
        cell.copy(push = Push.Both) shouldBe UpdateBlockCell(position, push = Push.Both, id, updated)
      }
    }
    describe("when asked to change the updated property") {
      it("should correctly update the updated property") {
        val push: Push = Push.Vertical
        val cell: UpdateBlockCell = UpdateBlockCell(position, push, id, updated = true)
        cell.changeUpdatedProperty(false) shouldBe UpdateBlockCell(position, push, id, false)
      }
    }
    describe("when converted to its corresponding BaseCell") {
      it("should get converted correctly") {
        val push: Push = Push.Vertical
        UpdateBlockCell(position, push, id, updated).toBaseCell shouldBe BaseBlockCell(push)(position)
      }
    }
    describe("when converted from its corresponding BaseCell") {
      it("should get converted correctly") {
        val push: Push = Push.Vertical
        BaseBlockCell(push)(position).toUpdateCell(id, updated) shouldBe
          UpdateBlockCell(position, push, id, updated)
      }
    }
  }

  describe("A deleter cell") {
    describe("when first created") {
      it("should return the given position") {
        val cell: UpdateDeleterCell = UpdateDeleterCell(position, id, updated)
        cell.position shouldBe position
        cell.id shouldBe id
        cell.updated shouldBe updated
      }
    }
    describe("when asked to extract cell properties") {
      it("should return the correct properties") {
        val cell: UpdateDeleterCell = UpdateDeleterCell(position, id, updated)
        UpdateDeleterCell.unapply(cell) shouldBe (position, id, updated)
      }
    }
    describe("when asked to produce a modified copy of itself") {
      it("should return a new cell with the same properties as the original, unless specified otherwise") {
        val cell: UpdateDeleterCell = UpdateDeleterCell(position, id, updated)
        cell.copy(id = newId) shouldBe UpdateDeleterCell(position, newId, updated)
        cell.copy(position = newPosition, id = newId) shouldBe UpdateDeleterCell(newPosition, newId, updated)
      }
    }
    describe("when asked to change the updated property") {
      it("should correctly update the updated property") {
        val cell: UpdateDeleterCell = UpdateDeleterCell(position, id, updated = true)
        cell.changeUpdatedProperty(false) shouldBe UpdateDeleterCell(position, id, false)
      }
    }
    describe("when converted to its corresponding BaseCell") {
      it("should get converted correctly") {
        UpdateDeleterCell(position, id, updated).toBaseCell shouldBe BaseDeleterCell(position)
      }
    }
    describe("when converted from its corresponding BaseCell") {
      it("should get converted correctly") {
        BaseDeleterCell(position).toUpdateCell(id, updated) shouldBe
          UpdateDeleterCell(position, id, updated)
      }
    }
  }
}

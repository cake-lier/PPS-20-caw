package it.unibo.pps.caw.common.model.cell

import it.unibo.pps.caw.common.model.Position
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/** Tests for the [[BaseCell]] trait and all its subtypes. */
class BaseCellTest extends AnyFunSpec with Matchers {
  private val position: Position = Position(1, 2)
  private val updatedPosition: Position = Position(2, 3)
  private def changePosition(cell: BaseCell): Position = (cell.position.x + 1, cell.position.y + 1)

  describe("An enemy cell") {
    describe("when first created") {
      it("should return the given position") {
        val cell: BaseEnemyCell = BaseEnemyCell(position)
        cell.position shouldBe position
      }
    }
    describe("when asked to extract cell properties") {
      it("should return the correct properties") {
        val cell: BaseEnemyCell = BaseEnemyCell(position)
        BaseEnemyCell.unapply(cell) shouldBe Tuple1(position)
      }
    }
    describe("when asked to change the position property") {
      it("should correctly update the position") {
        val cell: BaseEnemyCell = BaseEnemyCell(position)
        cell.changePositionProperty(changePosition) shouldBe BaseEnemyCell(updatedPosition)
      }
    }
  }

  describe("A wall cell") {
    describe("when first created") {
      it("should return the given position") {
        val cell: BaseWallCell = BaseWallCell(position)
        cell.position shouldBe position
      }
    }
    describe("when asked to extract cell properties") {
      it("should return the correct properties") {
        val cell: BaseWallCell = BaseWallCell(position)
        BaseWallCell.unapply(cell) shouldBe Tuple1(position)
      }
    }
    describe("when asked to change the position property") {
      it("should correctly update the position") {
        val cell: BaseWallCell = BaseWallCell(position)
        cell.changePositionProperty(changePosition) shouldBe BaseWallCell(updatedPosition)
      }
    }
  }

  describe("A mover cell") {
    describe("when first created") {
      it("should return the given orientation and position") {
        val orientation: Orientation = Orientation.Right
        val cell: BaseMoverCell = BaseMoverCell(orientation)(position)
        cell.position shouldBe position
        cell.orientation shouldBe orientation
      }
    }
    describe("when asked to extract cell properties") {
      it("should return the correct properties") {
        val orientation: Orientation = Orientation.Right
        val cell: BaseMoverCell = BaseMoverCell(orientation)(position)
        BaseMoverCell.unapply(cell) shouldBe (orientation, position)
      }
    }
    describe("when asked to change the position property") {
      it("should correctly update the position") {
        val orientation: Orientation = Orientation.Right
        val cell: BaseMoverCell = BaseMoverCell(orientation)(position)
        cell.changePositionProperty(changePosition) shouldBe BaseMoverCell(orientation)(updatedPosition)
      }
    }
  }

  describe("A generator cell") {
    describe("when first created") {
      it("should return the given orientation and position") {
        val orientation: Orientation = Orientation.Right
        val cell: BaseGeneratorCell = BaseGeneratorCell(orientation)(position)
        cell.position shouldBe position
        cell.orientation shouldBe orientation
      }
    }
    describe("when asked to extract cell properties") {
      it("should return the correct properties") {
        val orientation: Orientation = Orientation.Right
        val cell: BaseGeneratorCell = BaseGeneratorCell(orientation)(position)
        BaseGeneratorCell.unapply(cell) shouldBe (orientation, position)
      }
    }
    describe("when asked to change the position property") {
      it("should correctly update the position") {
        val orientation: Orientation = Orientation.Right
        val cell: BaseGeneratorCell = BaseGeneratorCell(orientation)(position)
        cell.changePositionProperty(changePosition) shouldBe BaseGeneratorCell(orientation)(updatedPosition)
      }
    }
  }

  describe("A rotation cell") {
    describe("when first created") {
      it("should return the given rotation and position") {
        val rotation: Rotation = Rotation.Clockwise
        val cell: BaseRotatorCell = BaseRotatorCell(rotation)(position)
        cell.position shouldBe position
        cell.rotation shouldBe rotation
      }
    }
    describe("when asked to extract cell properties") {
      it("should return the correct properties") {
        val rotation: Rotation = Rotation.Clockwise
        val cell: BaseRotatorCell = BaseRotatorCell(rotation)(position)
        BaseRotatorCell.unapply(cell) shouldBe (rotation, position)
      }
    }
    describe("when asked to change the position property") {
      it("should correctly update the position") {
        val rotation: Rotation = Rotation.Clockwise
        val cell: BaseRotatorCell = BaseRotatorCell(rotation)(position)
        cell.changePositionProperty(changePosition) shouldBe BaseRotatorCell(rotation)(updatedPosition)
      }
    }
  }

  describe("A block cell") {
    describe("when first created") {
      it("should return the given push and position") {
        val push: Push = Push.Vertical
        val cell: BaseBlockCell = BaseBlockCell(push)(position)
        cell.position shouldBe position
        cell.push shouldBe push
      }
    }
    describe("when asked to extract cell properties") {
      it("should return the correct properties") {
        val push: Push = Push.Vertical
        val cell: BaseBlockCell = BaseBlockCell(push)(position)
        BaseBlockCell.unapply(cell) shouldBe (push, position)
      }
    }
    describe("when asked to change the position property") {
      it("should correctly update the position") {
        val push: Push = Push.Vertical
        val cell: BaseBlockCell = BaseBlockCell(push)(position)
        cell.changePositionProperty(changePosition) shouldBe BaseBlockCell(push)(updatedPosition)
      }
    }
  }

  describe("A deleter cell") {
    describe("when first created") {
      it("should return the given position") {
        val cell: BaseDeleterCell = BaseDeleterCell(position)
        cell.position shouldBe position
      }
    }
    describe("when asked to extract cell properties") {
      it("should return the correct properties") {
        val cell: BaseDeleterCell = BaseDeleterCell(position)
        BaseDeleterCell.unapply(cell) shouldBe Tuple1(position)
      }
    }
    describe("when asked to change the position property") {
      it("should correctly update the position") {
        val cell: BaseDeleterCell = BaseDeleterCell(position)
        cell.changePositionProperty(changePosition) shouldBe BaseDeleterCell(updatedPosition)
      }
    }
  }
}

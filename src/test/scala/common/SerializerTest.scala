package it.unibo.pps.caw
package common

import common.LevelParser
import common.model.cell.*
import common.model.{Board, Dimensions, Level, PlayableArea}
import common.storage.FileStorage

import io.vertx.core.Vertx
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterAll, Suite}

class SerializerTest extends AnyFunSpec with Matchers with BeforeAndAfterAll { this: Suite =>


  override def afterAll(): Unit = Vertx.vertx().close()

  
}

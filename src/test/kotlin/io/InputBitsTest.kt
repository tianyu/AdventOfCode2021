package io

import assertk.Assert
import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.prop
import kotlin.test.Test

internal class InputBitsTest {
  @Test
  fun testEmpty() = assertThat(inputBitStream()).all {
    readBits(1).isEqualTo(-1)
  }

  @Test
  fun testWithin1Byte() = assertThat(inputBitStream(0b01010011)).all {
    readBits(1).isEqualTo(0b0)
    readBits(1).isEqualTo(0b1)
    readBits(3).isEqualTo(0b010)
    readBits(3).isEqualTo(0b011)
    readBits(1).isEqualTo(-1)
  }

  @Test
  fun testMultiplesOfBytes() = assertThat(inputBitStream(
    0b01010011, // 0
    0b00111010, // 1
    0b00001001, // 2
    0b01101100, // 3
    0b00100011, // 4
    0b11010110, // 5
    0b01101011, // 6
    0b11101111, // 7
    0b01110100, // 8
    0b11101011, // 9
  )).all {
    readBits(8).isEqualTo(0b01010011)
    readBits(32).isEqualTo(0b00111010_00001001_01101100_00100011)
    readBits(16).isEqualTo(0b11010110_01101011)
    readBits(24).isEqualTo(0b11101111_01110100_11101011)
    readBits(8).isEqualTo(-1)
  }

  @Test
  fun testCrossByteBoundary() = assertThat(inputBitStream(
    0b01010011, // 0
    0b00111010, // 1
    0b00001001, // 2
    0b01101100, // 3
  )).all {
    readBits(3).isEqualTo(0b010)
    readBits(8).isEqualTo(0b10011_001)
    readBits(10).isEqualTo(0b11010_00001)
    readBits(12).isEqualTo(0b001_01101100)
  }

  fun inputBitStream(vararg bytes: Int) = bytes
    .map(Int::toByte)
    .toByteArray()
    .inputStream()
    .bits()

  fun Assert<InputBits>.readBits(count: Int): Assert<Int> = prop("readBits($count)") { it.readBits(count) }
}
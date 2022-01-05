package io

import assertk.assertThat
import assertk.assertions.containsExactly
import kotlin.test.Test

internal class HexDecodedInputStreamTest {
  @Test
  fun test() {
    assertThat("0123456789ABCDEF".byteInputStream().hexDecode().readBytes()).containsExactly(
      0x01, 0x23, 0x45, 0x67, 0x89.toByte(), 0xAB.toByte(), 0xCD.toByte(), 0xEF.toByte()
    )
  }
}
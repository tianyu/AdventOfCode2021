package io

import java.io.InputStream

fun InputStream.hexDecode() = HexDecodedInputStream(this)

class HexDecodedInputStream(private val input: InputStream): InputStream() {
  override fun close() = input.close()

  override fun read(): Int {
    val c1 = decodeNextByteOr { return -1 }
    val c2 = decodeNextByteOr { 0 }
    return c1 shl 4 or c2
  }

  private inline fun decodeNextByteOr(default: () -> Int): Int {
    val read = input.read()
    if (read == -1) return default()
    return read.toChar().digitToInt(16)
  }
}
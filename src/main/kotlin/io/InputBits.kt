package io

import java.io.InputStream

fun InputStream.bits(): InputBits = InputBitStream(this)

interface InputBits : AutoCloseable {
  fun readBits(count: Int): Int

  fun take(count: Int): InputBits = FirstBits(count, this)
}

class FirstBits(private val size: Int, private val input: InputBits) : InputBits {
  private var read = 0

  override fun readBits(count: Int): Int {
    if (read >= size) return -1
    val toRead = minOf(count, size - count)
    val bits = input.readBits(toRead)
    read += toRead
    return bits
  }

  override fun close() {
    while (read < size) readBits(32)
  }
}

class InputBitStream(private val input: InputStream) : InputBits {
  var buffer: Int = 0
  var bufferSize: Int = 0

  override fun readBits(count: Int): Int {
    // Check if input has ended
    if (bufferSize < 0) return -1

    var result = 0
    var remainder = count

    while (remainder > Byte.SIZE_BITS) {
      nextBufferOr { return if (remainder == count) -1 else result }
      remainder -= bufferSize
      result = result shl bufferSize or readBufferBits(bufferSize)
    }

    while (remainder > 0) {
      nextBufferOr { return if (remainder == count) -1 else result }
      val readSize = minOf(remainder, bufferSize)
      result = result shl readSize or readBufferBits(readSize)
      remainder -= readSize
    }

    return result
  }

  private fun readBufferBits(count: Int): Int = buffer
    .shr(bufferSize - count)
    .and((1 shl count) - 1)
    .also { bufferSize -= count }

  private inline fun nextBufferOr(eof: () -> Nothing) {
    if (bufferSize == 0) {
      buffer = input.read()
      if (buffer == -1) {
        bufferSize = -1
        eof()
      }
      bufferSize = Byte.SIZE_BITS
    }
  }

  override fun close() = input.close()
}
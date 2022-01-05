import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import io.*

object PacketDecoder : Day {
  override val name: String = "Packet Decoder"

  @Composable
  override fun ColumnScope.content() {
    p("""
      As you leave the cave and reach open waters, you receive a transmission from the Elves back on the ship.

      The transmission was sent using the Buoyancy Interchange Transmission System (BITS), a method of packing numeric expressions into a binary sequence. Your submarine's computer has saved the transmission in hexadecimal (your puzzle input).
      
      The BITS transmission contains a single packet at its outermost layer which itself contains many other packets. The hexadecimal representation of this packet might encode a few extra 0 bits at the end; these are not part of the transmission and should be ignored.

      Every packet begins with a standard header: the first three bits encode the packet version, and the next three bits encode the packet type ID. These two values are numbers; all numbers encoded in any packet are represented as binary with the most significant bit first. For example, a version encoded as the binary sequence 100 represents the number 4.

      Packets with type ID 4 represent a literal value. Literal value packets encode a single binary number. To do this, the binary number is padded with leading zeroes until its length is a multiple of four bits, and then it is broken into groups of four bits. Each group is prefixed by a 1 bit except the last group, which is prefixed by a 0 bit. These groups of five bits immediately follow the packet header.
      
      Every other type of packet (any packet with a type ID other than 4) represent an operator that performs some calculation on one or more sub-packets contained within. Right now, the specific operations aren't important; focus on parsing the hierarchy of sub-packets.

      An operator packet contains one or more packets. To indicate which subsequent binary data represents its sub-packets, an operator packet can use one of two modes indicated by the bit immediately after the packet header; this is called the length type ID:

         - If the length type ID is 0, then the next 15 bits are a number that represents the total length in bits of the sub-packets contained by this packet.
         - If the length type ID is 1, then the next 11 bits are a number that represents the number of sub-packets immediately contained by this packet.

      Finally, after the length type ID bit and the 15-bit or 11-bit field, the sub-packets appear.
    """.trimIndent())

    h4("Input Packet")
    val input by answering {
      inputStream()
        .hexDecode()
        .bits()
        .use { it.readPacket() }
    }

    input {
      fun StructuredWriter.write(packet: Packet) {
        when (packet) {
          is Packet.Literal -> append(packet.value.toString())
          is Packet.Operator -> {
            append(packet::class.simpleName)
            append(" {")
            if (packet.all { it is Packet.Literal }) {
              append(' ')
              join(packet) { write(it) }
              append(' ')
            } else {
              indented {
                append(newlineIndent)
                join(packet, separator = ", $newlineIndent") { write(it) }
                append(newline)
              }
            }
            append('}')
          }
        }
      }
      Answer(buildStructuredString { write(it) })
    }

    h4("Part 1")
    p("""
      For now, parse the hierarchy of the packets throughout the transmission and add up all of the version numbers.
      
      Decode the structure of your hexadecimal-encoded BITS transmission; what do you get if you add up the version numbers in all packets?
    """.trimIndent())

    val part1 by input.transform { packet ->
      fun Packet.totalVersion(): Int = when (this) {
        is Packet.Literal -> version
        is Packet.Operator -> version + sumOf(Packet::totalVersion)
      }

      packet.totalVersion()
    }

    part1.invoke()

    h4("Part 2")
    p("""
      Now that you have the structure of your transmission decoded, you can calculate the value of the expression it represents.

      Literal values (type ID 4) represent a single number as described above. The remaining type IDs are more interesting:

         - Packets with type ID 0 are sum packets - their value is the sum of the values of their sub-packets. If they only have a single sub-packet, their value is the value of the sub-packet.
         - Packets with type ID 1 are product packets - their value is the result of multiplying together the values of their sub-packets. If they only have a single sub-packet, their value is the value of the sub-packet.
         - Packets with type ID 2 are minimum packets - their value is the minimum of the values of their sub-packets.
         - Packets with type ID 3 are maximum packets - their value is the maximum of the values of their sub-packets.
         - Packets with type ID 5 are greater than packets - their value is 1 if the value of the first sub-packet is greater than the value of the second sub-packet; otherwise, their value is 0. These packets always have exactly two sub-packets.
         - Packets with type ID 6 are less than packets - their value is 1 if the value of the first sub-packet is less than the value of the second sub-packet; otherwise, their value is 0. These packets always have exactly two sub-packets.
         - Packets with type ID 7 are equal to packets - their value is 1 if the value of the first sub-packet is equal to the value of the second sub-packet; otherwise, their value is 0. These packets always have exactly two sub-packets.

      Using these rules, you can now work out the value of the outermost packet in your BITS transmission.
    """.trimIndent())

    val part2 by input.transform {
      operator fun Packet.invoke(): Long = when (this) {
        is Packet.Literal -> value
        is Packet.EqualTo -> packets.let { (a, b) -> if (a() == b()) 1L else 0L }
        is Packet.GreaterThan -> packets.let { (a, b) -> if (a() > b()) 1L else 0L }
        is Packet.LessThan -> packets.let { (a, b) -> if (a() < b()) 1L else 0L }
        is Packet.Max -> maxOf { it() }
        is Packet.Min -> minOf { it() }
        is Packet.Product -> fold(1L) { p, it -> p * it() }
        is Packet.Sum -> sumOf { it() }
      }

      it()
    }

    part2.invoke()
  }

  fun InputBits.readPacket(version: Int = readBits(3)): Packet {
    return when (val type = readBits(3)) {
      0 -> Packet.Sum(version, readPackets())
      1 -> Packet.Product(version, readPackets())
      2 -> Packet.Min(version, readPackets())
      3 -> Packet.Max(version, readPackets())
      4 -> Packet.Literal(version, readNumber())
      5 -> Packet.GreaterThan(version, readPackets())
      6 -> Packet.LessThan(version, readPackets())
      7 -> Packet.EqualTo(version, readPackets())
      else -> throw IllegalStateException("Invalid type: $type")
    }
  }

  fun InputBits.readNumber(): Long {
    var value = 0L
    var done = false
    while (!done) {
      done = readBits(1) == 0
      value = value.shl(4).or(readBits(4).toLong())
    }
    return value
  }

  fun InputBits.readPackets(): List<Packet> = when (val lengthType = readBits(1)) {
    0 -> take(readBits(15)).readAllPackets()
    1 -> readPackets(readBits(11))
    else -> throw IllegalStateException("Invalid length type: $lengthType")
  }

  fun InputBits.readAllPackets(): List<Packet> = buildList {
    while (true) {
      val version = readBits(3)
      if (version == -1) break
      add(readPacket(version))
    }
  }

  fun InputBits.readPackets(count: Int): List<Packet> =
    List(count) { readPacket() }

  sealed interface Packet {
    val version: Int
    sealed class Operator(packets: List<Packet>): Packet, List<Packet> by packets
    data class Sum(override val version: Int, val packets: List<Packet>) : Operator(packets)
    data class Product(override val version: Int, val packets: List<Packet>) : Operator(packets)
    data class Min(override val version: Int, val packets: List<Packet>) : Operator(packets)
    data class Max(override val version: Int, val packets: List<Packet>) : Operator(packets)
    data class Literal(override val version: Int, val value: Long) : Packet
    data class GreaterThan(override val version: Int, val packets: List<Packet>) : Operator(packets)
    data class LessThan(override val version: Int, val packets: List<Packet>) : Operator(packets)
    data class EqualTo(override val version: Int, val packets: List<Packet>) : Operator(packets)
  }
}
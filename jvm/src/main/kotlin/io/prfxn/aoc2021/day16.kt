package io.prfxn.aoc2021

private object Day16 {

    data class Packet(
        val version: Int,
        val typeId: Int,

        val lengthTypeId: Char = '0',
        val subPackets: List<Packet> = listOf() ,

        val value: Long = 0,

        val bits: String,
        val start: Int,
        val length: Int
    )

    fun getVersion(bits: String, start: Int) = bits.substring((start + 0) .. (start + 2)).toInt(2)
    fun getTypeId(bits: String, start: Int) = bits.substring((start + 3) .. (start + 5)).toInt(2)
    fun getLengthTypeId(bits: String, start: Int) = bits[start + 6]


    fun readPacket(bits: String, start: Int): Packet =
        when (getTypeId(bits, start)) {
            4 -> readLiteralPacket(bits, start)
            else -> readOperatorPacket(bits, start)
        }


    fun readLiteralPacket(bits: String, start: Int): Packet {

        val groups =
            with (bits.subSequence(start + 6 until bits.length)
                .chunked(5)) {
                    var lastServed = false
                    takeWhile {
                        val shouldContinue = !lastServed
                        lastServed = it[0] == '0'
                        shouldContinue
                    }
                }
                .map { it.substring(1) }

        val length = (6 + groups.size * 5)

        val value = groups.joinToString("").toLong(2)

        return Packet(
            version = getVersion(bits, start),
            typeId = getTypeId(bits, start),
            value = value,
            bits = bits,
            start = start,
            length = length
        )
    }


    fun readOperatorPacket(bits: String, start: Int): Packet =
        when (getLengthTypeId(bits, start)) {
            '0' -> readOperatorPacketByLength(bits, start)
            else -> readOperatorPacketByCount(bits, start)
        }


    fun readOperatorPacketByLength(bits: String, start: Int): Packet {

        val totalLength = bits.substring(start + 7).take(15).toInt(2)

        val subPackets =
            iterator {
                var bitsRead = 0
                while (bitsRead < totalLength){
                    val packet = readPacket(bits, start + bitsRead + 7 + 15)
                    yield(packet)
                    bitsRead += packet.length
                }
            }.asSequence().toList()

        return Packet(
            version = getVersion(bits, start),
            typeId = getTypeId(bits, start),

            lengthTypeId = getLengthTypeId(bits, start),
            subPackets = subPackets,

            bits = bits,
            start = start,
            length = 7 + 15 + totalLength
        )
    }


    fun readOperatorPacketByCount(bits: String, start: Int): Packet {

        val count = bits.substring(start + 7).take(11).toInt(2)

        val subPackets =
            iterator {
                var numRead = 0
                var s = start
                while (numRead < count){
                    val packet = readPacket(bits, s + 7 + 11)
                    yield(packet)
                    numRead += 1
                    s += packet.length
                }
            }.asSequence().toList()

        return Packet(
            version = getVersion(bits, start),
            typeId = getTypeId(bits, start),

            lengthTypeId = getLengthTypeId(bits, start),
            subPackets = subPackets,

            bits = bits,
            start = start,
            length = 7 + 11 + subPackets.sumOf { it.length }
        )
    }


    val hexToNbl = mapOf(
        "0" to "0000", "1" to "0001", "2" to "0010", "3" to "0011", "4" to "0100",
        "5" to "0101", "6" to "0110", "7" to "0111", "8" to "1000", "9" to "1001",
        "A" to "1010", "B" to "1011", "C" to "1100", "D" to "1101", "E" to "1110",
        "F" to "1111")

    fun getBitString(hex: String) =
        hex.map { hexToNbl[it.toString()]!! }.joinToString("")
}


fun main() {
    val hex = textResourceReader("input/16.txt").readText().trim()

    Day16.apply {

        val rootPacket = readPacket(getBitString(hex), 0);

        fun getSumOfVersions(packet: Day16.Packet): Int =
            packet.version + packet.subPackets.sumOf(::getSumOfVersions)

        println(getSumOfVersions(rootPacket))


        fun getPacketValue(packet: Day16.Packet): Long =
            when (packet.typeId) {
                0 -> packet.subPackets.sumOf(::getPacketValue)
                1 -> packet.subPackets.map(::getPacketValue).reduce { p, v -> p * v }
                2 -> packet.subPackets.minOf(::getPacketValue)
                3 -> packet.subPackets.maxOf(::getPacketValue)
                4 -> packet.value
                5 -> packet.subPackets.map(::getPacketValue).let { (first, second) -> if (first > second) 1 else 0 }
                6 -> packet.subPackets.map(::getPacketValue).let { (first, second) -> if (first < second) 1 else 0 }
                7 -> packet.subPackets.map(::getPacketValue).let { (first, second) -> if (first == second) 1 else 0 }
                else -> fail()
            }

        println(getPacketValue(rootPacket))
    }
}

/** output
993
144595909277
*/

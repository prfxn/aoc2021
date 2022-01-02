package io.prfxn.aoc2021

import java.io.Reader
import java.lang.RuntimeException
import java.nio.charset.Charset
import java.util.PriorityQueue
import kotlin.math.ceil

fun textResourceReader(name: String, charset: Charset = Charsets.UTF_8): Reader =
    requireNotNull(
            ::textResourceReader.javaClass.classLoader.getResourceAsStream(name))
        { "Resource not found: $name" }
        .reader(charset)

fun fail(msg: String = ""): Nothing =
    throw RuntimeException(msg)

fun <T> List<T>.permutations(): List<List<T>> = permutations(size)

fun <T> List<T>.permutations(r: Int): List<List<T>> =
    if (r <= size) {
        generateSequence((0 until r).toMutableList()) { indices ->
            val (ptr, nextIdx) =
                (r - 1 downTo 0)
                    .map { ptr ->
                        ptr to
                                (indices[ptr] + 1 until size)
                                    .find { it !in indices.asSequence().take(ptr) }
                    }
                    .find { (_, nextIdx) -> nextIdx != null }
                    ?: return@generateSequence null

            (sequenceOf(nextIdx!!) +
                    this.indices.asSequence().filter { it !in indices.asSequence().take(ptr + 1) })
                .take(r - ptr)
                .forEachIndexed { i, idx ->
                    indices[ptr + i] = idx
                }

            indices
        }
            .map { indices -> indices.map(::get).toList() }
            .toList()
    } else {
        emptyList()
    }

typealias CP = Pair<Int, Int>
val CP.row get() = first
val CP.col get() = second


class PriorityMap<K, V> private constructor(private val pq: PriorityQueue<Pair<K, V>>,
                                            private val map: MutableMap<K, V>): MutableMap<K, V> by map {

    constructor(): this(PriorityQueue(), mutableMapOf())

    constructor(elements: Sequence<Pair<K, V>> = sequenceOf(), comparator: java.util.Comparator<V>? = null):
            this(
                PriorityQueue<Pair<K, V>>(
                    comparator?.let { valueComparator ->
                        Comparator { (_, v1), (_, v2) -> valueComparator.compare(v1, v2) }
                    }
                ).apply { addAll(elements) },
                mutableMapOf<K, V>().apply { putAll(elements) }
            )

    constructor(initialCapacity: Int = 11, loadFactor: Float = 0.75f, comparator: java.util.Comparator<V>? = null):
            this(
                PriorityQueue<Pair<K, V>>(
                    initialCapacity,
                    comparator?.let { valueComparator ->
                        Comparator { (_, v1), (_, v2) -> valueComparator.compare(v1, v2) }
                    }
                ),
                LinkedHashMap(initialCapacity, loadFactor)
            )

    override fun clear() {
        pq.clear()
        map.clear()
    }

    override fun put(key: K, value: V): V? =
        pq.add(key to value).let { map.put(key, value) }

    override fun putAll(from: Map<out K, V>) {
        from.forEach { (k, v) -> put(k, v) }
    }

    fun extract(): Pair<K, V> =
        generateSequence(pq.remove()) { (k, v) ->
            if (map[k] === v) {
                remove(k)
                null
            }
            else pq.remove()
        }.last()
}

fun Float.toCeilInt(): Int = ceil(this).toInt()

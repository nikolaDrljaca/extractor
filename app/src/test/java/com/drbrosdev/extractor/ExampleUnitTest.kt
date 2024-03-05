package com.drbrosdev.extractor

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.random.Random

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun `async process of list`() {

        val items = listOf(1, 2, 3, 4, 5)

        runBlocking {
            val result = items.asFlow()
                .map { networkCall(it) }
                .filter { it % 2 == 0 }
                .distinctUntilChanged()
                .toList()

            println(result)
        }
    }

    private suspend fun networkCall(param: Int): Int {
        delay(500)
        return param * Random.nextInt(until = 10)
    }

    @Test
    fun `scoped service`() = runBlocking {
        val service: FooService = FooServiceImpl()
        val current = Number(10)
        with(service) {
            val updated = current.update(12)
            println(updated)
        }

        Unit
    }

    @Test
    fun `visual embed deletion`() = runBlocking {
        val visualEmbeds = "foo,bar,baz,foobar"

        val out = visualEmbeds
            .split(",")
            .map { it.trim() }
            .filter { it.lowercase() != "baz".lowercase() }
            .joinToString(separator = ",") { it }
        println(out)
        assert(out == "foo,bar,foobar")
    }

    @Test
    fun `test build full match query`() = runBlocking {
        val adaptedQuery = "*lake*"
        val output = buildString {
            append("textIndex:$adaptedQuery")
            append(" OR ")
            append("visualIndex:$adaptedQuery")
            append(" OR ")
            append("userIndex:$adaptedQuery")
            append(" OR ")
            append("colorIndex:$adaptedQuery")
        }

        println(output)
    }
}


@JvmInline
value class Number(val value: Int)

interface FooService {

    suspend fun Number.update(newValue: Int): Number
}

class FooServiceImpl: FooService {

    override suspend fun Number.update(newValue: Int): Number {
        println("called update with the new value $newValue against old ${this.value}")
        return Number(newValue)
    }
}


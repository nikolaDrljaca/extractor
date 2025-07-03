package com.drbrosdev.extractor.usecase

import com.drbrosdev.extractor.domain.usecase.token.TokenizeText
import com.drbrosdev.extractor.util.memo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Test

class TokenizeTextTest {

    private val tokenizeText = TokenizeText(Dispatchers.Default)

    @Test
    fun `test memo function`() {
        val computation: (String) -> Unit = memo { param ->
            println("running with $param")
        }
        computation("1")
        computation("2")
        computation("1")
    }

    @Test
    fun `test tokenization of sentences`() = runTest {
        val input = """
            together with materialistic by hidroh which I've used.
            I still use the app and read all the emails but don't have --
        """.trimIndent()

        val output = tokenizeText.invoke(input)
            .map { it.text }
            .toList()

        println(output)
        println("Output size: ${output.size}")
    }

    @Test
    fun `special characters are skipped`() = runTest {
        val input = """
            ### --- $$ ^ &# !*@
        """.trimIndent()

        val output = tokenizeText.invoke(input)
            .map { it.text }
            .toList()

        assert(output.isEmpty())
        println(output)
        println("Output size: ${output.size}")
    }
}

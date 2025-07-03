package com.drbrosdev.extractor.usecase

import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.search.SearchType
import com.drbrosdev.extractor.domain.model.Token
import com.drbrosdev.extractor.domain.usecase.search.BuildFtsQuery
import org.junit.Test

class CreateFtsMatchQueryTest {

    private val buildFtsQuery = BuildFtsQuery()

    @Test
    fun `does create expected match query - FULL ALL`() {
        val multiWord = BuildFtsQuery.Params(
            tokens = listOf(Token("grey"), Token("car"), Token("goes")),
            keywordType = KeywordType.ALL,
            searchType = SearchType.FULL
        )
        val singleWord = multiWord.copy(
            tokens = listOf(Token("grey")),
        )

        val singleOut = buildFtsQuery(singleWord)
        val expectedSingle = "grey"
        assert(singleOut.value == expectedSingle) {
            "Expected result { $expectedSingle }, got { ${singleOut.value} }"
        }

        val multiOut = buildFtsQuery(multiWord)
        val expectedMulti = "grey car goes"
        assert(multiOut.value == expectedMulti) {
            "Expected result { $expectedMulti }, got { ${multiOut.value} }"
        }
    }

    @Test
    fun `does create expected match query - PARTIAL ALL`() {
        val multiWord = BuildFtsQuery.Params(
            tokens = listOf(Token("grey"), Token("car"), Token("goes")),
            keywordType = KeywordType.ALL,
            searchType = SearchType.PARTIAL
        )
        val singleWord = multiWord.copy(
            tokens = listOf(Token("grey"))
        )

        val singleOut = buildFtsQuery(singleWord)
        val expectedSingle = "*grey*"
        assert(singleOut.value == expectedSingle) {
            "Expected result { $expectedSingle }, got { ${singleOut.value} }"
        }

        val multiOut = buildFtsQuery(multiWord)
        val expectedMulti = "*grey* OR *car* OR *goes*"
        assert(multiOut.value == expectedMulti) {
            "Expected result { $expectedMulti }, got { ${multiOut.value} }"
        }
    }

    @Test
    fun `does create expected match query - FULL TEXT`() {
        val multiWord = BuildFtsQuery.Params(
            tokens = listOf(Token("grey"), Token("car"), Token("goes")),
            keywordType = KeywordType.TEXT,
            searchType = SearchType.FULL
        )
        val singleWord = multiWord.copy(
            tokens = listOf(Token("grey")),
        )

        val singleOut = buildFtsQuery(singleWord)
        val expectedSingle = "text_index:grey"
        assert(singleOut.value == expectedSingle) {
            "Expected result { $expectedSingle }, got { ${singleOut.value} }"
        }

        val multiOut = buildFtsQuery(multiWord)
        val expectedMulti = "text_index:grey text_index:car text_index:goes"
        assert(multiOut.value == expectedMulti) {
            "Expected result { $expectedMulti }, got { ${multiOut.value} }"
        }
    }

    @Test
    fun `does create expected match query - PARTIAL TEXT`() {
        val multiWord = BuildFtsQuery.Params(
            tokens = listOf(Token("grey"), Token("car"), Token("goes")),
            keywordType = KeywordType.TEXT,
            searchType = SearchType.PARTIAL
        )
        val singleWord = multiWord.copy(
            tokens = listOf(Token("grey")),
        )

        val singleOut = buildFtsQuery(singleWord)
        val expectedSingle = "text_index:*grey*"
        assert(singleOut.value == expectedSingle) {
            "Expected result { $expectedSingle }, got { ${singleOut.value} }"
        }

        val multiOut = buildFtsQuery(multiWord)
        val expectedMulti = "text_index:*grey* OR text_index:*car* OR text_index:*goes*"
        assert(multiOut.value == expectedMulti) {
            "Expected result { $expectedMulti }, got { ${multiOut.value} }"
        }
    }

    @Test
    fun `does create expected match query - FULL IMAGE`() {
        val multiWord = BuildFtsQuery.Params(
            tokens = listOf(Token("grey"), Token("car"), Token("goes")),
            keywordType = KeywordType.IMAGE,
            searchType = SearchType.FULL
        )
        val singleWord = multiWord.copy(
            tokens = listOf(Token("grey")),
        )

        val singleOut = buildFtsQuery(singleWord)
        val expectedSingle = "visual_index:grey"
        assert(singleOut.value == expectedSingle) {
            "Expected result { $expectedSingle }, got { ${singleOut.value} }"
        }

        val multiOut = buildFtsQuery(multiWord)
        val expectedMulti = "visual_index:grey visual_index:car visual_index:goes"
        assert(multiOut.value == expectedMulti) {
            "Expected result { $expectedMulti }, got { ${multiOut.value} }"
        }
    }

    @Test
    fun `does create expected match query - PARTIAL IMAGE`() {
        val multiWord = BuildFtsQuery.Params(
            tokens = listOf(Token("grey"), Token("car"), Token("goes")),
            keywordType = KeywordType.IMAGE,
            searchType = SearchType.PARTIAL
        )
        val singleWord = multiWord.copy(
            tokens = listOf(Token("grey")),
        )

        val singleOut = buildFtsQuery(singleWord)
        val expectedSingle = "visual_index:*grey*"
        assert(singleOut.value == expectedSingle) {
            "Expected result { $expectedSingle }, got { ${singleOut.value} }"
        }

        val multiOut = buildFtsQuery(multiWord)
        val expectedMulti = "visual_index:*grey* OR visual_index:*car* OR visual_index:*goes*"
        assert(multiOut.value == expectedMulti) {
            "Expected result { $expectedMulti }, got { ${multiOut.value} }"
        }
    }
}
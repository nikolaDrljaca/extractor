package com.drbrosdev.extractor.usecase

import com.drbrosdev.extractor.domain.model.KeywordType
import com.drbrosdev.extractor.domain.model.SearchType
import com.drbrosdev.extractor.domain.model.Token
import com.drbrosdev.extractor.domain.usecase.CreateAdaptedQuery
import org.junit.Test

class CreateFtsMatchQueryTest {

    private val createAdaptedQuery = CreateAdaptedQuery()


    @Test
    fun `does create expected match query - FULL ALL`() {
        val multiWord = CreateAdaptedQuery.Params(
            tokens = listOf(Token("grey"), Token("car"), Token("goes")),
            keywordType = KeywordType.ALL,
            searchType = SearchType.FULL
        )
        val singleWord = multiWord.copy(
            tokens = listOf(Token("grey")),
        )

        val singleOut = createAdaptedQuery(singleWord)
        val expectedSingle = "grey"
        assert(singleOut.query == expectedSingle) {
            "Expected result { $expectedSingle }, got { ${singleOut.query} }"
        }

        val multiOut = createAdaptedQuery(multiWord)
        val expectedMulti = "grey car goes"
        assert(multiOut.query == expectedMulti) {
            "Expected result { $expectedMulti }, got { ${multiOut.query} }"
        }
    }

    @Test
    fun `does create expected match query - PARTIAL ALL`() {
        val multiWord = CreateAdaptedQuery.Params(
            tokens = listOf(Token("grey"), Token("car"), Token("goes")),
            keywordType = KeywordType.ALL,
            searchType = SearchType.PARTIAL
        )
        val singleWord = multiWord.copy(
            tokens = listOf(Token("grey"))
        )

        val singleOut = createAdaptedQuery(singleWord)
        val expectedSingle = "*grey*"
        assert(singleOut.query == expectedSingle) {
            "Expected result { $expectedSingle }, got { ${singleOut.query} }"
        }

        val multiOut = createAdaptedQuery(multiWord)
        val expectedMulti = "*grey* OR *car* OR *goes*"
        assert(multiOut.query == expectedMulti) {
            "Expected result { $expectedMulti }, got { ${multiOut.query} }"
        }
    }

    @Test
    fun `does create expected match query - FULL TEXT`() {
        val multiWord = CreateAdaptedQuery.Params(
            tokens = listOf(Token("grey"), Token("car"), Token("goes")),
            keywordType = KeywordType.TEXT,
            searchType = SearchType.FULL
        )
        val singleWord = multiWord.copy(
            tokens = listOf(Token("grey")),
        )

        val singleOut = createAdaptedQuery(singleWord)
        val expectedSingle = "textIndex:grey"
        assert(singleOut.query == expectedSingle) {
            "Expected result { $expectedSingle }, got { ${singleOut.query} }"
        }

        val multiOut = createAdaptedQuery(multiWord)
        val expectedMulti = "textIndex:grey textIndex:car textIndex:goes"
        assert(multiOut.query == expectedMulti) {
            "Expected result { $expectedMulti }, got { ${multiOut.query} }"
        }
    }

    @Test
    fun `does create expected match query - PARTIAL TEXT`() {
        val multiWord = CreateAdaptedQuery.Params(
            tokens = listOf(Token("grey"), Token("car"), Token("goes")),
            keywordType = KeywordType.TEXT,
            searchType = SearchType.PARTIAL
        )
        val singleWord = multiWord.copy(
            tokens = listOf(Token("grey")),
        )

        val singleOut = createAdaptedQuery(singleWord)
        val expectedSingle = "textIndex:*grey*"
        assert(singleOut.query == expectedSingle) {
            "Expected result { $expectedSingle }, got { ${singleOut.query} }"
        }

        val multiOut = createAdaptedQuery(multiWord)
        val expectedMulti = "textIndex:*grey* OR textIndex:*car* OR textIndex:*goes*"
        assert(multiOut.query == expectedMulti) {
            "Expected result { $expectedMulti }, got { ${multiOut.query} }"
        }
    }

    @Test
    fun `does create expected match query - FULL IMAGE`() {
        val multiWord = CreateAdaptedQuery.Params(
            tokens = listOf(Token("grey"), Token("car"), Token("goes")),
            keywordType = KeywordType.IMAGE,
            searchType = SearchType.FULL
        )
        val singleWord = multiWord.copy(
            tokens = listOf(Token("grey")),
        )

        val singleOut = createAdaptedQuery(singleWord)
        val expectedSingle = "visualIndex:grey"
        assert(singleOut.query == expectedSingle) {
            "Expected result { $expectedSingle }, got { ${singleOut.query} }"
        }

        val multiOut = createAdaptedQuery(multiWord)
        val expectedMulti = "visualIndex:grey visualIndex:car visualIndex:goes"
        assert(multiOut.query == expectedMulti) {
            "Expected result { $expectedMulti }, got { ${multiOut.query} }"
        }
    }

    @Test
    fun `does create expected match query - PARTIAL IMAGE`() {
        val multiWord = CreateAdaptedQuery.Params(
            tokens = listOf(Token("grey"), Token("car"), Token("goes")),
            keywordType = KeywordType.IMAGE,
            searchType = SearchType.PARTIAL
        )
        val singleWord = multiWord.copy(
            tokens = listOf(Token("grey")),
        )

        val singleOut = createAdaptedQuery(singleWord)
        val expectedSingle = "visualIndex:*grey*"
        assert(singleOut.query == expectedSingle) {
            "Expected result { $expectedSingle }, got { ${singleOut.query} }"
        }

        val multiOut = createAdaptedQuery(multiWord)
        val expectedMulti = "visualIndex:*grey* OR visualIndex:*car* OR visualIndex:*goes*"
        assert(multiOut.query == expectedMulti) {
            "Expected result { $expectedMulti }, got { ${multiOut.query} }"
        }
    }
}
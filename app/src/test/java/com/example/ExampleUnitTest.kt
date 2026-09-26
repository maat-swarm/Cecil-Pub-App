package com.example

import com.example.ui.components.CurrencyFormatter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun randCurrencyFormatting_matchesSouthAfricanFormat() {
        val formatted = CurrencyFormatter.formatRand(1234.50)
        assertEquals("R 1 234,50", formatted)
        assertFalse("Currency must never contain dollar sign", formatted.contains("$"))
        assertTrue("Currency must start with R", formatted.startsWith("R "))
    }

    @Test
    fun randCurrencyFormatting_zeroAndCents() {
        val formattedZero = CurrencyFormatter.formatRand(0.0)
        assertEquals("R 0,00", formattedZero)

        val formattedLarge = CurrencyFormatter.formatRand(15800.75)
        assertEquals("R 15 800,75", formattedLarge)
    }

    @Test
    fun casesAndLooseFormatting() {
        assertEquals("2 cases + 7", CurrencyFormatter.formatCasesAndLoose(2, 7))
        assertEquals("3 cases", CurrencyFormatter.formatCasesAndLoose(3, 0))
        assertEquals("5 bottles", CurrencyFormatter.formatCasesAndLoose(0, 5))
        assertEquals("0 cases", CurrencyFormatter.formatCasesAndLoose(0, 0))
    }
}

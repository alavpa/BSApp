package com.alavpa.bsproducts.data.local

import android.content.SharedPreferences
import com.google.gson.Gson
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class LocalDataSourceTest {

    private lateinit var localDataSource: LocalDataSource
    private val sharedPreferences: SharedPreferences = mockk()

    @Before
    fun setup() {
        localDataSource = PreferencesDataSource(sharedPreferences, Gson())
    }

    @Test
    fun `on like`() {
        val editor: SharedPreferences.Editor = mockk(relaxed = true)
        every { sharedPreferences.getString(any(), any()) } returns "[1,2]"
        every { sharedPreferences.edit() } returns editor

        localDataSource.like(3)
        verify { editor.putString("KEY_LIKES", "[1,2,3]") }
    }

    @Test
    fun `on dislike`() {
        val editor: SharedPreferences.Editor = mockk(relaxed = true)
        every { sharedPreferences.getString(any(), any()) } returns "[1,2]"
        every { sharedPreferences.edit() } returns editor

        localDataSource.dislike(2)
        verify { editor.putString("KEY_LIKES", "[1]") }
    }

    @Test
    fun `get likes`() {
        every { sharedPreferences.getString(any(), any()) } returns "[1,2]"

        localDataSource.likes()
        verify { sharedPreferences.getString("KEY_LIKES", "[]") }
    }
}

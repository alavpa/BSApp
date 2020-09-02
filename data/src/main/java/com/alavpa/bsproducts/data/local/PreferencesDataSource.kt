package com.alavpa.bsproducts.data.local

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PreferencesDataSource(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : LocalDataSource {
    companion object {
        private const val KEY_LIKES = "KEY_LIKES"
    }

    override fun like(id: Long) {

        val strLikes = sharedPreferences.getString(KEY_LIKES, "[]") ?: "[]"

        val list = fromJson<MutableList<Long>>(strLikes)
        if (!list.contains(id)) {
            list.add(id)
            sharedPreferences.edit().putString(KEY_LIKES, toJson(list)).apply()
        }
    }

    override fun dislike(id: Long) {
        val strLikes = sharedPreferences.getString(KEY_LIKES, "[]") ?: "[]"

        val list = fromJson<MutableList<Long>>(strLikes)
        if (list.contains(id)) {
            list.remove(id)
            sharedPreferences.edit().putString(KEY_LIKES, toJson(list)).apply()
        }
    }

    override fun likes(): List<Long> {
        val strLikes = sharedPreferences.getString(KEY_LIKES, "[]") ?: "[]"
        return fromJson<MutableList<Long>>(strLikes)
    }

    private inline fun <reified T> fromJson(json: String): T {
        return gson.fromJson(json, object : TypeToken<T>() {}.type)
    }

    private inline fun <reified T> toJson(json: T): String {
        return gson.toJson(json)
    }
}

package com.example.iossearch.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.dataStore by preferencesDataStore(name = "ios_search_prefs")
private val RECENTS_KEY = stringPreferencesKey("recent_searches")
private const val MAX_RECENTS = 12

/**
 * Persists recent searches across app restarts, mirroring iOS Search's
 * "Recents" section which survives relaunches until manually cleared.
 */
class RecentSearchStore(private val context: Context) {

    val recentSearches: Flow<List<RecentSearch>> = context.dataStore.data.map { prefs ->
        val raw = prefs[RECENTS_KEY] ?: return@map emptyList()
        parseJson(raw)
    }

    suspend fun add(query: String) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return
        context.dataStore.edit { prefs ->
            val current = parseJson(prefs[RECENTS_KEY] ?: "[]").toMutableList()
            current.removeAll { it.query.equals(trimmed, ignoreCase = true) }
            current.add(0, RecentSearch(System.currentTimeMillis(), trimmed, System.currentTimeMillis()))
            val trimmedList = current.take(MAX_RECENTS)
            prefs[RECENTS_KEY] = toJson(trimmedList)
        }
    }

    suspend fun remove(id: Long) {
        context.dataStore.edit { prefs ->
            val current = parseJson(prefs[RECENTS_KEY] ?: "[]").toMutableList()
            current.removeAll { it.id == id }
            prefs[RECENTS_KEY] = toJson(current)
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { prefs ->
            prefs[RECENTS_KEY] = "[]"
        }
    }

    private fun parseJson(raw: String): List<RecentSearch> {
        return try {
            val arr = JSONArray(raw)
            (0 until arr.length()).map { i ->
                val obj = arr.getJSONObject(i)
                RecentSearch(
                    id = obj.getLong("id"),
                    query = obj.getString("query"),
                    timestamp = obj.getLong("timestamp")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun toJson(list: List<RecentSearch>): String {
        val arr = JSONArray()
        list.forEach {
            val obj = JSONObject()
            obj.put("id", it.id)
            obj.put("query", it.query)
            obj.put("timestamp", it.timestamp)
            arr.put(obj)
        }
        return arr.toString()
    }
}

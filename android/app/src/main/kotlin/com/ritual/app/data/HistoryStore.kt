package com.ritual.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ritual.app.domain.DayRecord
import com.ritual.app.domain.TaskSnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate

private val Context.historyDataStore by preferencesDataStore(name = "ritual_history")

/** Persists one completion snapshot per calendar day so streaks and the dashboard survive closing the app. */
class HistoryStore(private val context: Context) {
    private val key = stringPreferencesKey("history_json")

    val historyFlow: Flow<Map<LocalDate, DayRecord>> = context.historyDataStore.data.map { prefs ->
        prefs[key]?.let(::decode) ?: emptyMap()
    }

    /** Upserts a single day's record, merging with whatever else is already stored. */
    suspend fun saveDay(record: DayRecord) {
        context.historyDataStore.edit { prefs ->
            val current = prefs[key]?.let(::decode) ?: emptyMap()
            prefs[key] = encode(current + (record.date to record))
        }
    }

    private fun encode(history: Map<LocalDate, DayRecord>): String {
        val array = JSONArray()
        history.values.forEach { r ->
            val tasksArray = JSONArray()
            r.tasks.forEach { t ->
                tasksArray.put(
                    JSONObject().apply {
                        put("id", t.id)
                        put("name", t.name)
                        put("done", t.done)
                    }
                )
            }
            array.put(
                JSONObject().apply {
                    put("date", r.date.toString())
                    put("tasks", tasksArray)
                }
            )
        }
        return array.toString()
    }

    private fun decode(json: String): Map<LocalDate, DayRecord> = try {
        val array = JSONArray(json)
        (0 until array.length()).associate { i ->
            val obj = array.getJSONObject(i)
            val date = LocalDate.parse(obj.getString("date"))
            val tasksArray = obj.optJSONArray("tasks") ?: JSONArray()
            val tasks = (0 until tasksArray.length()).map { j ->
                val t = tasksArray.getJSONObject(j)
                TaskSnapshot(id = t.getLong("id"), name = t.getString("name"), done = t.getBoolean("done"))
            }
            date to DayRecord(date, tasks)
        }
    } catch (e: org.json.JSONException) {
        emptyMap()
    }
}

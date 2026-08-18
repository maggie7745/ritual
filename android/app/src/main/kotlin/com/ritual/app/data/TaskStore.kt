package com.ritual.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ritual.app.domain.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate

private val Context.dataStore by preferencesDataStore(name = "ritual_prefs")

/** Persists the task list (and last-opened date, for daily rollover) so habits survive closing the app. */
class TaskStore(private val context: Context) {
    private val tasksKey = stringPreferencesKey("tasks_json")
    private val lastOpenedDateKey = stringPreferencesKey("last_opened_date")

    val tasksFlow: Flow<List<Task>> = context.dataStore.data.map { prefs ->
        prefs[tasksKey]?.let(::decode) ?: emptyList()
    }

    /** The calendar date the app last finished a rollover check on; null on first-ever launch. */
    val lastOpenedDateFlow: Flow<LocalDate?> = context.dataStore.data.map { prefs ->
        prefs[lastOpenedDateKey]?.let(LocalDate::parse)
    }

    suspend fun saveTasks(tasks: List<Task>) {
        context.dataStore.edit { prefs -> prefs[tasksKey] = encode(tasks) }
    }

    suspend fun saveLastOpenedDate(date: LocalDate) {
        context.dataStore.edit { prefs -> prefs[lastOpenedDateKey] = date.toString() }
    }

    private fun encode(tasks: List<Task>): String {
        val array = JSONArray()
        tasks.forEach { task ->
            array.put(
                JSONObject().apply {
                    put("id", task.id)
                    put("name", task.name)
                    put("meta", task.meta)
                    put("done", task.done)
                    put("daily", task.daily)
                }
            )
        }
        return array.toString()
    }

    private fun decode(json: String): List<Task> = try {
        val array = JSONArray(json)
        (0 until array.length()).map { i ->
            val obj = array.getJSONObject(i)
            Task(
                id = obj.getLong("id"),
                name = obj.getString("name"),
                meta = obj.getString("meta"),
                done = obj.getBoolean("done"),
                // Older saved data predates this field — fall back to the "Daily" label it already has.
                daily = if (obj.has("daily")) obj.getBoolean("daily") else obj.optString("meta") != "Once",
            )
        }
    } catch (e: org.json.JSONException) {
        emptyList()
    }
}

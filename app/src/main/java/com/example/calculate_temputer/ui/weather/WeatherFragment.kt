package com.example.calculate_temputer.ui.weather

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.calculate_temputer.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.concurrent.thread
import kotlin.random.Random

class WeatherFragment : Fragment() {

    private val mainHandler = Handler(Looper.getMainLooper())
    private companion object {
        private const val TAG = "WeatherFragment"
    }

    private lateinit var cityChip: Chip
    private lateinit var statusText: TextView
    private lateinit var tempText: TextView
    private lateinit var conditionText: TextView
    private lateinit var windText: TextView
    private lateinit var humidityText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var cityInput: TextInputEditText
    private lateinit var searchButton: MaterialButton
    private lateinit var cityListButton: MaterialButton
    private lateinit var historyGroup: ChipGroup
    private lateinit var historyEmpty: TextView
    private lateinit var historyStore: WeatherHistoryStore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_weather, container, false)
        historyStore = WeatherHistoryStore(requireContext())
        bindViews(view)
        bindActions()
        resetWeatherCard()
        renderHistory()
        return view
    }

    private fun bindViews(root: View) {
        cityChip = root.findViewById(R.id.weather_chip_current_city)
        statusText = root.findViewById(R.id.weather_status)
        tempText = root.findViewById(R.id.weather_temp)
        conditionText = root.findViewById(R.id.weather_condition)
        windText = root.findViewById(R.id.weather_wind)
        humidityText = root.findViewById(R.id.weather_humidity)
        progressBar = root.findViewById(R.id.weather_progress)
        cityInput = root.findViewById(R.id.weather_city_input)
        searchButton = root.findViewById(R.id.weather_search_button)
        cityListButton = root.findViewById(R.id.weather_city_list_button)
        historyGroup = root.findViewById(R.id.weather_history_group)
        historyEmpty = root.findViewById(R.id.weather_history_empty)
    }

    private fun bindActions() {
        searchButton.setOnClickListener {
            val query = cityInput.text?.toString()?.trim().orEmpty()
            if (query.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.weather_empty_input), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            fetchWeather(query)
        }

        cityListButton.setOnClickListener { showCityListDialog() }
    }

    private fun fetchWeather(cityQuery: String) {
        setLoadingState(cityQuery)
        logDebug("start fetch (mock) city=$cityQuery")

        thread {
            try {
                // 使用本地模拟数据，避免调用商用接口；可在 MockWeatherProvider 内调整
                val weather = MockWeatherProvider.now(cityQuery)
                mainHandler.post {
                    bindWeather(weather)
                    historyStore.record(weather.city)
                    renderHistory()
                }
            } catch (e: Exception) {
                logDebug("error (mock): ${e.message}")
                mainHandler.post { showError(e.message ?: "请求失败") }
            }
        }
    }

    private fun setLoadingState(city: String) {
        statusText.text = getString(R.string.weather_status_loading, city)
        progressBar.visibility = View.VISIBLE
        tempText.text = "--"
        conditionText.text = ""
        windText.text = ""
        humidityText.text = ""
        cityChip.text = city
    }

    private fun bindWeather(info: WeatherInfo) {
        if (!isAdded) return
        progressBar.visibility = View.GONE
        statusText.text = getString(R.string.weather_status_ready)
        cityChip.text = info.city
        tempText.text = getString(R.string.weather_temp_display, info.temperature)
        conditionText.text = getString(R.string.weather_condition_display, info.condition, info.feelsLike)
        windText.text = getString(R.string.weather_wind_display, info.windDir, info.windScale)
        humidityText.text = getString(R.string.weather_humidity_display, info.humidity)
    }

    private fun showError(message: String) {
        if (!isAdded) return
        progressBar.visibility = View.GONE
        statusText.text = getString(R.string.weather_status_error, message)
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun resetWeatherCard() {
        cityChip.text = getString(R.string.weather_current_city_placeholder)
        statusText.text = getString(R.string.weather_status_idle)
        tempText.text = "--"
        conditionText.text = ""
        windText.text = ""
        humidityText.text = ""
    }

    private fun logDebug(message: String) {
        try {
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA).format(Date())
            val line = "$timestamp [DEBUG] $message"
            Log.d(TAG, line)
            val ctx = context ?: return

            // App 专属外部目录（adb pull /sdcard/Android/data/...，无需存储权限）
            val appLogs = File(ctx.getExternalFilesDir(null), "logs/weather_debug.log")
            writeLine(appLogs, "$line\n")

            // 公共下载目录，便于直接查看（可能需要存储权限，失败会被忽略）
            val downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            writeLine(File(downloads, "weather_debug.log"), "$line\n")
        } catch (_: Exception) {
            // 忽略日志写入异常，避免影响主流程
        }
    }

    // 向后兼容旧名，避免增量编译遗留引用
    @Suppress("Unused")
    @Deprecated("Use logDebug", level = DeprecationLevel.HIDDEN)
    private fun logToFile(message: String) = logDebug(message)

    private fun writeLine(file: File, line: String) {
        try {
            file.parentFile?.let { if (!it.exists()) it.mkdirs() }
            file.appendText(line)
        } catch (_: Exception) {
            // 独立捕获单个文件的写入问题，避免影响其他路径
        }
    }

    private fun renderHistory() {
        val cities = historyStore.history()
        if (cities.isEmpty()) {
            historyGroup.visibility = View.GONE
            historyEmpty.visibility = View.VISIBLE
            return
        }
        historyGroup.visibility = View.VISIBLE
        historyEmpty.visibility = View.GONE
        historyGroup.removeAllViews()
        cities.forEach { city ->
            val chipView = Chip(requireContext()).apply {
                text = city
                isCloseIconVisible = false
                setOnClickListener { fetchWeather(city) }
            }
            historyGroup.addView(chipView)
        }
    }

    private fun showCityListDialog() {
        val cityArray = resources.getStringArray(R.array.weather_city_provinces)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.weather_city_list_button_label))
            .setItems(cityArray) { dialog, which ->
                val city = cityArray[which]
                logDebug("city picker select $city")
                fetchWeather(city)
                dialog.dismiss()
            }
            .show()
    }
}

private data class WeatherInfo(
    val city: String,
    val temperature: String,
    val condition: String,
    val feelsLike: String,
    val windDir: String,
    val windScale: String,
    val humidity: String
)

// 本地模拟天气数据，避免调用商用接口；数值可自行调整
private object MockWeatherProvider {
    private val conditions = listOf("晴", "多云", "小雨", "雷阵雨", "阴", "阵雪")
    private val winds = listOf("东北风", "西南风", "北风", "东风", "西北风")

    fun now(city: String): WeatherInfo {
        val temp = Random.nextInt(10, 32)
        val feels = temp + Random.nextInt(-2, 2)
        val windScale = Random.nextInt(1, 5).toString()
        val humidity = Random.nextInt(35, 90).toString()
        val condition = conditions.random()
        val windDir = winds.random()
        return WeatherInfo(
            city = city,
            temperature = temp.toString(),
            condition = condition,
            feelsLike = feels.toString(),
            windDir = windDir,
            windScale = windScale,
            humidity = humidity
        )
    }
}

// 历史城市存储，替代旧版 WeatherApp 的本地数据库，限制为近期 maxSize 条
private class WeatherHistoryStore(context: Context) {
    private val prefs = context.getSharedPreferences("weather_history", Context.MODE_PRIVATE)
    private val key = "cities"
    private val maxSize = 8

    fun record(city: String) {
        if (city.isBlank()) return
        val list = history().toMutableList()
        list.remove(city)
        list.add(0, city)
        if (list.size > maxSize) {
            list.subList(maxSize, list.size).clear()
        }
        prefs.edit().putString(key, list.joinToString("|")).apply()
    }

    fun history(): List<String> {
        val raw = prefs.getString(key, "").orEmpty()
        return raw.split("|").filter { it.isNotBlank() }
    }
}

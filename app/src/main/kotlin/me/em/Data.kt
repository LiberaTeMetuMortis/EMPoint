package me.em

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.util.logging.Level

class Data(private val plugin: Plugin) {
    private var dataConfig: FileConfiguration? = null
    private var configFile: File? = null

    init {
        saveDefaultConfig()
    }

    fun reloadConfig() {
        if (configFile == null) {
            configFile = File(plugin.dataFolder, "data.yml")
        }
        dataConfig = YamlConfiguration.loadConfiguration(configFile!!)
        val defaultStream = plugin.getResource("data.yml")
        if (defaultStream != null) {
            val defaultConfig = YamlConfiguration.loadConfiguration(InputStreamReader(defaultStream))
            (dataConfig as YamlConfiguration).setDefaults(defaultConfig)
        }
    }

    val config: FileConfiguration?
        get() {
            if (dataConfig == null) {
                reloadConfig()
            }
            return dataConfig
        }

    fun saveConfig() {
        if (dataConfig == null || configFile == null) return
        try {
            config!!.save(configFile!!)
        } catch (e: IOException) {
            plugin.logger.log(Level.SEVERE, "Data kaydedilemedi!")
        }
    }

    fun saveDefaultConfig() {
        if (configFile == null) {
            configFile = File(plugin.dataFolder, "data.yml")
        }
        if (!configFile!!.exists()) {
            plugin.saveResource("data.yml", false)
        }
    }

}
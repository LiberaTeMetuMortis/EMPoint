package me.em

import org.bukkit.ChatColor
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

class point : JavaPlugin(){
    override fun onEnable() {
        this.saveDefaultConfig()
        this.reloadConfig()
        publicConfig = config
        sortAndReloadPoints(this)
        server.getPluginCommand("em")!!.setExecutor(command(this))
        startTimer(this)
        if(server.pluginManager.getPlugin("PlaceholderAPI") != null) PAPI().register()
    }

    override fun onDisable() {
        publicPointData.saveConfig()
    }
    companion object{
        lateinit var publicPointData: data
        lateinit var publicConfig: FileConfiguration
        lateinit var sortedPointList: List<MutableMap.MutableEntry<String, Long>>
        fun translateColors(str: String) = ChatColor.translateAlternateColorCodes('&', str)
        fun startTimer(plugin: JavaPlugin){
            object : BukkitRunnable() {
                override fun run() {
                    reload(plugin, reloadConfig = false)
                }
            }.also { it.runTaskTimer(plugin, publicConfig.getInt("settings.autoRestartPeriod")*1200L, publicConfig.getInt("settings.autoRestartPeriod")*1200L) }
        }
        private fun sortAndReloadPoints(plugin: JavaPlugin){
            val pointData =  data(plugin)
            pointData.saveDefaultConfig()
            pointData.reloadConfig()
            publicPointData = pointData
            val pointMap = mutableMapOf<String, Long>()
            for(path: String in publicPointData.config!!.getKeys(true).filter { it.matches(Regex("players\\..+")) }){
                val name = path.replace("players.", "")
                val point = publicPointData.config!!.getLong(path)
                pointMap[name] = point
            }
            val playerPointMap = ArrayList(pointMap.toSortedMap(compareByDescending{ pointMap[it] }).entries)
            val size = if (playerPointMap.size < 10) playerPointMap.size-1 else 9
            sortedPointList = playerPointMap.slice(0..size)
        }

        fun reload(plugin: JavaPlugin, reloadConfig: Boolean){
            publicPointData.saveConfig()
            if(reloadConfig) {
                plugin.saveDefaultConfig()
                plugin.reloadConfig()
                publicConfig = plugin.config
            }
            else{
                plugin.logger.warning(publicConfig.getString("messages.automaticBackup")?.let(::translateColors))
            }
            sortAndReloadPoints(plugin)
        }
    }
}
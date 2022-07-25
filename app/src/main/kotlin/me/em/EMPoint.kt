package me.em
import org.bukkit.ChatColor
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import java.util.regex.Pattern

class EMPoint : JavaPlugin(){
    override fun onEnable() {
        this.saveDefaultConfig()
        this.reloadConfig()
        publicConfig = config
        sortAndReloadPoints(this)
        server.getPluginCommand("em")!!.setExecutor(Command(this))
        startTimer(this)
        if(server.pluginManager.getPlugin("PlaceholderAPI") != null) PAPI().register()
    }

    override fun onDisable() {
        publicPointData.saveConfig()
    }
    companion object{
        lateinit var publicPointData: Data
        lateinit var publicConfig: FileConfiguration
        lateinit var sortedPointList: List<MutableMap.MutableEntry<String, Long>>


        fun hexToRGB(hex: String): Map<String, Int?> {
            return mapOf(
                "R" to hex.slice(0 until 2).toIntOrNull(16),
                "G" to hex.slice(2 until 4).toIntOrNull(16),
                "B" to hex.slice(4 until 6).toIntOrNull(16)
            )
        }


        fun translateColors(str: String): String {

            if("&#[0-9a-f]{6}".toRegex().containsMatchIn(str)){
                var parsedStr = str
                //println(str.replace("&#([0-9a-f]{6})".toRegex(), "\u00A7$1"));
                for (x in "&(#[0-9A-f]{6})".toRegex().findAll(str)){
                    parsedStr = parsedStr.replaceFirst(x.value.toRegex(), net.md_5.bungee.api.ChatColor.of(x.value.slice(
                        1 until x.value.length
                    )).toString())
                }

                return ChatColor.translateAlternateColorCodes('&', parsedStr)
            }

            return ChatColor.translateAlternateColorCodes('&', str)
        }
        fun startTimer(plugin: JavaPlugin){
            object : BukkitRunnable() {
                override fun run() {
                    reload(plugin, reloadConfig = false)
                }
            }.also { it.runTaskTimer(plugin, publicConfig.getInt("settings.autoRestartPeriod")*1200L, publicConfig.getInt("settings.autoRestartPeriod")*1200L) }
        }
        private fun sortAndReloadPoints(plugin: JavaPlugin){
            val pointData =  Data(plugin)
            pointData.saveDefaultConfig()
            pointData.reloadConfig()
            publicPointData = pointData
            val pointMap = mutableMapOf<String, Long>()
            for(path: String in publicPointData.config!!.getKeys(true).filter { it.matches(Regex("players\\..+")) }){
                val name = path.replace("players.", "")
                val point = publicPointData.config!!.getLong(path)
                pointMap[name] = point
            }
            val playerPointMap = ArrayList(pointMap.toSortedMap(compareByDescending<String> { pointMap[it] }.thenByDescending { it }).entries)
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
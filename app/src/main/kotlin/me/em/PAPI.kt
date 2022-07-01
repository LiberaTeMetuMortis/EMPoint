package me.em
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer
import me.em.point.Companion.sortedPointList
import me.em.point.Companion.publicPointData

class PAPI : PlaceholderExpansion() {
    override fun getAuthor(): String {
        return "MetuMortis"
    }

    override fun getIdentifier(): String {
        return "EMPoint"
    }

    override fun getVersion(): String {
        return "1.0.0"
    }

    override fun onRequest(player: OfflinePlayer, params: String): String? {

        if (params.equals("me", ignoreCase = true)) {
            return publicPointData.config!!.getLong("players.${player.name}", 0L).toString()
        }
        if (params.matches(Regex("top_name_\\d+"))){
            val index = params.replace(Regex("[^0-9]"), "").toInt()-1
            if(index !in sortedPointList.indices) return null
            return sortedPointList[index].key
        }
        if (params.matches(Regex("top_point_\\d+"))){
            val index = params.replace(Regex("[^0-9]"), "").toInt()-1
            if(index !in sortedPointList.indices) return null
            return sortedPointList[index].value.toString()
        }

        return null // Placeholder is unknown by the Expansion
    }
}
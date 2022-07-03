package me.em

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import me.em.EMPoint.Companion.publicConfig
import me.em.EMPoint.Companion.translateColors
import me.em.EMPoint.Companion.publicPointData
import me.em.EMPoint.Companion.reload
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class Command(val plugin: JavaPlugin) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(args.isEmpty()){
            sender.sendMessage(publicConfig.getStringList("helpMessage").map(::translateColors).joinToString("\n"))
            return true
        }
        if(args[0].equals("points", ignoreCase = true)) {
            if(args.size == 2 && sender.hasPermission("em.points.others")){

                sender.sendMessage(publicConfig.getString("messages.balanceOthers")?.let{ translateColors(it.replace("{balance}", publicPointData.config!!.getLong("players."+args[1],
                    0L
                ).toString()).replace("{player}", args[1]))})
                return true
            }
            if(sender is ConsoleCommandSender){
                sender.sendMessage(publicConfig.getString("messages.forOnlyPlayers")?.let(::translateColors))
                return true
            }
            else if(sender is Player){
                sender.sendMessage(publicConfig.getString("messages.balance")?.let{ translateColors(it.replace("{balance}", publicPointData.config!!.getLong("players."+sender.name,
                    0L
                ).toString()))})
                return true

            }

        }
        else if(args[0].equals("set", ignoreCase = true)){
            if(!sender.hasPermission("em.set")){
                sender.sendMessage(publicConfig.getString("messages.noPerm")?.let(::translateColors))
                return true
            }
            if(args.size != 3){
                sender.sendMessage(publicConfig.getString("usages.set")?.let(::translateColors))
                return true
            }
            if(args[2].toLongOrNull() == null){
                sender.sendMessage(publicConfig.getString("usages.set")?.let(::translateColors))
                return true
            }
            val playerName = args[1]
            val amount = args[2]
            sender.sendMessage(publicConfig.getString("messages.successfullySet")?.let { translateColors(it.replace("{player}", args[1]).replace("{balance}", args[2])) })
            publicPointData.config!!.set("players.$playerName", amount.toLongOrNull())
            return true
        }
        else if(args[0].equals("take", ignoreCase = true)){
            if(!sender.hasPermission("em.take")){
                sender.sendMessage(publicConfig.getString("messages.noPerm")?.let(::translateColors))
                return true
            }
            if(args.size != 3){
                sender.sendMessage(publicConfig.getString("usages.take")?.let(::translateColors))
                return true
            }

            if(args[2].toLongOrNull() == null){
                sender.sendMessage(publicConfig.getString("usages.take")?.let(::translateColors))
                return true
            }
            val playerName = args[1]
            val amount = args[2].toLong()
            val currentAmount = publicPointData.config!!.getLong("players.$playerName", 0L)
            if(amount > currentAmount){
                sender.sendMessage(publicConfig.getString("messages.notEnoughBalance")?.let(::translateColors))
                return true
            }
            sender.sendMessage(publicConfig.getString("messages.successfullyTaken")?.let { translateColors(it.replace("{player}", args[1]).replace("{balance}", args[2])) })
            publicPointData.config!!.set("players.$playerName", currentAmount-amount)
            return true
        }
        else if(args[0].equals("give", ignoreCase = true)){
            if(!sender.hasPermission("em.give")){
                sender.sendMessage(publicConfig.getString("messages.noPerm")?.let(::translateColors))
                return true
            }
            if(args.size != 3){
                sender.sendMessage(publicConfig.getString("usages.give")?.let(::translateColors))
                return true
            }

            if(args[2].toLongOrNull() == null){
                sender.sendMessage(publicConfig.getString("usages.give")?.let(::translateColors))
                return true
            }
            val playerName = args[1]
            val amount = args[2].toLongOrNull() as Long
            val currentAmount = publicPointData.config!!.getLong("players.$playerName", 0L)
            sender.sendMessage(publicConfig.getString("messages.successfullyGiven")?.let { translateColors(it.replace("{player}", args[1]).replace("{balance}", args[2])) })
            publicPointData.config!!.set("players.$playerName", currentAmount+amount)
            return true
        }
        else if(args[0].equals("reset", ignoreCase = true)){
            if(!sender.hasPermission("em.reset")){
                sender.sendMessage(publicConfig.getString("messages.noPerm")?.let(::translateColors))
                return true
            }
            if(args.size != 2){
                sender.sendMessage(publicConfig.getString("usages.reset")?.let(::translateColors))
                return true
            }
            sender.sendMessage(publicConfig.getString("messages.successfullyReset")?.let { translateColors(it.replace("{player}", args[1])) })
            publicPointData.config!!.set("players.${args[1]}", 0L)
            return true
        }
        else if(args[0].equals("reload", ignoreCase = true)){
            if(!sender.hasPermission("em.relaod")){
                sender.sendMessage(publicConfig.getString("messages.noPerm")?.let(::translateColors))
                return true
            }
            sender.sendMessage(publicConfig.getString("messages.successfullyReloaded")?.let(::translateColors))
            reload(plugin, reloadConfig = true)
            return true
        }

        return false
    }
}
package com.ericlam.mc.configdto

import org.bukkit.ChatColor
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin

class ConfigDTO : JavaPlugin() {

    companion object Api : ConfigAPI {
        override fun getFactory(plugin: Plugin): ConfigFactory {
            return ConfigBuilder(plugin)
        }

        override fun color(string: String?): String {
            return ChatColor.translateAlternateColorCodes('&', string ?: "null")
        }
    }

    override fun onEnable() {
        logger.info("ConfigDAO enabled!! by Eric Lam")
    }

    override fun onDisable() {
        logger.info("ConfigDAO disabled.")
    }

}
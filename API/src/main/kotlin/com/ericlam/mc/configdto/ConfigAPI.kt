package com.ericlam.mc.configdto

import org.bukkit.plugin.Plugin

/**
 * Config API
 */
interface ConfigAPI {

    /**
     * @param plugin the plugin where you want to create config
     * @return the config factory new instance
     */
    fun getFactory(plugin: Plugin): ConfigFactory

    /**
     * @param string you want to color
     * @return colored string
     */
    fun color(string: String?): String

}
package com.ericlam.mc.configdto

import org.bukkit.plugin.Plugin

interface ConfigAPI {

    fun getFactory(plugin: Plugin): ConfigFactory

    fun color(string: String?): String

}
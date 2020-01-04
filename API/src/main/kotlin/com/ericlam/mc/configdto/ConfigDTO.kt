package com.ericlam.mc.configdto

import org.bukkit.plugin.Plugin

class ConfigDTO {
    companion object Api : ConfigAPI {
        override fun getFactory(plugin: Plugin): ConfigFactory {
            throw Exception("you are using the API.jar to run in the server")
        }

        override fun color(string: String?): String {
            throw Exception("you are using the API.jar to run in the server")
        }
    }
}
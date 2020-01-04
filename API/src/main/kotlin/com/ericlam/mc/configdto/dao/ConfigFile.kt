package com.ericlam.mc.configdto.dao

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.yaml.snakeyaml.Yaml
import java.io.*

abstract class ConfigFile {
    private lateinit var _config: FileConfiguration
    private lateinit var _file: File

    protected val config: FileConfiguration
        get() = if (::_config.isInitialized) _config else throw IllegalStateException("Config has not initialized")

    private val file: File
        get() = if (::_file.isInitialized) _file else throw IllegalStateException("File has not initialized")

    fun save(){
        val yaml = Yaml()
        PrintWriter(FileWriter(file)).use {
            it.print(yaml.dumpAsMap(this))
        }
        _config = YamlConfiguration.loadConfiguration(file)
    }

    open operator fun get(node: String): Any? = config.get(node)
}
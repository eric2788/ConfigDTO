package com.ericlam.mc.configdto.dao

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter

/**
 * config file for extends when you create yaml
 */
abstract class ConfigFile {
    private lateinit var _config: FileConfiguration
    private lateinit var _file: File
    private lateinit var _yaml: Yaml

    protected val config: FileConfiguration
        get() = if (::_config.isInitialized) _config else throw IllegalStateException("Config has not initialized")

    protected val file: File
        get() = if (::_file.isInitialized) _file else throw IllegalStateException("File has not initialized")

    private val yaml: Yaml
        get() = if (::_yaml.isInitialized) _yaml else throw IllegalStateException("Yaml has not initialized")

    /**
     * save the yaml base on your object values
     */
    open fun save() {
        PrintWriter(FileWriter(file)).use {
            it.print(yaml.dumpAsMap(this))
        }
        _config = YamlConfiguration.loadConfiguration(file)
    }

    /**
     * get data with using common way
     * @param node path
     * @return object
     */
    open operator fun get(node: String): Any? = config.get(node)
}
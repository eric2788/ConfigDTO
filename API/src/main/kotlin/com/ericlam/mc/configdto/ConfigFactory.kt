package com.ericlam.mc.configdto

import com.ericlam.mc.configdto.dao.ConfigFile

interface ConfigFactory {

    fun <T : ConfigFile> register(config: Class<T>): ConfigFactory

    fun dump(): ConfigManager
}
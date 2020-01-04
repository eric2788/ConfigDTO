package com.ericlam.mc.configdto

import com.ericlam.mc.configdto.dao.ConfigFile

interface ConfigManager {

    fun <T : ConfigFile> getConfig(config: Class<T>): T

    fun <T : ConfigFile> reload(config: Class<T>)


}
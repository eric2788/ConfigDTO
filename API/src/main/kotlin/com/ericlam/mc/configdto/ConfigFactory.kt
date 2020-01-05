package com.ericlam.mc.configdto

import com.ericlam.mc.configdto.dao.ConfigFile

/**
 * The Config Factory to register config
 */
interface ConfigFactory {

    /**
     * @param config the class config
     * @return this
     */
    fun <T : ConfigFile> register(config: Class<T>): ConfigFactory

    /**
     * @return the config manager
     */
    fun dump(): ConfigManager
}
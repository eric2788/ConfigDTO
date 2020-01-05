package com.ericlam.mc.configdto

import com.ericlam.mc.configdto.dao.ConfigFile

/**
 * Config Manager
 */
interface ConfigManager {

    /**
     * @param config the config class
     * @return the instance of the config class
     */
    fun <T : ConfigFile> getConfig(config: Class<T>): T

    /**
     * After reload, you need to recall getConfig to get the latest data
     * <p>
     * @see getConfig
     * @param config the config class
     */
    fun <T : ConfigFile> reload(config: Class<T>)


}
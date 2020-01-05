package com.ericlam.mc.configdto.dao

import com.ericlam.mc.configdto.ConfigDTO.Api.color

/**
 * message file for extends when you create language yaml
 */
abstract class MessageFile : ConfigFile() {
    private lateinit var _prefix: String

    val prefix: String
        get() = if (::_prefix.isInitialized) color(_prefix) else throw IllegalStateException("Prefix has not initialized")

    /**
     * @param node the path
     * @return the string colored without prefix
     */
    fun getPure(node: String): String = color(config.getString(node))

    /**
     * @param node the path
     * @return the string colored with prefix
     */
    override operator fun get(node: String) = prefix + getPure(node)

    override fun save() {
        config.save(file)
    }
}
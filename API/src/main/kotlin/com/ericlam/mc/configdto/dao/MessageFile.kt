package com.ericlam.mc.configdto.dao

import com.ericlam.mc.configdto.ConfigDTO.Api.color

abstract class MessageFile : ConfigFile() {
    private lateinit var _prefix: String

    val prefix: String
        get() = if (::_prefix.isInitialized) color(_prefix) else throw IllegalStateException("Prefix has not initialized")

    fun getPure(node: String): String = color(config.getString(node))

    override operator fun get(node: String) = prefix + getPure(node)
}
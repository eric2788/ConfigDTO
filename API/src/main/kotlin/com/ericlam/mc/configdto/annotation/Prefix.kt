package com.ericlam.mc.configdto.annotation

/**
 * User for prefix path marking
 *
 * @property path the path of prefix string
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
annotation class Prefix(val path: String)
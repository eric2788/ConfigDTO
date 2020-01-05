package com.ericlam.mc.configdto.annotation

/**
 * the resource to locate and copyTo
 * <p>
 * if copy to is empty, it will use the path of locate
 *
 * @property copyTo the destination to copy
 * @property locate the path of resource locate
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Resource(val locate: String, val copyTo: String = "")
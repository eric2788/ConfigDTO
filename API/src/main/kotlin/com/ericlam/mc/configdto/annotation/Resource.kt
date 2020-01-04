package com.ericlam.mc.configdto.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Resource(val locate: String, val copyTo: String = "")
package com.ericlam.mc.configdto

import com.ericlam.mc.configdto.annotation.Prefix
import com.ericlam.mc.configdto.annotation.Resource
import com.ericlam.mc.configdto.dao.ConfigFile
import com.ericlam.mc.configdto.dao.MessageFile
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.plugin.Plugin
import org.yaml.snakeyaml.DumperOptions.FlowStyle
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor
import org.yaml.snakeyaml.nodes.*
import org.yaml.snakeyaml.representer.Represent
import org.yaml.snakeyaml.representer.Representer
import java.io.File
import java.io.FileInputStream

class ConfigBuilder(private val plugin: Plugin) : ConfigFactory {

    private val list: MutableList<Class<out ConfigFile>> = mutableListOf()

    override fun <T : ConfigFile> register(config: Class<T>): ConfigFactory {
        list.add(config).also { return this }
    }

    private operator fun File.get(file: String) = File(this, file)

    override fun dump(): ConfigManager {
        return object : ConfigManager {

            private val map: MutableMap<Class<*>, ConfigFile> = mutableMapOf()

            init {
                list.forEach { reload(it) }
            }

            override fun <T : ConfigFile> reload(config: Class<T>) {
                val res = config.getAnnotation(Resource::class.java)
                        ?: throw IllegalStateException("${config.name} is lack of @Resource annotation")
                val f = plugin.dataFolder[res.copyTo.takeUnless { it.isBlank() } ?: res.locate]
                f.parentFile.mkdirs()
                if (!f.exists()) plugin.saveResource(res.locate, true)
                val cfg = YamlConfiguration.loadConfiguration(f)
                val customConstructor = object : CustomClassLoaderConstructor(plugin::class.java.classLoader) {

                    init {
                        this.yamlClassConstructors[NodeId.scalar] = EnumConstructor()
                        this.yamlClassConstructors[NodeId.mapping] = SerializableConstructor()
                        propertyUtils.isSkipMissingProperties = true
                        propertyUtils.isAllowReadOnlyProperties = true
                    }

                    private inner class EnumConstructor : ConstructScalar() {
                        override fun construct(nnode: Node?): Any {
                            val node = nnode ?: return super.construct(nnode)
                            if (node.type.isEnum) {
                                val value = (node as ScalarNode).value
                                return node.type.enumConstants.find { (it as Enum<*>).name == value }
                                        ?: throw IllegalArgumentException("$value is not in enum class ${node.type.name}")
                            }
                            return super.construct(nnode)
                        }
                    }

                    private inner class SerializableConstructor : ConstructMapping() {


                        override fun constructJavaBean2ndStep(node: MappingNode?, `object`: Any?): Any {
                            val result = node ?: return super.constructJavaBean2ndStep(node, `object`)
                            val filtered = result.value.filter { tuple ->
                                val key = tuple.keyNode as ScalarNode
                                config.declaredFields.any { it.name == key.value }.also { if (!it) plugin.logger.warning("property ${key.value} is not on class ${result.type.name}, skipped") }
                            }
                            result.value = filtered
                            return super.constructJavaBean2ndStep(result, `object`)
                        }

                        override fun construct(node: Node?): Any {
                            node ?: return super.construct(node)

                            fun isSerialize(node: Node): Boolean {
                                return node is MappingNode && node.value.any { (it.keyNode as ScalarNode).value == "==" }
                            }

                            return if (ConfigurationSerializable::class.java.isAssignableFrom(node.type) || isSerialize(node)) {
                                if (node.isTwoStepsConstruction) return super.construct(node)
                                val mapNode = node as MappingNode
                                val objMap = mapNode.value.map { Pair<String, Any>((it.keyNode as ScalarNode).value, this.construct(it.valueNode)) }.toMap()
                                ConfigurationSerialization.deserializeObject(objMap) as Any
                            } else {
                                return if (node is MappingNode) super.construct(node) else constructObject(node)
                            }
                        }
                    }
                }

                val customRepresent = object : Representer() {
                    init {
                        fun mapping(tag: Tag, map: Map<*, *>, flow: FlowStyle): Node = this.representMapping(tag, map, flow)
                        fun tag(cls: Class<*>, tag: Tag): Tag = this.getTag(cls, tag)
                        fun superPresent(p0: Any?): Node = mapping(tag(p0!!::class.java, Tag.MAP), p0 as Map<*, *>, FlowStyle.AUTO)
                        this.multiRepresenters[ConfigurationSerializable::class.java] = Represent { p0 ->
                            @Suppress("UNCHECKED_CAST") val value = if (p0 is ConfigurationSerializable) {
                                val m = LinkedHashMap<String, Any>()
                                m.put("==", ConfigurationSerialization.getAlias(p0::class.java as Class<out ConfigurationSerializable>)).also { m.putAll(p0.serialize()) }
                                m
                            } else {
                                p0
                            }
                            superPresent(value)
                        }
                    }
                }
                val yaml = Yaml(customConstructor, customRepresent)
                val ins = yaml.loadAs(FileInputStream(f), config)
                val superCls = if (ins is MessageFile) config.superclass.superclass else config.superclass
                superCls.getDeclaredField("_config").also { field -> field.isAccessible = true; field.set(ins, cfg) }
                superCls.getDeclaredField("_file").also { field -> field.isAccessible = true; field.set(ins, f) }
                superCls.getDeclaredField("_yaml").also { field -> field.isAccessible = true; field.set(ins, yaml) }
                if (ins is MessageFile) {
                    val pre = config.getAnnotation(Prefix::class.java)
                            ?: throw IllegalStateException("${config.simpleName} is lack of @Prefix annotation")
                    val prefix = ins.getPure(pre.path)
                    config.superclass.getDeclaredField("_prefix").also { field -> field.isAccessible = true; field.set(ins, prefix) }
                }
                map[config] = ins
            }


            override fun <T : ConfigFile> getConfig(config: Class<T>): T {
                return config.cast(map[config]) ?: throw IllegalStateException("${config.simpleName} is not exist.")
            }

        }
    }
}
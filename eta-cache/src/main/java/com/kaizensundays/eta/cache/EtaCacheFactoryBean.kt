package com.kaizensundays.eta.cache

import org.springframework.beans.factory.FactoryBean

/**
 * Created: Sunday 10/20/2024, 1:04 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class EtaCacheFactoryBean : FactoryBean<EtaCache<*, *>> {

    lateinit var node: EtaCacheNode
    lateinit var cacheName: String

    override fun getObject(): EtaCache<*, *> {
        return node.getCache<Any, Any>(cacheName)
    }

    override fun getObjectType(): Class<*> {
        return EtaCache::class.java
    }

}
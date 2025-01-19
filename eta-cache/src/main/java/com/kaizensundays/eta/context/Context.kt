package com.kaizensundays.eta.context

import com.kaizensundays.eta.cache.EtaCacheNode
import com.kaizensundays.eta.cache.EtaCacheNodeImpl
import com.kaizensundays.eta.cache.EtaNodeConfiguration
import com.kaizensundays.eta.jgroups.JGroupsRaftNode

/**
 * Created: Sunday 1/19/2025, 11:08 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
object Context {

    fun cacheNode(conf: EtaNodeConfiguration): EtaCacheNode {
        val node = EtaCacheNodeImpl(JGroupsRaftNode())
        node.configuration = conf
        return node
    }

}
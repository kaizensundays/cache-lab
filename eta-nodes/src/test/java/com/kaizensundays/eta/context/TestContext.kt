package com.kaizensundays.eta.context

import com.kaizensundays.eta.cache.MessageGrid
import com.kaizensundays.eta.cache.MessageGridImpl
import com.kaizensundays.eta.jgroups.JGroupsMessageBus
import org.jgroups.JChannel

/**
 * Created: Sunday 12/22/2024, 2:54 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class TestContext {

    fun createMessageGrid(clusterName: String): MessageGrid {

        val channel = JChannel()

        val messageBus = JGroupsMessageBus(channel)

        channel.setReceiver(messageBus)
        channel.connect(clusterName)

        return MessageGridImpl(messageBus)
    }

}
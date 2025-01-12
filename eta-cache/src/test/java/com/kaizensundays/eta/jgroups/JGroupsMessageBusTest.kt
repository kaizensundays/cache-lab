package com.kaizensundays.eta.jgroups

import com.kaizensundays.eta.raft.NopMessageListener
import org.jgroups.JChannel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock

/**
 * Created: Sunday 12/22/2024, 11:27 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
class JGroupsMessageBusTest {

    private val mainChannel: JChannel = mock()

    private val receiver = JGroupsMessageBus(mainChannel)

    @Test
    fun test() {

        assertEquals(0, receiver.getListeners("t").size)

        val listeners = arrayOf(NopMessageListener(), NopMessageListener(), NopMessageListener())

        listeners.forEach { l -> receiver.addListener("t", l) }

        var list = receiver.getListeners("t")

        assertEquals(listeners.size, list.size)
        listeners.forEach { l -> assertTrue(list.contains(l)) }

        receiver.removeListener("t", listeners[1])

        list = receiver.getListeners("t")
        assertFalse(list.contains(listeners[1]))

        listeners.forEach { l -> receiver.removeListener("t", l) }
        assertEquals(0, receiver.getListeners("t").size)
    }

}
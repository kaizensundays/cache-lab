package com.kaizensundays.eta.jgroups

import com.kaizensundays.eta.raft.NodeEventListener
import com.kaizensundays.eta.raft.NodeEventType
import org.jgroups.Receiver
import org.jgroups.View

/**
 * Created: Sunday 12/8/2024, 3:16 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class JGroupsEventListener(private val listener: NodeEventListener) : Receiver {

    override fun viewAccepted(view: View?) {
        listener.onEvent(NodeEventType.ViewAccepted, view?.toString() ?: "?")
    }

}
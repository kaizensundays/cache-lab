package com.kaizensundays.eta.jgroups

import com.kaizensundays.eta.raft.RoleChangeListener
import org.jgroups.protocols.raft.RAFT
import org.jgroups.protocols.raft.Role

/**
 * Created: Sunday 12/29/2024, 5:15 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class JGroupsRoleChangeListener(private val listener: RoleChangeListener) : RAFT.RoleChange {

    override fun roleChanged(role: Role) {
        listener.onEvent(role.name)
    }

}
/**
 * Created: Saturday 10/12/2024, 12:55 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
module jgroups.cache {

    exports com.kaizensundays.eta.cache;
    exports com.kaizensundays.eta.raft;

    requires java.base;
    requires kotlin.stdlib;
    requires org.slf4j;
    requires cache.api;
    requires org.jgroups;
    requires jgroups.raft;
    requires leveldb;
    requires leveldb.api;
    requires spring.beans;

    uses javax.cache.spi.CachingProvider;
    uses com.kaizensundays.eta.raft.RaftNode;

    provides javax.cache.spi.CachingProvider with com.kaizensundays.eta.cache.CachingProvider;
    provides com.kaizensundays.eta.raft.RaftNode with com.kaizensundays.eta.jgroups.JGroupsRaftNode;
}
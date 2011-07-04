package org.nuxeo.ecm.platform.ui.granite.filter;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Helper class used to avoid concurrent access from several threads on the same
 * CoreSession
 *
 * @author Tiry (tdelprat@nuxeo.com)
 *
 */
public class SessionConcurrencyManager {

    protected static final int MAX_TIMEOUT_WAIT_S = 60;

    protected static Log log = LogFactory.getLog(SessionConcurrencyManager.class);

    // stores lock per CoreSession
    protected static ConcurrentHashMap<String, ReentrantLock> locksOnCoreSession = new ConcurrentHashMap<String, ReentrantLock>();

    // stores current thread bound SessionId
    protected static ThreadLocal<String> threadSessionId = new ThreadLocal<String>();

    // list of SessionIds waiting for access
    protected static List<String> sessionIdWaitingForAccess = new CopyOnWriteArrayList<String>();

    public static void getExclusiveAccess(CoreSession session)
            throws ClientException {

        if (threadSessionId.get() != null) {
            if (!threadSessionId.get().equals(session.getSessionId())) {
                log.warn("You are accessing several core session within the same thread, is this normal ?");
            }
            return;
        }

        // create lock object if needed
        locksOnCoreSession.putIfAbsent(session.getSessionId(),
                new ReentrantLock());
        String sid = session.getSessionId();
        try {
            ReentrantLock lock = locksOnCoreSession.get(sid);

            // mark this session as synch
            sessionIdWaitingForAccess.add(sid);

            // acquire lock
            boolean sync = lock.tryLock(MAX_TIMEOUT_WAIT_S, TimeUnit.SECONDS);

            if (sync) {
                sessionIdWaitingForAccess.remove(sid);
                threadSessionId.set(session.getSessionId());
            } else {
                throw new ClientException(
                        "Unable to obtains exclusive access to session " + sid
                                + " within the blocking timeout");
            }
        } catch (InterruptedException e) {
            log.error("can not obtain exclusive access to CoreSession "
                    + session.getSessionId(), e);
            throw new ClientException(
                    "Error while trying to obtain exclusive access to session "
                            + sid, e);
        }
    }

    public static void release() {
        String sid = threadSessionId.get();
        if (sid != null) {
            ReentrantLock lock = locksOnCoreSession.get(sid);
            if (lock != null && lock.isHeldByCurrentThread()) {
                lock.unlock();
                if (!sessionIdWaitingForAccess.contains(sid)) {
                    // no one else waiting for (?!)
                    locksOnCoreSession.remove(sid);
                }
            }
            threadSessionId.set(null);
        }
    }

}

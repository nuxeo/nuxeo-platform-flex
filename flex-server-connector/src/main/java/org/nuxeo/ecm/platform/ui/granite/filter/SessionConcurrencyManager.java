package org.nuxeo.ecm.platform.ui.granite.filter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Helper class used to avoid concurrent access from several threads on the same CoreSession
 *
 * TODO : add a GC
 *
 * @author Tiry (tdelprat@nuxeo.com)
 *
 */
public class SessionConcurrencyManager {

    protected static final int MAX_TIMEOUT_WAIT_S = 60;

    protected static Log log = LogFactory.getLog(SessionConcurrencyManager.class);

    protected static Map<String, Lock> coreSessionLocks = new ConcurrentHashMap<String, Lock>();

    protected static ThreadLocal<String> coreSessionId = new ThreadLocal<String>();

    public static void getExclusiveAccess(CoreSession session) {

        if (coreSessionId.get() != null) {
            if (!coreSessionId.get().equals(session.getSessionId())) {
                log.warn("You are accessing several core session within the same thread, is this normal ?");
            }
            return;
        }

        synchronized (coreSessionLocks) {
            if (!coreSessionLocks.keySet().contains(session.getSessionId())) {
                coreSessionLocks.put(session.getSessionId(),
                        new ReentrantLock());
            }
        }
        try {
            coreSessionLocks.get(session.getSessionId()).tryLock(
                    MAX_TIMEOUT_WAIT_S, TimeUnit.SECONDS);
            coreSessionId.set(session.getSessionId());
        } catch (InterruptedException e) {
            log.error("can not obtain exclusive access to CoreSession "
                    + session.getSessionId(), e);
        }

    }

    public static void release() {
        if (coreSessionId.get() != null) {
            synchronized (coreSessionLocks) {
                coreSessionLocks.get(coreSessionId.get()).unlock();
                coreSessionId.set(null);
            }
        }
    }

}

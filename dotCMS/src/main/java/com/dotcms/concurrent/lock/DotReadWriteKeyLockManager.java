package com.dotcms.concurrent.lock;

import com.dotcms.util.ReturnableDelegate;

public interface DotReadWriteKeyLockManager <K> {

    <R> R tryReadLock(K key, ReturnableDelegate<R> callback) throws Throwable;

}

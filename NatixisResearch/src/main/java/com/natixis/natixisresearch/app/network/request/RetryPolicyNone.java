package com.natixis.natixisresearch.app.network.request;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.retry.RetryPolicy;

/**
 * Created by Thibaud on 18/04/2017.
 */
public class RetryPolicyNone implements RetryPolicy {
    /**
     * @return the remaining number of retry attempts. When this method returns
     * 0, request is not retried anymore.
     */
    @Override
    public int getRetryCount() {
        return 0;
    }

    /**
     * @param e
     * @return the delay to sleep between each retry attempt (in ms).
     */
    @Override
    public void retry(SpiceException e) {

    }

    /**
     * @return the delay to sleep between each retry attempt (in ms).
     */
    @Override
    public long getDelayBeforeRetry() {
        return 0;
    }
}

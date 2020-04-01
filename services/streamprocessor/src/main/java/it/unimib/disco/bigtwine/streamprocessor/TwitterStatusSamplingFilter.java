package it.unimib.disco.bigtwine.streamprocessor;

import org.apache.flink.api.common.functions.FilterFunction;

public class TwitterStatusSamplingFilter implements FilterFunction<String> {

    private int maxTweetsPerSecond;
    private long tweetsInterval;
    private long lastTweetTs;

    public TwitterStatusSamplingFilter(int maxTweetsPerSecond) {
        this.maxTweetsPerSecond = maxTweetsPerSecond;
        this.tweetsInterval = 1000 / maxTweetsPerSecond;
    }

    public int getMaxTweetsPerSecond() {
        return maxTweetsPerSecond;
    }

    @Override
    public boolean filter(String value) throws Exception {
        long oldTs = this.lastTweetTs;
        long nowTs = System.currentTimeMillis();
        this.lastTweetTs = nowTs;

        return (nowTs - oldTs) > tweetsInterval;
    }
}

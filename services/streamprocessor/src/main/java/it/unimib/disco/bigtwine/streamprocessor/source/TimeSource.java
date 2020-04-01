package it.unimib.disco.bigtwine.streamprocessor.source;

import org.apache.flink.streaming.api.functions.source.SourceFunction;

public class TimeSource implements SourceFunction<Integer> {
    private boolean isRunning = true;

    @Override
    public void run(SourceContext<Integer> ctx) throws Exception {
        while(isRunning) {
            ctx.collect((int)(System.currentTimeMillis() / 1000));
            Thread.sleep(1000);
        }
    }

    @Override
    public void cancel() {
        isRunning = false;
    }
}

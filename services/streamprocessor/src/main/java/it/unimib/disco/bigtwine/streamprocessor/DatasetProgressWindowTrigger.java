package it.unimib.disco.bigtwine.streamprocessor;

import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeutils.base.LongSerializer;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;

public class DatasetProgressWindowTrigger extends Trigger<Tuple2<Integer, Integer>, GlobalWindow> {
    private final long period;

    private final ValueStateDescriptor<Long> processingTimeTriggerDescriptor =
            new ValueStateDescriptor<>("PROCESSING_TIME_TRIGGER", LongSerializer.INSTANCE);

    public static DatasetProgressWindowTrigger create(Time timeout) {
        return new DatasetProgressWindowTrigger(timeout);
    }

    private DatasetProgressWindowTrigger(Time period) {
        this.period = period.toMilliseconds();
    }

    @Override
    public TriggerResult onElement(Tuple2<Integer, Integer> element, long timestamp, GlobalWindow window, TriggerContext ctx) throws Exception {
        ValueState<Long> processingTimeTrigger = ctx.getPartitionedState(processingTimeTriggerDescriptor);
        if(processingTimeTrigger.value() == null) {
            long nextProcessingTimeTriggerTimestamp = getNextProcessingTimeTriggerTimestamp();
            processingTimeTrigger.update(nextProcessingTimeTriggerTimestamp);
            ctx.registerProcessingTimeTimer(nextProcessingTimeTriggerTimestamp);
        }
        return TriggerResult.CONTINUE;
    }

    @Override
    public TriggerResult onProcessingTime(long time, GlobalWindow window, TriggerContext ctx) throws Exception {
        ValueState<Long> processingTimeTrigger = ctx.getPartitionedState(processingTimeTriggerDescriptor);
        long nextProcessingTimeTriggerTimestamp = getNextProcessingTimeTriggerTimestamp();
        processingTimeTrigger.update(nextProcessingTimeTriggerTimestamp);
        ctx.registerProcessingTimeTimer(nextProcessingTimeTriggerTimestamp);
        return TriggerResult.FIRE_AND_PURGE;
    }

    @Override
    public TriggerResult onEventTime(long time, GlobalWindow window, TriggerContext ctx) throws Exception {
        return TriggerResult.CONTINUE;
    }

    @Override
    public void clear(GlobalWindow window, TriggerContext ctx) throws Exception {
        ValueState<Long> processingTimeTrigger = ctx.getPartitionedState(processingTimeTriggerDescriptor);
        Long late = processingTimeTrigger.value();
        if(late != null) {
            ctx.deleteProcessingTimeTimer(late);
        }
        processingTimeTrigger.clear();
    }

    private long getNextProcessingTimeTriggerTimestamp() {
        return System.currentTimeMillis() + period;
    }
}

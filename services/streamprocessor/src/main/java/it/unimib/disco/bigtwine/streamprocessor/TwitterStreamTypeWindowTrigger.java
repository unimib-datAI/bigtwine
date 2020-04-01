package it.unimib.disco.bigtwine.streamprocessor;

import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.util.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;

public class TwitterStreamTypeWindowTrigger extends Trigger<Tuple3<String, Object, StreamType>, GlobalWindow> {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterStreamTypeWindowTrigger.class);
    private final ValueStateDescriptor<EnumSet<StreamType>> receivedStreamsDescriptor =
            new ValueStateDescriptor<>("RECEIVED_STREAMS", TypeInformation.of(new TypeHint<EnumSet<StreamType>>(){}));

    private final long period;

    private final ValueStateDescriptor<Long> processingTimeTriggerDescriptor =
            new ValueStateDescriptor<>("PROCESSING_TIME_TRIGGER", Long.class);

    public static TwitterStreamTypeWindowTrigger create(Time period) {
        return new TwitterStreamTypeWindowTrigger(period.toMilliseconds());
    }

    private TwitterStreamTypeWindowTrigger(long period) {
        Preconditions.checkArgument(period >= 0);
        this.period = period;
    }

    @Override
    public TriggerResult onElement(Tuple3<String, Object, StreamType> element, long timestamp, GlobalWindow window, TriggerContext ctx) throws Exception {
        ValueState<Long> processingTimeTrigger = ctx.getPartitionedState(processingTimeTriggerDescriptor);
        ValueState<EnumSet<StreamType>> receivedStreams = ctx.getPartitionedState(receivedStreamsDescriptor);
        EnumSet<StreamType> streams = receivedStreams.value();

        if (processingTimeTrigger.value() == null) {
            long nextProcessingTimeTriggerTimestamp = System.currentTimeMillis() + period;
            processingTimeTrigger.update(nextProcessingTimeTriggerTimestamp);
            ctx.registerProcessingTimeTimer(nextProcessingTimeTriggerTimestamp);
        }

        if (streams == null) {
            streams = EnumSet.of(element.f2);
        } else {
            streams.add(element.f2);
        }

        receivedStreams.update(streams);

        LOG.debug("Received from stream {} for tag {} (current size: {})", element.f2, element.f0, streams.size());

        if (streams.size() == 4) {
            return TriggerResult.FIRE_AND_PURGE;
        } else {
            ctx.registerProcessingTimeTimer(window.maxTimestamp());
            return TriggerResult.CONTINUE;
        }
    }

    @Override
    public TriggerResult onProcessingTime(long time, GlobalWindow window, TriggerContext ctx) throws Exception {
        ValueState<EnumSet<StreamType>> receivedStreams = ctx.getPartitionedState(receivedStreamsDescriptor);
        EnumSet<StreamType> streams = receivedStreams.value();

        if (streams.size() > 0 && streams.contains(StreamType.status)) {
            return TriggerResult.FIRE_AND_PURGE;
        } else {
            return TriggerResult.PURGE;
        }
    }

    @Override
    public TriggerResult onEventTime(long time, GlobalWindow window, TriggerContext ctx) throws Exception {
        return TriggerResult.CONTINUE;
    }

    @Override
    public void clear(GlobalWindow window, TriggerContext ctx) throws Exception {
        ValueState<EnumSet<StreamType>> receivedStreams = ctx.getPartitionedState(receivedStreamsDescriptor);
        receivedStreams.clear();

        ValueState<Long> processingTimeTrigger = ctx.getPartitionedState(processingTimeTriggerDescriptor);
        long late = processingTimeTrigger.value();
        if(late != -1) {
            ctx.deleteProcessingTimeTimer(late);
        }
        processingTimeTrigger.clear();
    }

    @Override
    public String toString() {
        return "TwitterStreamTypeWindowTrigger()";
    }
}

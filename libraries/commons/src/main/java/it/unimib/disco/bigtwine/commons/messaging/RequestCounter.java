package it.unimib.disco.bigtwine.commons.messaging;

public class RequestCounter<R extends RequestMessage> {
    private R request;
    private long count;

    public RequestCounter(R request) {
        this.request = request;
    }

    public RequestCounter(R request, long count) {
        this(request);
        this.count = count;
    }

    public R get() {
        return request;
    }

    public long getCount() {
        return count;
    }

    public void increment(long value) {
        this.count += value;
    }

    public void increment() {
        this.increment(1);
    }

    public void decrement(long value) {
        this.count -= value;
        if (this.count < 0) {
            this.count = 0;
        }
    }

    public void decrement() {
        this.decrement(1);
    }

    public boolean hasMore() {
        return this.count > 0;
    }
}

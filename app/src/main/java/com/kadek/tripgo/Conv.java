package com.kadek.tripgo;

/**
 * Created by K on 08/03/2018.
 */

public class Conv {

    public boolean seen;
    public long timestamp;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isSeen() {

        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public Conv(boolean seen, long timestamp) {

        this.seen = seen;
        this.timestamp = timestamp;
    }

    public Conv() {

    }
}

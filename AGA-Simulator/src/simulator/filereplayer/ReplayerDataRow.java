package simulator.filereplayer;

public class ReplayerDataRow implements Comparable<ReplayerDataRow> {

    private int signalID;
    private String data;
    private long timestamp;

    public ReplayerDataRow(int signalID, String data, long timestamp) {
        this.signalID = signalID;
        this.data = data;
        this.timestamp = timestamp;
    }

    public int getSignalID() {
        return signalID;
    }

    public String getData() {
        return data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public int compareTo(ReplayerDataRow o) {
        if (o == null) {
            throw new NullPointerException();
        } else if (this == o) {
            return 0;
        } else if (this.timestamp == o.getTimestamp()) {
            return 0;
        } else if (this.timestamp > o.timestamp) {
            return 1;
        } else {
            return -1;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof ReplayerDataRow) {
            ReplayerDataRow tmp = (ReplayerDataRow) o;
            return signalID == tmp.getSignalID() && data.equals(tmp.getData())
                    && timestamp == tmp.getTimestamp();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (int) ((timestamp + 1) * data.hashCode() * signalID);
    }
}

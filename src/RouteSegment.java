public class RouteSegment {
    private final Train train;
    private final Stop fromStop;
    private final Stop toStop;

    public RouteSegment(Train train, Stop fromStop, Stop toStop) {
        this.train = train;
        this.fromStop = fromStop;
        this.toStop = toStop;
    }

    public Stop getToStop() {
        return toStop;
    }

    @Override
    public String toString() {
        return String.format("Take Train %s from %s to %s", train.getTrainId(), fromStop.getName(), toStop.getName());
    }
}
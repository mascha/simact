package tri.lithium.meta.qss.core;

import tri.lithium.meta.qss.core.DoubleInport;
import tri.lithium.meta.qss.core.Block;

import java.util.ArrayList;
import java.util.List;

public class DoubleSink extends Block {

    public static final int STREAM_SIZE = 4 * 10000;

    double[] plotData = new double[STREAM_SIZE];
    double[] timeData = new double[STREAM_SIZE];

    public List<double[]> getDataStreams() {
        return dataStreams;
    }
    public List<double[]> getTimeStreams() {
        return timeStreams;
    }

    List<double[]> dataStreams = new ArrayList<double[]>();
    List<double[]> timeStreams = new ArrayList<double[]>();

    double time;

    public DoubleInport input = DoubleInport.Double("input", this);

    private int dataCount;

    public int getDataCount() {
        return dataCount;
    }

    @Override
    protected void deltaExternal(double elapsedTime) {

        if (dataCount + 1 > STREAM_SIZE) {
            plotData = new double[STREAM_SIZE];
            timeData = new double[STREAM_SIZE];

            dataStreams.add(plotData);
            timeStreams.add(timeData);

            dataCount = 0;
        }

        time += elapsedTime;

        plotData[dataCount] = input.receivePrimitive();
        timeData[dataCount] = time;

        dataCount++;

        passivate();
    }

    @Override
    protected void deltaInternal() {
        passivate();
    }

    @Override
    protected void outputFunction() {}

    public String toString() {
        return getName() + " = Sink(data = " + dataCount +")";
    }

    public DoubleSink(String name) {
        setName(name);
        dataStreams.add(plotData);
        timeStreams.add(timeData);
    }

    public DoubleSink() {
        this("SINK"+Math.random()*100000);
    }
}

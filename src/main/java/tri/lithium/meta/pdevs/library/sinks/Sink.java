/*
 * Copyright (C) Martin Schade 2015. All rights reserved.
 */
package tri.lithium.meta.pdevs.library.sinks;

import tri.lithium.meta.qss.core.DoubleSink;

import java.util.ArrayList;
import java.util.List;

public class Sink extends DoubleSink {

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

    public Inport<Number> input = new Inport<Number>("input", this);

    private int dataCount;

    @Override
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

        double val = input.receive().doubleValue();
        plotData[dataCount] = val <= 1 && val >= 0 ? 0 : val;
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
        return getName() + " = Sink("+ dataCount + " values)";
    }

    public Sink(String name) {
        setName(name);
        dataStreams.add(plotData);
        timeStreams.add(timeData);
    }

    public Sink() {
        setName("SINK"+hashCode());
    }
}

/**
 * Copyright (C) Martin Schade, 2015.
 * All rights reserved.
 */
package tri.lithium.meta.qss.core;

import tri.lithium.meta.pdevs.api.IInport;
import tri.lithium.meta.pdevs.core.Atomic;

import java.util.Objects;

public class DoubleInport implements IInport<Double> {

    final Atomic hostModel;

    private String name;

    public void setName(String newName) {
        this.name = newName;
    }

    public String getName() {
        return name;
    }

    double[] values;

    int curPos = -1;

    public final void clear() {
        curPos = -1;
    }

    public boolean hasInputs() {
        return curPos > -1;
    }

    @Override
    public void post(Double aDouble) {
        curPos++;
        values[curPos] = aDouble;
        hostModel.markAsInfluenced();
    }

    public final void postFast(double data) {
        curPos++;
        values[curPos] = data;
        hostModel.markAsInfluenced();
    }

    public final void postFast(double val1, double val2) {
        curPos++;
        values[curPos] = val1;
        curPos++;
        values[curPos] = val2;
        hostModel.markAsInfluenced();
    }

    public final void postFast(double val1, double val2, double val3) {
        curPos++;
        values[curPos] = val1;
        curPos++;
        values[curPos] = val2;
        curPos++;
        values[curPos] = val3;
        hostModel.markAsInfluenced();
    }

    @Override
    public Double receive() {
        if (curPos > -1) {
            return values[curPos--];
        } else return (double) 0;
    }

    public final double receivePrimitive() {
        if (curPos > -1) {
            return values[curPos--];
        } else return 0;
    }

    DoubleInport(Block hostModel) {
        Objects.requireNonNull(hostModel, "Port has no viable model to attach to!");
        this.hostModel = hostModel;
        values = new double[4];
    }


    public static DoubleInport Double(String name, Block qss) {
        DoubleInport inport = new DoubleInport(qss);
        qss.getPrimitivePorts().add(inport);
        inport.setName(name);
        return inport;
    }
}

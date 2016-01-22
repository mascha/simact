/*
 * Copyright (C) Martin Schade 2015. All rights reserved.
 */
package tri.lithium.meta.pdevs.library.sources;

import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import tri.lithium.meta.pdevs.core.Atomic;

import java.util.Objects;

/**
 * Arrival generator.
 */
public class Arrival<E> extends Atomic {

    public final Outport<E> output = new Outport<E>("output", this);

    protected InterarrivalTime interarrivalTime;
    protected EntityGenerator<E> entityProvider;

    protected double time;
    private int entities;

    @Override
    protected void deltaExternal(double elapsedTime) {
        time += elapsedTime;
        timeout(timeout - elapsedTime);
    }

    @Override
    protected void deltaInternal() {
        time   += timeout;
        timeout = interarrivalTime.calculate();
    }

    @Override
    protected void outputFunction() {
        double currentTime = time + timeout;
        E entity = entityProvider.create(currentTime);
        if (entity != null) {
            //System.out.println(getName() + " " + entities++);
            output.send(entity);
        }
    }

    @Override
    public String toString() {
        return getName() + " : Arrival";
    }

    /**
     * Periodic arrival with custom entity creation routine and interarrival time.
     * @param name
     * @param entityType
     */
    public Arrival(String name, Class<? extends E> entityType, InterarrivalTime interarrivalTime) {
        this(name, new EntityGenerator<E>() {
            public E create(double time) {
                E entity = null;
                try {
                    entity = entityType.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return entity;
            }
        }, interarrivalTime);
    }

    /**
     * Periodic arrival with custom entity creation routine.
     * @param name
     * @param entityType
     * @param period
     */
    public Arrival(String name, Class<? extends E> entityType, double period) {
        this(name, new EntityGenerator<E>() {
            public E create(double time) {
                E entity = null;
                try {
                    entity = entityType.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return entity;
            }
        }, new InterarrivalTime() {
            public double calculate() {
                return period;
            }
        });
    }

    /**
     * Deterministic arrival with start time.
     * @param name
     * @param entityType
     * @param period
     * @param start
     */
    public Arrival(String name, Class<? extends E> entityType, double period, double start) {
        this(name, entityType, period);
        timeout(start);
    }

    /**
     * Arrival generator with custom entity and time specification.
     * @param name Name of the entity.
     * @param provider Generator routine for constructing arrival classes.
     * @param interarrivalTime Interarrival time calculator. Needs to be non-null, but can be null if
     *                         the calculator is later supplied in the overriding constructor. The initial
     *                         model timeout must then be calculated in the inheriting class.
     */
    public Arrival(String name, EntityGenerator<E> provider, InterarrivalTime interarrivalTime) {
        Objects.requireNonNull(provider, "An entity generator must be specifyied");

        this.entityProvider   = provider;
        this.interarrivalTime = interarrivalTime;
        this.setName(name);

        if (interarrivalTime != null)
            timeout(interarrivalTime.calculate());
    }

    /**
     * Arrival generator with custom entity and time specification and a custom starting time.
     * @param name
     * @param provider
     */
    public Arrival(String name, EntityGenerator<E> provider, InterarrivalTime interarrivalTime, double startingTime) {
        this(name, provider, interarrivalTime);
        timeout(startingTime);
    }


    /**
     * Models a thinned poisson arrival process using a maximum arrival rate.
     * It generates lots of potential arrivals and only actually produces in
     * poportion to the actual rate
     * @param <>
     */
    public static class NonStationary<E> extends Arrival<E> {

        public final Inport<Double> rate = new Inport<Double>("arrivalRate", this);

        private final IntensityFunction intensity;
        private final UniformRealDistribution u;
        private double clock;
        private double arrivalRate;
        private final double maximumArrivalRate;

        private double getArrivalRate(double time) {
            if (intensity == null)
                return arrivalRate;
            else
                return intensity.calculateArrivalRate(time);
        }

        @Override
        protected final void deltaExternal(double elapsedTime) {
            clock += elapsedTime;
            timeout(timeout - elapsedTime);
            arrivalRate = rate.receive();
        }

        @Override
        protected void deltaInternal() {
            clock  += timeout;
            timeout = interarrivalTime.calculate();
        }

        @Override
        protected void outputFunction() {
            double currentTime = clock + timeout;
            double decision    = u.sample();
            double arrivalRate = getArrivalRate(currentTime);
            double probability = arrivalRate / maximumArrivalRate;

            if (probability > 1)
                throw new IllegalStateException(getFullName() + " : Arrival rate was greater than it's expected maximum (was " + arrivalRate + ", maximum " + maximumArrivalRate);

            /* Check whether this is just a potential arrival */
            if (decision <= (probability)) {
                E entity = entityProvider.create(currentTime);
                if (entity != null) {
                    output.send(entity);
                }
            }
        }

        @Override
        public String toString() {
            return getName() + " : Arrival ( dynamic, rate = " + getArrivalRate(clock) +")";
        }

        /**
         *
         * @param name
         * @param generator
         * @param intensity
         * @param maximumArrivalRate
         */
        public NonStationary(String name, EntityGenerator<E> generator, IntensityFunction intensity, double maximumArrivalRate) {
            super(name, generator, null);

            if (maximumArrivalRate <= 0)
                throw new NotStrictlyPositiveException(maximumArrivalRate);

            this.maximumArrivalRate = maximumArrivalRate;
            this.intensity          = intensity;

            u = new UniformRealDistribution();

            /* Always generate the maximum number of events */
            this.interarrivalTime   = new InterarrivalTime() {
                public double calculate() {
                    return Math.log(u.sample()) / - maximumArrivalRate;
                }};

        }
    }

    public static class Initial<E> extends Arrival<E> {
        private final int number;

        boolean sent;

        public Initial(String name, Class<? extends E> entityType, int numberOfEntities,  double start) {
            super(name, entityType, Double.POSITIVE_INFINITY, start);
            this.number = numberOfEntities;
            timeout(start);
        }

        @Override
        protected void outputFunction() {
            if (sent) return;
            double currentTime = time + timeout;
            for (int i = 0; i < number; i++) {
                E entity = entityProvider.create(currentTime);
                if (entity != null) {
                    output.send(entity);
                }
            }
        }

        @Override
        public String toString() {
            return getName() + " : Arrival (initial, "+ number+")";
        }
    }

    /**
     *
     * @param <E>
     */
    public interface EntityGenerator<E> {
        E create(double time);
    }

    /**
     *
     */
    public interface InterarrivalTime {
        double calculate();
    }

    /**
     *
     */
    public interface IntensityFunction {
        /**
         * Calculate the time-dependant arrival time.
         * @param time Strictly positive time.
         * @return the arrival rate.
         */
        double calculateArrivalRate(double time);
    }

}




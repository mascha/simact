/*
 * Copyright (C) Martin Schade 2015. All rights reserved. No commercial and non-commerical usage.
 */
package tri.lithium.meta.pdevs.library.queue;

import tri.lithium.meta.pdevs.core.Atomic;
import tri.lithium.meta.pdevs.core.Link;
import tri.lithium.meta.pdevs.library.queue.Join.Event;

import java.util.*;

import static tri.lithium.meta.pdevs.library.queue.Queue.Response.FAILED;
import static tri.lithium.meta.pdevs.library.queue.Queue.Response.RESERVED;

/**
 * A simple queueing model.
 */
public class Queue<E> extends Atomic {

    private int capacity = Integer.MAX_VALUE;

    private final Ordering ordering;
    private java.util.Queue<E> queue;

    public final Inport<E> input              = new Inport<E>("input", this);
    public final Outport<Integer> queueLength = new Outport<Integer>("queueLength", this);
    public final Outport<E> overflow          = new Outport<E>("overflow", this);

    private double clock;
    private Deque<E> overflowQueue = new ArrayDeque<>();

    @Override
    protected void deltaConfluent() {
        deltaExternal(0.0);
    }

    @Override
    protected void deltaExternal(double elapsedTime) {
        clock += elapsedTime;

        if (input.hasInputs()) {
            activate();

            while (input.hasInputs())
                if (queue.size() + 1 <= capacity)
                    queue.add(input.receive());
                else
                    overflowQueue.add(input.receive());
        }

        channels.forEach(ch ->
                ch.eventIn.forAll(command -> {
                    switch (command) {
                        case RESERVE: ch.handleReserve();
                            break;
                        case CANCEL:
                                while (ch.reserved.size() > 0)
                                    queue.add(ch.reserved.removeFirst());
                                ch.hasFailed = false;
                                ch.hasReserved = false;
                            break;
                        case FIRE: ch.handleFiring();
                            break;
                    }}));
        activate();
    }

    @Override
    protected void deltaInternal() {
        clock += timeout;
        passivate();
    }

    @Override
    protected void outputFunction() {
        while (!overflowQueue.isEmpty())
            overflow.send(overflowQueue.removeFirst());

        queueLength.send(queue.size());
        channels.forEach(Channel::handleSending);
    }

    @Override
    public String toString() {
        if (queue.size() < 1)
            return getName() + " : Queue (" + ordering + ", empty)";
        else if (queue.size() < 3)
            return getName() + " : Queue (" + ordering + ", entities = "+ queue +")";
        else
            return getName() + " : Queue (" + ordering + ", entities = "+ queue.size() +")";
    }

    private List<Channel<E>> channels = new ArrayList<>();

    private Map<Join<E>, Channel<E>> channelMap = new HashMap<>();

    public final Channel<E> getChannelFor(Join<E> source) {
        return channelMap.get(source);
    }

    public void addTarget(Join<E> join) {
        Channel<E> channel = new Channel<E>(this, join);
        channels.add(channel);
        channelMap.put(join, channel);
    }

    public static final class Channel<E> {

        Deque<E> reserved = new ArrayDeque<>();
        Deque<E> sending  = new ArrayDeque<>();

        Outport<E> itemOut;
        Outport<Response> responseOut;
        Inport<Event> eventIn;

        boolean hasReserved;
        boolean hasFailed;

        private final Join<E> target;
        private final Queue<E> parent;

        public Channel(Queue<E> parent, Join<E> target) {
            this.parent = parent;
            this.target = target;

            itemOut     = new Outport<E>(target.getName(), parent);
            responseOut = new Outport<Response>(target.getName(), parent);
            eventIn     = new Inport<Event>(target.getName(), parent);

            Join.Channel<E> remote = target.getChannelFor(parent);

            if (remote != null) {
                Link.connect(itemOut, remote.itemIn);
                Link.connect(responseOut, remote.responseIn);
                Link.connect(remote.eventOut, eventIn);
                Link.connect(parent.queueLength, remote.lengthIn);
            }
        }

        public Queue<E> getParent() {
            return parent;
        }

        public void handleReserve() {
            if (parent.queue.size() > 0) {
                //System.out.println("\t\t"+parent.getFullName()+" : Reserved token for " + target.getFullName());
                reserved.add(parent.queue.remove());
                hasFailed   = false;
                hasReserved = true;
            }
            else {
                //System.out.println("\t\t"+parent.getFullName()+" : Not enough token for " + target.getFullName());
                hasReserved = false;
                hasFailed   = true;
            }

        }

        public void handleFiring() {
            if (!hasReserved) {
                //System.out.println("\t\t" + parent.getFullName() + " : Firing requested, but no reserved tokens!");
                hasFailed = true;
            } else {
                //System.out.println("\t\t" + parent.getFullName() + " : Handle firing");
                while (reserved.size() > 0)
                    sending.add(reserved.removeFirst());
            }
        }

        public void handleSending() {
            if (hasReserved)
                if (sending.isEmpty())
                    responseOut.send(RESERVED);
                else while (sending.size() > 0) itemOut.send(sending.removeFirst());
            else {
                responseOut.send(FAILED);
            }
        }
    }

    /**
     * Create a queue using the given ordering and the comparator
     * for the priority ordering to use.
     * @param name
     * @param type
     * @param comparator
     */
    public Queue(String name, Ordering type, Comparator<E> comparator) {
        this.setName(name);
        this.ordering = type;

        switch (type) {
            case FIFO: queue = new ArrayDeque<>();
                break;
            case LIFO: queue = Collections.asLifoQueue(new ArrayDeque<E>());
                break;
            case PRIORITY:
                if (comparator == null) {
                    queue = new PriorityQueue<>();
                    System.err.println(getFullName() + "Warning: No priority comparator given - using default");
                }
                else
                    queue = new PriorityQueue<>(comparator);
                break;

            default:
                throw new UnsupportedOperationException("Random ordering is not supported at the moment");
        }

        passivate();
    }

    /**
     * Create a queue using the given ordering.
     * @param name
     * @param type
     */
    public Queue(String name, Ordering type) {
        this(name, type, null);
    }

    /**
     * Create a simple FIFO queue.
     * @param name
     */
    public Queue(String name) {
        this(name, Ordering.FIFO);
    }


    public enum Ordering {
        FIFO,
        LIFO,
        PRIORITY,
        RANDOM,
    }

    public enum Response {
        RESERVED,
        FAILED
    }
}

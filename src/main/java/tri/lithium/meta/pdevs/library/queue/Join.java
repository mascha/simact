/*
 * Copyright (C) Martin Schade 2015. All rights reserved.
 */

package tri.lithium.meta.pdevs.library.queue;

import tri.lithium.meta.pdevs.core.Atomic;
import tri.lithium.meta.pdevs.core.Link;
import tri.lithium.meta.pdevs.library.queue.Queue.Response;

import java.util.*;

import static tri.lithium.meta.pdevs.library.queue.Join.Event.CANCEL;
import static tri.lithium.meta.pdevs.library.queue.Queue.Response.FAILED;
import static tri.lithium.meta.pdevs.library.queue.Join.Event.FIRE;
import static tri.lithium.meta.pdevs.library.queue.Join.Event.RESERVE;
import static tri.lithium.meta.pdevs.library.queue.Join.Phase.*;

public class Join<E> extends Atomic {

    Deque<E> sending = new ArrayDeque<E>();

    public final Outport<E> output = new Outport<E>("outport", this);

    private Phase phase = INACTIVE;

    @Override
    protected void deltaConfluent() {
        deltaExternal(0.0);
    }

    @Override
    protected void deltaExternal(double elapsedTime) {
        switch (phase) {
            case INACTIVE:
                channels.forEach(Channel::receiveQueueLength);
                //printQueueSizes();
                if (transitionEnabled())
                    gotoPhase(RESERVING);
                else
                    passivate();

                break;

            case RESERVING:
                channels.forEach(Channel::receiveResponse);

                if (noneFailed())
                    gotoPhase(FIRING);
                else {
                    gotoPhase(INACTIVE);
                    passivate();
                  //  System.out.println(getName() + " denied!");
                }

                break;

            case FIRING:
                channels.forEach(Channel::receiveEntites);
                gotoPhase(SENDING);
                break;

            case SENDING:
                break;
        }
    }

    private void printQueueSizes() {
        System.out.print("\t\t[");
        for (int i = 0; i < channels.size(); i++) {
            System.out.print(channels.get(i).queueLength + (i == channels.size()-1 ? "" : ", "));
        }
        System.out.println("]");
    }

    private boolean noneFailed() {
        for (int i = 0; i < channels.size(); i++)
            if (channels.get(i).failed) return false;
        return true;
    }

    private void gotoPhase(Phase newPhase) {
        activate();
        phase = newPhase;
    }

    private void resetFailed() {
        for (int i = 0; i < channels.size(); i++)
            channels.get(i).failed = true;
    }

    private boolean transitionEnabled() {
        for (int i = 0; i < channels.size(); i++)
            if (channels.get(i).queueLength < 1) return false;
        return true;
    }


    @Override
    protected void deltaInternal() {
        passivate();
        if (phase == INACTIVE) {
            resetFailed();
            passivate();
        }
        else if (phase == SENDING) {
            phase = INACTIVE;
            resetFailed();
        }
    }

    @Override
    protected void outputFunction() {
        switch (phase) {
            case INACTIVE:
                if (!noneFailed())
                    channels.forEach(ch -> ch.eventOut.send(CANCEL));
                break;

            case RESERVING:
                channels.forEach(channel -> channel.eventOut.send(RESERVE));
                break;

            case FIRING:
                channels.forEach(channel -> channel.eventOut.send(FIRE));
                break;

            case SENDING:
                while (sending.size() > 0)
                    output.send(sending.removeFirst());
                break;
        }

    }

    private List<Channel<E>> channels = new ArrayList<>();

    private Map<Queue<E>, Channel<E>> channelMap = new HashMap<>();

    public final Channel<E> getChannelFor(tri.lithium.meta.pdevs.library.queue.Queue<E> source) {
        return channelMap.get(source);
    }

    public void addSource(Queue<E> source) {
        source.addTarget(this);
        Channel<E> channel = new Channel<E>(this, source);
        channels.add(channel);
        channelMap.put(source, channel);
    }


    public static final class Channel<E> {
        boolean mainChannel;

        Inport<E> itemIn;
        Inport<Response> responseIn;
        Outport<Event> eventOut;
        Inport<Integer> lengthIn;

        int queueLength;
        boolean failed;

        private final Join<E> parent;
        private final Queue<E> source;

        private static boolean debug;

        public Channel(Join<E> parent, Queue<E> source) {
            this.source = source;
            this.parent = parent;

            itemIn     = new Inport<E>(source.getName(), parent);
            responseIn = new Inport<Response>(source.getName(), parent);
            eventOut   = new Outport<Event>(source.getName(), parent);
            lengthIn   = new Inport<Integer>(source.getName(), parent);

            tri.lithium.meta.pdevs.library.queue.Queue.Channel<E> remote = source.getChannelFor(parent);
            if (remote != null) {
                Link.connect(remote.itemOut, itemIn);
                Link.connect(remote.responseOut, responseIn);
                Link.connect(eventOut, remote.eventIn);
                Link.connect(remote.getParent().queueLength, lengthIn);
               // System.out.println("Linked "+ parent.getName() + " to " + remote.getParent().getFullName());
            }
        }

        public Join<E> getParent() {
            return parent;
        }

        public void receiveQueueLength() {
            if (lengthIn.hasInputs()) {
                queueLength = lengthIn.receive();
               // System.out.println("\t\tReceived queue length " + queueLength + " from " + source.getFullName());
            } else {
               // System.out.println("\t\tReceived queue length ? from " + source.getFullName());
            }
        }

        public void receiveResponse() {
            if (responseIn.hasInputs()) {
                Response response = responseIn.receive();
                //System.out.println("\t\tReceived "+response + " from "  + this.source.getFullName());
                failed = response == FAILED || response == null;
            } else {
                failed = true; // Not enough token -> this has to be zero!
                queueLength = 0;
                //if (debug) System.out.println("\t\tNo response received from " + this.source.getFullName());
            }
        }

        public void receiveEntites() {
            itemIn.forAll(item -> {
                parent.sending.add(item);
                queueLength--;
            });
        }
    }

    @Override
    public String toString() {
        return getName() + " : Sync (" + phase + ")";
    }

    public Join(String name) {
        this.setName(name);
        passivate();
    }


    public enum Phase {
        INACTIVE,
        RESERVING,
        FIRING,
        SENDING,
    }

    public enum Event {
        RESERVE,
        CANCEL,
        FIRE
    }
}

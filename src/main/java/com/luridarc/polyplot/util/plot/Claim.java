package com.luridarc.polyplot.util.plot;

import java.util.Optional;

public class Claim 
{    
    public enum State {
        OWNER, 
        CLAIMED, 
        UNCLAIMED
    }

    public State state;
    public Optional<Integer> plot_index;

    public Claim(State state, Optional<Integer> plot_index) {
        this.state = state;
        this.plot_index = plot_index;
    }

    public Optional<Integer> getPlot_index() {
        return plot_index;
    }

    public void setPlot_index(Optional<Integer> plot_index) {
        this.plot_index = plot_index;
    }
}


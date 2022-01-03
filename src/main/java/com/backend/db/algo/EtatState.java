package com.backend.db.algo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class EtatState {
    protected int numstate;
    protected boolean sfinal;
    protected HashMap<Character, Integer> sousstate;

    public EtatState() {
        this.sousstate = new HashMap<>();
        this.sfinal = false;
    }


    public int getNumstate() {
        return numstate;
    }

    public void setNumstate(int numstate) {
        this.numstate = numstate;
    }

    public boolean getSfinal() { return this.sfinal; }

    public boolean isSfinal() {
        return sfinal;
    }

    public void setSfinal(boolean sfinal) {
        this.sfinal = sfinal;
    }

    public HashMap<Character, Integer> getSousstate() {
        return sousstate;
    }

    public void setSousstate(HashMap<Character, Integer> sousstate) {
        this.sousstate = sousstate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EtatState etatState = (EtatState) o;
        return numstate == etatState.numstate && sfinal == etatState.sfinal && sousstate.equals(etatState.sousstate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numstate, sfinal, sousstate);
    }

    @Override
    public String toString() {
        return "EtatState{" +
                "numstate=" + numstate +
                ", sfinal=" + sfinal +
                ", sousstate=" + sousstate +
                '}';
    }
    public void addSousState(Character character, Integer num) {
        if (this.sousstate.get(character) == null) {
            this.sousstate.put(character, num);
        } else {
            System.out.println("Error");
        }
    }
}

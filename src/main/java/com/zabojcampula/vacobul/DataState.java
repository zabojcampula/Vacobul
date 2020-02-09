package com.zabojcampula.vacobul;

public class DataState {

    private int numElementsAdded = 0;
    private int numElemetsExamined = 0;

    public boolean isDirty() {
        return numElemetsExamined + numElementsAdded> 0;
    }

    public boolean isWorthWrite() {
        return numElemetsExamined > 100 || numElementsAdded > 2;
    }

    public void reset() {
        numElementsAdded = 0;
        numElemetsExamined = 0;
    }

    public void elementAdded() {
        numElementsAdded++;
    }


    public void elementExamined() {
        numElemetsExamined++;
    }

    @Override
    public String toString() {
        return String.format("added:%-3d  examined:%-3d", numElementsAdded, numElemetsExamined);
    }
}

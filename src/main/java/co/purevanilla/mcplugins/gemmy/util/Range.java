package co.purevanilla.mcplugins.gemmy.util;

import java.util.Random;

public class Range {

    public int min;
    public int max;

    public Range(int min, int max){
        this.min=min;
        this.max=max;
    }

    public int getMax() {
        return max;
    }

    public int getMin() {
        return min;
    }

    public int getAmount(){
        Random rand = new Random();
        return rand.nextInt((this.getMax() - this.getMin()) + 1) + this.getMin();
    }
}

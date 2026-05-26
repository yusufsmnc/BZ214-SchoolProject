package bm.erciyes.robotvacuumsim.model;

import bm.erciyes.robotvacuumsim.util.DirtType;

public class Liquid extends Dirt{
    public Liquid(){
        this.type = DirtType.LIQUID;
        this.cleaningTime = 3;
        this.batteryCost = 3;
        this.remainingTime = 3;
    }
    @Override
    public void clean() {
        if(remainingTime > 0)
            remainingTime -=1;
    }
}

package bm.erciyes.robotvacuumsim.model;

import bm.erciyes.robotvacuumsim.util.DirtType;

public class Stain extends Dirt{
    public Stain(){
        this.type = DirtType.STAIN;
        this.cleaningTime = 5;
        this.batteryCost = 5;
        this.remainingTime = 5;
    }
    @Override
    public void clean() {
        if(remainingTime > 0)
            remainingTime -=1;
    }
}

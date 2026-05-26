package bm.erciyes.robotvacuumsim.model;

public class ChargingStation {
    private int x;
    private int y;

    public ChargingStation(int x, int y){
        // statiton koordinatları belirleniyor
        this.x = x;
        this.y = y;

    }
    public int getX() {return x;}
    public int getY() {return y;}
}

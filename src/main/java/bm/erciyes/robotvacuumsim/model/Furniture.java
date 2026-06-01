package bm.erciyes.robotvacuumsim.model;

public class Furniture {
    private int x;
    private int y;
    private int width;  // width ve height kaç hücre kapladığını hesaplamak için alındı (Şuan 2x2 lik ekliyoz ilerde değişebilir)
    private int height;

    public Furniture(int x, int y, int width, int height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    public int getX() {return x;}
    public int getY() {return y;}
    public int getHeight() {return height;}
    public int getWidth() {return width;}
}

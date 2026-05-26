package bm.erciyes.robotvacuumsim.model;

public class Furniture {
    private int x;
    private int y;
    private int width;  // width ve height kaç hücre kapladığını hesaplamak için alındı
    private int height;
    private String name;

    public Furniture(int x, int y, int width, int height, String name){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.name = name;
    }
    public int getX() {return x;}
    public int getY() {return y;}
    public int getHeight() {return height;}
    public int getWidth() {return width;}
    public String getName() {return name;}
}

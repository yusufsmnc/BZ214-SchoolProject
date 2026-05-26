package bm.erciyes.robotvacuumsim.model;

public class Cell {

    // Bu hücrenin oda içindeki koordinatları
    private int x;
    private int y;

    // Duvar veya mobilya varsa true — robot buradan geçemez
    private boolean isObstacle;

    // Robot buradan geçtiyse true — yol çizimi ve istatistik için
    private boolean isVisited;

    // Hücredeki kir — temizse null
    private Dirt dirt;

    // Şarj istasyonu mu?
    private boolean isCharger;

    // Constructor — hücre oluşturulunca koordinatları ver
    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
        this.isObstacle = false;
        this.isVisited = false;
        this.dirt = null;
        this.isCharger = false;
    }

    // Kir var mı? — dirt null değilse kir var demektir
    public boolean hasDirt() {
        return dirt != null;
    }

    // Kiri temizle — null yap
    public void removeDirt() {
        this.dirt = null;
    }

    // Getterlar ve Setterlar
    public int getX() { return x; }
    public int getY() { return y; }

    public boolean isObstacle() { return isObstacle; }
    public void setObstacle(boolean obstacle) {
        this.isObstacle = obstacle;
    }

    public boolean isVisited() { return isVisited; }
    public void setVisited(boolean visited) {
        this.isVisited = visited;
    }

    public Dirt getDirt() { return dirt; }
    public void setDirt(Dirt dirt) { this.dirt = dirt; }

    public boolean isCharger() { return isCharger; }
    public void setCharger(boolean charger) { this.isCharger = charger; }
}
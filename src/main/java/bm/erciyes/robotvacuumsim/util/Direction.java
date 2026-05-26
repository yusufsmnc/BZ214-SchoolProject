package bm.erciyes.robotvacuumsim.util;

public enum Direction {
    NORTH(0, -1),
    SOUTH(0,  1),
    EAST ( 1,  0),
    WEST (-1,  0);

    private final int dx;
    private final int dy;

    // constructor — her yön için dx ve dy atanıyor
    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public int getDx() { return dx; }
    public int getDy() { return dy; }
}
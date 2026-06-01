package bm.erciyes.robotvacuumsim.model;

import bm.erciyes.robotvacuumsim.util.DirtType;

import java.util.*;

public class Room {
    private int width;
    private int height;
    private Cell[][] grid;
    private ChargingStation station;
    private List<Furniture> furnitures;

    public Room(int width, int height){
        this.width = width;
        this.height = height;
        this.furnitures = new ArrayList<>();

        // grid oluştur — her hücreyi başlat
        grid = new Cell[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[x][y] = new Cell(x, y);
            }
        }

        // şarj istasyonu sol üst köşede
        station = new ChargingStation(0, 0);
        grid[0][0].setCharger(true);
    }

    public boolean isInBounds(int x, int y){
        /*
         koordinat sınır kontrolü. Her hareket ve ekleme işleminde kullanılır. Sınır dışı erişimi önler
        */

        // sınırlar içindemi
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public boolean isObstacle(int x, int y){
        /*
        Önce isInBounds kontrol eder, sonra grid[x][y].isObstacle() döndürür. Sınır dışı koordinat otomatik obstacle sayılır
        */

        if(!isInBounds(x,y))
            return true;
        return grid[x][y].isObstacle(); // false döner
    }

    public void addFurniture(int x, int y, int width, int height){
        this.furnitures.add(new Furniture(x,y,width,height));

        // engelin koordinatlarına bakılır , sınırlar içidemi diye
        for (int i = x; i < x + width; i++) {
            for (int j = y; j < y + height; j++) {
                if (isInBounds(i,j))
                    grid[i][j].setObstacle(true);
            }
        }
    }

    public void addDirt(int x, int y, DirtType type) {
        // sınır dışıysa veya engel olan hücreye kir eklenemez
        if (!isInBounds(x, y) || isObstacle(x, y))
            return; // metotdan hemen çıksın diye return konuldu

        // kir türüne göre doğru alt sınıf oluşturuluyor
        // polimorfizm — Dirt tipinde tutuyoruz ama Dust/Liquid/Stain olabilir
        Dirt dirt = switch (type) {
            case DUST   -> new Dust();
            case LIQUID -> new Liquid();
            case STAIN  -> new Stain();
        };

        // oluşturulan kir hücreye atandı
        grid[x][y].setDirt(dirt);
    }

    //  Bütün hücreleri gezerek, engel olmayan hücreleri sayar
    public int getTotalCleanableCells() {
        int count = 0;
        List<int[]> unreachable = getUnreachableCells();

        // unreachable hücreleri set'e ekle — hızlı arama için
        Set<String> unreachableSet = new HashSet<>();
        for (int[] uc : unreachable) {
            unreachableSet.add(uc[0] + "," + uc[1]);
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (!isObstacle(x, y) && !unreachableSet.contains(x + "," + y))
                    count++;
            }
        }
        return count;
    }
    // tam temizlenmiş hücreleri sayar — kiri olmayan ve ziyaret edilmiş
    public int getFullyCleanedCells() {
        int count = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (grid[x][y].isVisited() && !grid[x][y].hasDirt()) count++;
            }
        }
        return count;
    }

    // Kirli hücreleri sayar (istastik için)
    public int getDirtyCells() {
        int count = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // hücrede kir varsa say
                if (grid[x][y].hasDirt()) count++;
            }
        }
        // kirli hücre sayısı döndürülüyor
        return count;
    }

    public List<int[]> getUnreachableCells() {
        /*
        BFS algoritması
        Şarj istasyonundan başlar
        4 yönde genişler — obstacle olmayanları işaretler
        Sonunda işaretlenmemiş obstacle olmayan hücreler → unreachable
        getTotalCleanableCells() ve RoomPane.update() içinde kullanılır
         */

        boolean[][] reachable = new boolean[width][height];
        Queue<int[]> queue = new LinkedList<>();

        // şarj istasyonundan BFS başlat
        int startX = station.getX();
        int startY = station.getY();
        queue.add(new int[]{startX, startY});
        reachable[startX][startY] = true;

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int cx = current[0];
            int cy = current[1];

            for (int[] dir : new int[][]{{0,1},{0,-1},{1,0},{-1,0}}) {
                int nx = cx + dir[0];
                int ny = cy + dir[1];
                if (isInBounds(nx, ny) && !isObstacle(nx, ny) && !reachable[nx][ny]) {
                    reachable[nx][ny] = true;
                    queue.add(new int[]{nx, ny});
                }
            }
        }

        // ulaşılamayan engel olmayan hücreler
        List<int[]> unreachable = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (!isObstacle(x, y) && !reachable[x][y]) {
                    unreachable.add(new int[]{x, y});
                }
            }
        }
        return unreachable;
    }

    public Cell getCell(int x, int y) {return grid[x][y];}

    public int getWidth() {return this.width;}
    public int getHeight() {return this.height;}

    public ChargingStation getStation() {return station;}
    public List<Furniture> getFurnitures() {return furnitures;}
}

package bm.erciyes.robotvacuumsim.controller;

import bm.erciyes.robotvacuumsim.model.Room;
import bm.erciyes.robotvacuumsim.util.Direction;

import java.util.*;

public class PathFinder {
    private Room room;

    public PathFinder(Room room){
        this.room = room;
    }

    public List<Direction> findPath(int startX, int startY, int targetX, int targetY){
        // 1. Kuyruk — sıradaki ziyaret edilecek noktalar
        Queue<int[]> queue = new LinkedList<>();

        // 2. Ziyaret edilenler — aynı yere tekrar gitme
        boolean[][] visited = new boolean[room.getWidth()][room.getHeight()];

        // 3. Nereden geldik — yolu geri takip etmek için
        Direction[][] cameFrom = new Direction[room.getWidth()][room.getHeight()];

        // başlangıç noktasını kuyruğa ekle
        queue.add(new int[]{startX, startY});
        // başlangıç noktası ziyaret edildi
        visited[startX][startY] = true;

        // kuyruk boşalana kadar devam et
        while (!queue.isEmpty()) {
            // kuyruktan bir nokta al
            int[] current = queue.poll();
            int cx = current[0];
            int cy = current[1];

            // hedefe ulaştık mı?
            if (cx == targetX && cy == targetY) {
                // yolu geri takip et ve döndür
                return reconstructPath(cameFrom, startX, startY, targetX, targetY);
            }

            // tüm yönleri dene
            for (Direction dir : Direction.values()) {
                int nx = cx + dir.getDx();
                int ny = cy + dir.getDy();

                // engel değilse ve ziyaret edilmemişse kuyruğa ekle
                if (!room.isObstacle(nx, ny) && !visited[nx][ny]) {
                    visited[nx][ny] = true;
                    cameFrom[nx][ny] = dir;
                    queue.add(new int[]{nx, ny});
                }
            }
        }
        // yol bulunamadı
        return new ArrayList<>();
    }

    private List<Direction> reconstructPath(Direction[][] cameFrom, int startX, int startY, int targetX, int targetY) {
        List<Direction> path = new ArrayList<>();
        int cx = targetX;
        int cy = targetY;

        // hedeften başlangıca geri git
        while (cx != startX || cy != startY) {
            Direction dir = cameFrom[cx][cy];
            path.add(dir);
            // geri git — getDx() ve getDy() tersini al
            cx -= dir.getDx();
            cy -= dir.getDy();
        }

        // yol tersine — başlangıçtan hedefe çevir
        Collections.reverse(path);
        return path;
    }
}

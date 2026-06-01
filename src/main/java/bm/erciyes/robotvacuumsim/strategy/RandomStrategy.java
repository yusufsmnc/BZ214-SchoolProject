package bm.erciyes.robotvacuumsim.strategy;

import bm.erciyes.robotvacuumsim.model.Robot;
import bm.erciyes.robotvacuumsim.model.Room;
import bm.erciyes.robotvacuumsim.util.Direction;

import java.util.*;

public class RandomStrategy implements CleaningStrategy {

    private final Random random = new Random();

    @Override
    public Direction nextMove(Robot robot, Room room) {
        Direction[] directions = Direction.values();

        List<Direction> unvisited = new ArrayList<>();
        List<Direction> visited = new ArrayList<>();

        for (Direction dir : directions) {
            int newX = robot.getX() + dir.getDx();
            int newY = robot.getY() + dir.getDy();

            if (room.isObstacle(newX, newY)) continue;
            if (room.getCell(newX, newY).isCharger() && robot.getBat().getLevel() > 30) continue;

            if (!room.getCell(newX, newY).isVisited() || room.getCell(newX, newY).hasDirt()) {
                unvisited.add(dir); // kirli hücreler de unvisited sayılsın
            } else {
                visited.add(dir);
            }
        }

        // komşuda ziyaret edilmemiş varsa direkt git
        if (!unvisited.isEmpty()) {
            return unvisited.get(random.nextInt(unvisited.size()));
        }

        // komşuda yoksa BFS ile en yakın ziyaret edilmemişi bul
        Direction bfsDir = findNearestUnvisited(robot, room);
        if (bfsDir != null) return bfsDir;

        // tüm oda ziyaret edildiyse ziyaret edilmişten seç
        if (!visited.isEmpty()) {
            return visited.get(random.nextInt(visited.size()));
        }

        return robot.getDir();
    }

    // BFS ile en yakın ziyaret edilmemiş hücreye giden ilk adımı döndür
    private Direction findNearestUnvisited(Robot robot, Room room) {
        Queue<int[]> queue = new LinkedList<>();
        boolean[][] seen = new boolean[room.getWidth()][room.getHeight()];
        Direction[][] firstDir = new Direction[room.getWidth()][room.getHeight()];

        int startX = robot.getX();
        int startY = robot.getY();

        queue.add(new int[]{startX, startY});
        seen[startX][startY] = true;

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int cx = current[0];
            int cy = current[1];

            // ziyaret edilmemiş hücre bulundu
            if ((!room.getCell(cx, cy).isVisited() || room.getCell(cx, cy).hasDirt()) && (cx != startX || cy != startY)) {
                return firstDir[cx][cy];
            }

            for (Direction dir : Direction.values()) {
                int nx = cx + dir.getDx();
                int ny = cy + dir.getDy();

                if (!room.isInBounds(nx, ny)) continue;
                if (room.isObstacle(nx, ny)) continue;
                if (seen[nx][ny]) continue;

                seen[nx][ny] = true;
                // başlangıç noktasından ilk adımı kaydet
                firstDir[nx][ny] = (cx == startX && cy == startY) ? dir : firstDir[cx][cy];
                queue.add(new int[]{nx, ny});
            }
        }

        return null; // tüm oda ziyaret edildi
    }
}
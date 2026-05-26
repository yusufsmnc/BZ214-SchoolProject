package bm.erciyes.robotvacuumsim.strategy;

import bm.erciyes.robotvacuumsim.model.Robot;
import bm.erciyes.robotvacuumsim.model.Room;
import bm.erciyes.robotvacuumsim.util.Direction;

import java.util.*;

public class WallFollowStrategy implements CleaningStrategy {

    private List<Direction> currentPath = new ArrayList<>();

    @Override
    public Direction nextMove(Robot robot, Room room) {

        // mevcut yol varsa takip et
        if (!currentPath.isEmpty()) {
            Direction next = currentPath.remove(0);
            int nx = robot.getX() + next.getDx();
            int ny = robot.getY() + next.getDy();
            if (!room.isObstacle(nx, ny)) return next;
            else currentPath.clear();
        }

        // frontier bul — en yakın keşfedilmemiş sınır hücresi
        Direction dir = findNearestFrontier(robot, room);
        if (dir != null) return dir;

        // frontier kalmadıysa rastgele git
        for (Direction d : Direction.values()) {
            int nx = robot.getX() + d.getDx();
            int ny = robot.getY() + d.getDy();
            if (!room.isObstacle(nx, ny)) return d;
        }

        return robot.getDir();
    }

    // frontier: ziyaret edilmiş hücrenin yanında ziyaret edilmemiş hücre
    private Direction findNearestFrontier(Robot robot, Room room) {
        Queue<int[]> queue = new LinkedList<>();
        boolean[][] seen = new boolean[room.getWidth()][room.getHeight()];
        Direction[][] cameFrom = new Direction[room.getWidth()][room.getHeight()];

        int startX = robot.getX();
        int startY = robot.getY();

        queue.add(new int[]{startX, startY});
        seen[startX][startY] = true;

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int cx = current[0];
            int cy = current[1];

            // frontier mı? — ziyaret edilmemiş ve komşusu ziyaret edilmiş
            if (!room.getCell(cx, cy).isVisited() && (cx != startX || cy != startY)) {
                if (hasFrontierNeighbor(cx, cy, room)) {
                    List<Direction> path = reconstructPath(cameFrom, startX, startY, cx, cy);
                    if (!path.isEmpty()) {
                        currentPath = path;
                        return currentPath.remove(0);
                    }
                }
            }

            for (Direction dir : Direction.values()) {
                int nx = cx + dir.getDx();
                int ny = cy + dir.getDy();

                if (!room.isInBounds(nx, ny)) continue;
                if (room.isObstacle(nx, ny)) continue;
                if (seen[nx][ny]) continue;

                seen[nx][ny] = true;
                cameFrom[nx][ny] = dir;
                queue.add(new int[]{nx, ny});
            }
        }

        return null;
    }

    // komşularından biri ziyaret edilmiş mi?
    private boolean hasFrontierNeighbor(int x, int y, Room room) {
        for (Direction dir : Direction.values()) {
            int nx = x + dir.getDx();
            int ny = y + dir.getDy();
            if (!room.isInBounds(nx, ny)) continue;
            if (room.isObstacle(nx, ny)) continue;
            if (room.getCell(nx, ny).isVisited()) return true;
        }
        return false;
    }

    private List<Direction> reconstructPath(Direction[][] cameFrom, int startX, int startY, int targetX, int targetY) {
        List<Direction> path = new ArrayList<>();
        int cx = targetX;
        int cy = targetY;

        while (cx != startX || cy != startY) {
            Direction dir = cameFrom[cx][cy];
            path.add(dir);
            cx -= dir.getDx();
            cy -= dir.getDy();
        }

        Collections.reverse(path);
        return path;
    }
}
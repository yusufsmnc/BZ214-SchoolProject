package bm.erciyes.robotvacuumsim.strategy;

import bm.erciyes.robotvacuumsim.model.Robot;
import bm.erciyes.robotvacuumsim.model.Room;
import bm.erciyes.robotvacuumsim.util.Direction;

import java.util.*;

public class SpiralStrategy implements CleaningStrategy {

    private boolean goingRight = true;
    private List<Direction> pathToUnvisited = new ArrayList<>();

    @Override
    public Direction nextMove(Robot robot, Room room) {

        // BFS ile gidilecek yol varsa takip et
        if (!pathToUnvisited.isEmpty()) {
            Direction next = pathToUnvisited.remove(0);
            // yol geçerliyse devam et
            int nx = robot.getX() + next.getDx();
            int ny = robot.getY() + next.getDy();
            if (!room.isObstacle(nx, ny)) return next;
            else pathToUnvisited.clear(); // yol geçersizse sıfırla
        }

        Direction horizontal = goingRight ? Direction.EAST : Direction.WEST;

        int frontX = robot.getX() + horizontal.getDx();
        int frontY = robot.getY() + horizontal.getDy();

        // yatay hareket yapabiliyorsa devam et
        if (!room.isObstacle(frontX, frontY) && !room.getCell(frontX, frontY).isVisited()) {
            return horizontal;
        }

        // aşağı in
        int downX = robot.getX() + Direction.SOUTH.getDx();
        int downY = robot.getY() + Direction.SOUTH.getDy();

        if (!room.isObstacle(downX, downY) && !room.getCell(downX, downY).isVisited()) {
            goingRight = !goingRight;
            return Direction.SOUTH;
        }

        // sıkıştı — BFS ile en yakın ziyaret edilmemiş hücreyi bul
        pathToUnvisited = findPathToUnvisited(robot, room);
        if (!pathToUnvisited.isEmpty()) {
            return pathToUnvisited.remove(0);
        }

        // tüm oda temizlendi
        return robot.getDir();
    }

    private List<Direction> findPathToUnvisited(Robot robot, Room room) {
        Queue<int[]> queue = new LinkedList<>();
        boolean[][] seen = new boolean[room.getWidth()][room.getHeight()];
        Direction[][] cameFrom = new Direction[room.getWidth()][room.getHeight()];
        int[][] parent = new int[room.getWidth() * room.getHeight()][2];

        int startX = robot.getX();
        int startY = robot.getY();

        queue.add(new int[]{startX, startY});
        seen[startX][startY] = true;

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int cx = current[0];
            int cy = current[1];

            // ziyaret edilmemiş hücre bulundu
            if (!room.getCell(cx, cy).isVisited() && (cx != startX || cy != startY)) {
                return reconstructPath(cameFrom, startX, startY, cx, cy);
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

        return new ArrayList<>();
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
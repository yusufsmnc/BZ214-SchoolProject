package bm.erciyes.robotvacuumsim.controller;

import bm.erciyes.robotvacuumsim.model.Robot;
import bm.erciyes.robotvacuumsim.model.Room;
import bm.erciyes.robotvacuumsim.util.Direction;

public class CollisionHandler {
    public boolean canMove(Robot robot, Direction dir, Room room) {
        int newX = robot.getX() + dir.getDx();
        int newY = robot.getY() + dir.getDy();
        // engel kontrolü
        return !room.isObstacle(newX, newY);
    }
    public Direction resolveCollision(Robot robot, Room room){
        // tüm yönleri dene, geçilebileni döndür
        for (Direction dir : Direction.values()) {
            if (canMove(robot, dir, room))
                return dir;
        }
        return robot.getDir(); // hiçbiri yoksa aynı yönde kal
    }
}

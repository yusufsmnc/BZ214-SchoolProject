package bm.erciyes.robotvacuumsim.strategy;

import bm.erciyes.robotvacuumsim.model.Robot;
import bm.erciyes.robotvacuumsim.model.Room;
import bm.erciyes.robotvacuumsim.util.Direction;

public interface CleaningStrategy {
    Direction nextMove(Robot robot, Room room);
}

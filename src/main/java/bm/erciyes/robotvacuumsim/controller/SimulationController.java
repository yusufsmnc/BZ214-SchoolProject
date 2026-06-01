package bm.erciyes.robotvacuumsim.controller;

import bm.erciyes.robotvacuumsim.model.Cell;
import bm.erciyes.robotvacuumsim.model.Robot;
import bm.erciyes.robotvacuumsim.model.Room;
import bm.erciyes.robotvacuumsim.model.SimulationState;
import bm.erciyes.robotvacuumsim.strategy.CleaningStrategy;
import bm.erciyes.robotvacuumsim.strategy.RandomStrategy;
import bm.erciyes.robotvacuumsim.strategy.SpiralStrategy;
import bm.erciyes.robotvacuumsim.strategy.WallFollowStrategy;
import bm.erciyes.robotvacuumsim.util.Direction;
import bm.erciyes.robotvacuumsim.util.DirtType;
import bm.erciyes.robotvacuumsim.util.RobotStatus;
import bm.erciyes.robotvacuumsim.util.StrategyType;
import bm.erciyes.robotvacuumsim.view.MainController;
import javafx.application.Platform;

import java.util.List;

public class SimulationController {
    private int cleaningTick = 0; // temizleme sayacı
    private int tickCount = 0;  // hareketleri sayıp, istasyona yetişmek için gereken pil miktarını hesaplamak için kullancaz
    private boolean robotMoved = false;
    private Room room;
    private Robot robot;
    private SimulationState state;
    private CleaningStrategy strategy;
    private PathFinder pathFinder;
    private StatisticTracker stats;
    private CollisionHandler collisionHandler;
    private List<Direction> returnPath;
    private MainController mainController; // UI referansı

    public SimulationController() {
        this.room = new Room(20,14);
        this.robot = new Robot(1,1);
        this.state = new SimulationState();
        this.strategy = new RandomStrategy();
        this.pathFinder = new PathFinder(room);
        this.stats = new StatisticTracker(room, state);
        this.collisionHandler = new CollisionHandler();
    }

    // MainController referansını set et
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void startSimulation(){
        state.start();
        robot.setRobotStatus(RobotStatus.MOVING);
    }

    public void pauseSimulation() {
        state.pause();
        robot.setRobotStatus(RobotStatus.IDLE);
    }

    public void resetSimulation(){
        state.reset();
        this.robot = new Robot(1,1);
        this.room = new Room(20,14);
        this.stats = new StatisticTracker(room,state);
        this.pathFinder = new PathFinder(room);
    }
    public void returnToStation(){
        // dönüş yolu
        robot.setRobotStatus(RobotStatus.RETURNING);
        this.returnPath = pathFinder.findPath(robot.getX(), robot.getY(), room.getStation().getX(), room.getStation().getY());
    }

    public boolean didRobotMove() {
        boolean moved = robotMoved;
        robotMoved = false;
        return moved;
    }

    public void onTick() {
        // simülasyon çalışmıyorsa veya duraklatıldıysa dur
        if (!state.isRunning() || state.isPaused()) return;

        tickCount++; // her tick sayılıyor

        // robot istasyona dönüyorsa
        if (robot.getRobotStatus() == RobotStatus.RETURNING) {
            if (returnPath != null && !returnPath.isEmpty()) {
                // yoldan bir sonraki adımı al ve hareket et
                Direction next = returnPath.remove(0);
                robot.move(next);
                robotMoved = true;

                // istasyona ulaştı mı?
                if (robot.getX() == room.getStation().getX() && robot.getY() == room.getStation().getY()) {
                    // şarj et
                    robot.getBat().charge();
                    robot.setRobotStatus(RobotStatus.CHARGING);
                }
            }
            updateUI();
            return;
        }

        // şarj oluyorsa
        if (robot.getRobotStatus() == RobotStatus.CHARGING) {
            // şarj tamamlandı, tekrar hareket et
            robot.setRobotStatus(RobotStatus.MOVING);
            updateUI();
            return;
        }

        // her tick mesafe hesapla
        int distance = pathFinder.findPath(
                robot.getX(), robot.getY(),
                room.getStation().getX(), room.getStation().getY()
        ).size();

        int safetyMargin = 10;

        if (robot.getBat().getLevel() <= distance + safetyMargin) {
            returnToStation();
            updateUI();
            return;
        }
        // hücrede kir var mı? temizle
        Cell cell = room.getCell(robot.getX(), robot.getY());
        if (cell.hasDirt()) {
            robot.setRobotStatus(RobotStatus.CLEANING);
            robot.clean(cell);
            cleaningTick++;

            // kir temizlendi mi?
            if (!cell.hasDirt()) {
                state.incrementDirt();
                cleaningTick = 0;
                // kir temizlendiğinde hücreyi ziyaret edildi olarak işaretle
                room.getCell(robot.getX(), robot.getY()).setVisited(true);

            }
            // UI güncelle — JavaFX thread'inde çalıştır
            updateUI();
            return;
        }
        cleaningTick = 0;

        // normal hareket — strateji ile yön belirle
        Direction dir = strategy.nextMove(robot, room);

        // engel varsa çarpışma çöz
        if (!collisionHandler.canMove(robot, dir, room)) {
            dir = collisionHandler.resolveCollision(robot, room);
        }

        // hareket et
        robot.move(dir);
        robotMoved = true;
        robot.setRobotStatus(RobotStatus.MOVING);

        // hücreyi ziyaret edildi yap
        room.getCell(robot.getX(), robot.getY()).setVisited(true);

        // şarj istasyonunun üzerindeyse şarj et
        if (robot.getX() == room.getStation().getX() &&
                robot.getY() == room.getStation().getY()) {
            robot.getBat().charge();
        }
        updateUI();
    }

    // UI'yi güvenli şekilde güncelle
    private void updateUI() {
        if (mainController != null) {
            Platform.runLater(() -> mainController.updateStatus());
        }
    }

    // kir ekle — araç panelinden
    public void addDirt(int x, int y, DirtType type) {
        room.addDirt(x, y, type);
    }

    // mobilya ekle
    public void addFurniture(int x, int y, int width, int height) {
        room.addFurniture(x, y, width, height);
    }

    // strateji değiştir
    public void setStrategy(StrategyType type) {
        switch (type) {
            case RANDOM     -> strategy = new RandomStrategy();
            case SPIRAL     -> strategy = new SpiralStrategy();
            case WALL_FOLLOW -> strategy = new WallFollowStrategy();
        }
    }

    // batarya manuel ayarla
    public void setBattery(int level) {
        robot.getBat().setLevel(level);
    }

    // getter'lar — view bunları okuyacak
    public Room getRoom() { return room; }
    public Robot getRobot() { return robot; }
    public SimulationState getState() { return state; }
    public StatisticTracker getStats() { return stats; }
}

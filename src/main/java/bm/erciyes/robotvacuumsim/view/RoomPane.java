package bm.erciyes.robotvacuumsim.view;

import bm.erciyes.robotvacuumsim.controller.SimulationController;
import bm.erciyes.robotvacuumsim.model.*;
import bm.erciyes.robotvacuumsim.util.DirtType;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RoomPane extends Pane {

    private SimulationController controller;
    private ImageView robotView;
    private Rectangle[][] cellRects;
    private ImageView[][] dirtViews;
    private Image robotImage;
    private Image stationImage;
    private Image dustImage;
    private Image liquidImage;
    private Image stainImage;
    private Image furnitureImage;

    public static final int CELL_SIZE = 40;
    private boolean rebuilding = false; // listener kontrolü

    private double lastWidth = 0;
    private double lastHeight = 0;


    public RoomPane(SimulationController controller) {
        this.controller = controller;
        loadImages();

        widthProperty().addListener((obs, old, newVal) -> {
            if (newVal.doubleValue() > 0 && !rebuilding &&
                    Math.abs(newVal.doubleValue() - lastWidth) > 1) {
                lastWidth = newVal.doubleValue();
                rebuildGrid();
            }
        });
        heightProperty().addListener((obs, old, newVal) -> {
            if (newVal.doubleValue() > 0 && !rebuilding &&
                    Math.abs(newVal.doubleValue() - lastHeight) > 1) {
                lastHeight = newVal.doubleValue();
                rebuildGrid();
            }
        });
    }

    private Image loadImage(String path) {
        try {
            var stream = getClass().getResourceAsStream(path);
            return stream != null ? new Image(stream) : null;
        } catch (Exception e) {
            return null;
        }
    }

    private void loadImages() {
        robotImage     = loadImage("/bm/erciyes/robotvacuumsim/images/robot-vacuum-cleaner.png");
        stationImage   = loadImage("/bm/erciyes/robotvacuumsim/images/placeholder.png");
        dustImage      = loadImage("/bm/erciyes/robotvacuumsim/images/dust.png");
        liquidImage    = loadImage("/bm/erciyes/robotvacuumsim/images/splash.png");
        stainImage     = loadImage("/bm/erciyes/robotvacuumsim/images/spot.png");
        furnitureImage = loadImage("/bm/erciyes/robotvacuumsim/images/armchair.png");
    }

    public int getCellSize() {
        int w = (int) Math.max(getWidth(), getPrefWidth());
        int h = (int) Math.max(getHeight(), getPrefHeight());
        if (w <= 0 || h <= 0) return CELL_SIZE;
        Room room = controller.getRoom();
        int cellW = w / room.getWidth();
        int cellH = h / room.getHeight();
        return Math.min(cellW, cellH);
    }

    public void rebuildGrid() {
        getChildren().clear();

        Room room = controller.getRoom();
        int cs = getCellSize();
        int width = room.getWidth();
        int height = room.getHeight();

        cellRects = new Rectangle[width][height];
        dirtViews = new ImageView[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Cell cell = room.getCell(x, y);
                Rectangle rect = new Rectangle(x * cs, y * cs, cs, cs);
                rect.setFill(getCellColor(cell));
                rect.setStroke(Color.rgb(210, 210, 215));
                rect.setStrokeWidth(0.5);
                cellRects[x][y] = rect;
                getChildren().add(rect);
            }
        }

        // yenisi — metodu çağır
        updateUnreachable();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Cell cell = room.getCell(x, y);
                if (cell.hasDirt() && !cell.isObstacle()) {
                    ImageView dv = createDirtView(x, y, cell.getDirt().getType(), cs);
                    if (dv != null) {
                        dirtViews[x][y] = dv;
                        getChildren().add(dv);
                    }
                }
            }
        }

        for (Furniture f : room.getFurnitures()) {
            if (furnitureImage != null) {
                ImageView fv = new ImageView(furnitureImage);
                fv.setX(f.getX() * cs);
                fv.setY(f.getY() * cs);
                fv.setFitWidth(f.getWidth() * cs);
                fv.setFitHeight(f.getHeight() * cs);
                getChildren().add(fv);
            } else {
                Rectangle fr = new Rectangle(f.getX() * cs, f.getY() * cs, f.getWidth() * cs, f.getHeight() * cs);
                fr.setFill(Color.SADDLEBROWN);
                getChildren().add(fr);
            }
        }

        if (stationImage != null) {
            ImageView sv = new ImageView(stationImage);
            sv.setX(room.getStation().getX() * cs);
            sv.setY(room.getStation().getY() * cs);
            sv.setFitWidth(cs);
            sv.setFitHeight(cs);
            getChildren().add(sv);
        }

        robotView = new ImageView(robotImage);
        Robot robot = controller.getRobot();
        robotView.setX(robot.getX() * cs);
        robotView.setY(robot.getY() * cs);
        robotView.setFitWidth(cs);
        robotView.setFitHeight(cs);
        getChildren().add(robotView);
    }

    private Color getCellColor(Cell cell) {
        if (cell.isObstacle()) return Color.rgb(100, 100, 110);
        if (cell.isVisited()) return Color.rgb(200, 230, 210);
        return Color.rgb(245, 245, 250);
    }

    private ImageView createDirtView(int x, int y, DirtType type, int cs) {
        Image dirtImg = getDirtImage(type);
        if (dirtImg == null) return null;
        ImageView dv = new ImageView(dirtImg);
        dv.setX(x * cs + 4);
        dv.setY(y * cs + 4);
        dv.setFitWidth(cs - 8);
        dv.setFitHeight(cs - 8);
        return dv;
    }

    private Image getDirtImage(DirtType type) {
        return switch (type) {
            case DUST   -> dustImage;
            case LIQUID -> liquidImage;
            case STAIN  -> stainImage;
        };
    }

    public void update() {
        if (cellRects == null) {
            rebuildGrid();
            return;
        }

        Room room = controller.getRoom();
        Robot robot = controller.getRobot();
        int cs = getCellSize();

        // unreachable hücreleri al
        List<int[]> unreachable = room.getUnreachableCells();
        Set<String> unreachableSet = new HashSet<>();
        for (int[] uc : unreachable) {
            unreachableSet.add(uc[0] + "," + uc[1]);
        }

        for (int x = 0; x < room.getWidth(); x++) {
            for (int y = 0; y < room.getHeight(); y++) {
                Cell cell = room.getCell(x, y);
                Rectangle rect = cellRects[x][y];
                if (rect != null) {
                    if (unreachableSet.contains(x + "," + y)) {
                        rect.setFill(Color.rgb(255, 180, 180)); // kırmızı
                    } else {
                        rect.setFill(getCellColor(cell));
                    }
                }
                if (!cell.hasDirt() && dirtViews[x][y] != null) animateDirtClean(x, y);
            }
        }

        robotView.setX(robot.getX() * cs);
        robotView.setY(robot.getY() * cs);
    }

    public void animateRobotMove(int fromX, int fromY, int toX, int toY) {
        int cs = getCellSize();
        robotView.setX(fromX * cs);
        robotView.setY(fromY * cs);

        TranslateTransition move = new TranslateTransition(Duration.millis(150), robotView);
        move.setToX((toX - fromX) * cs);
        move.setToY((toY - fromY) * cs);
        move.setOnFinished(e -> {
            robotView.setX(toX * cs);
            robotView.setY(toY * cs);
            robotView.setTranslateX(0);
            robotView.setTranslateY(0);
        });
        move.play();
    }

    public void animateDirtClean(int x, int y) {
        if (dirtViews[x][y] == null) return;
        ImageView dirtView = dirtViews[x][y];
        dirtViews[x][y] = null;

        FadeTransition fade = new FadeTransition(Duration.millis(400), dirtView);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);

        ScaleTransition scale = new ScaleTransition(Duration.millis(400), dirtView);
        scale.setToX(1.5);
        scale.setToY(1.5);

        ParallelTransition anim = new ParallelTransition(fade, scale);
        anim.setOnFinished(e -> getChildren().remove(dirtView));
        anim.play();
    }

    public void addDirtView(int x, int y, DirtType type) {
        if (controller.getRoom().isObstacle(x, y)) return;
        if (dirtViews[x][y] != null) return;

        Platform.runLater(() -> {
            int cs = getCellSize();
            ImageView dirtView = createDirtView(x, y, type, cs);
            if (dirtView == null) return;
            dirtView.setScaleX(0);
            dirtView.setScaleY(0);
            if (dirtViews != null) dirtViews[x][y] = dirtView;
            getChildren().add(dirtView);
            ScaleTransition appear = new ScaleTransition(Duration.millis(300), dirtView);
            appear.setToX(1.0);
            appear.setToY(1.0);
            appear.play();
        });
    }

    public void addFurnitureView(int x, int y, int cs) {
        System.out.println("addFurnitureView çağrıldı: x=" + x + " y=" + y + " cs=" + cs);
        System.out.println("furnitureImage: " + furnitureImage);
        System.out.println("getChildren().size() önce: " + getChildren().size());

        // zaten obstacle ise ekleme
        if (cellRects != null && cellRects[x][y] != null &&
                cellRects[x][y].getFill().equals(Color.rgb(100, 100, 110))) {
            return;
        }

        Room room = controller.getRoom();
        rebuilding = true;

        for (int i = x; i < x + 2; i++) {
            for (int j = y; j < y + 2; j++) {
                if (room.isInBounds(i, j) && cellRects != null && cellRects[i][j] != null) {
                    cellRects[i][j].setFill(Color.rgb(100, 100, 110));
                }
            }
        }

        if (furnitureImage != null) {
            ImageView fv = new ImageView(furnitureImage);
            fv.setX(x * cs - 4);   // image i büyüttük , altındaki gir tabanın gözükmesini azaltmak için
            fv.setY(y * cs - 4);
            fv.setFitWidth(2 * cs + 8);
            fv.setFitHeight(2 * cs + 8);
            fv.setPreserveRatio(false);
            getChildren().remove(robotView);
            getChildren().add(fv);
            getChildren().add(robotView);
            System.out.println("ImageView eklendi: " + fv.getX() + " " + fv.getY() + " w=" + fv.getFitWidth());
        }

        System.out.println("getChildren().size() sonra: " + getChildren().size());
        rebuilding = false;

    }

    private void updateUnreachable() {
        if (cellRects == null) return;
        Room room = controller.getRoom();

        // obstacle olmayanları temizle
        for (int x = 0; x < room.getWidth(); x++) {
            for (int y = 0; y < room.getHeight(); y++) {
                if (cellRects[x][y] != null && !room.isObstacle(x, y)) {
                    cellRects[x][y].setFill(getCellColor(room.getCell(x, y)));
                }
            }
        }

        // unreachable hücreleri kırmızı yap
        List<int[]> unreachable = room.getUnreachableCells();
        for (int[] uc : unreachable) {
            if (cellRects[uc[0]][uc[1]] != null) {
                cellRects[uc[0]][uc[1]].setFill(Color.rgb(255, 180, 180));
            }
        }
    }
}
package bm.erciyes.robotvacuumsim.view;

import bm.erciyes.robotvacuumsim.controller.SimulationController;
import bm.erciyes.robotvacuumsim.model.Cell;
import bm.erciyes.robotvacuumsim.model.Furniture;
import bm.erciyes.robotvacuumsim.model.Robot;
import bm.erciyes.robotvacuumsim.model.Room;
import bm.erciyes.robotvacuumsim.util.DirtType;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class RoomCanvas {

    private Canvas canvas;
    private SimulationController controller;
    private Image robotImage;
    private Image wallImage;
    private Image stationImage;
    private Image furnitureImage;
    private Image dustImage;
    private Image liquidImage;
    private Image stainImage;

    public RoomCanvas(Canvas canvas, SimulationController controller) {
        this.canvas = canvas;
        this.controller = controller;

        // robot resmini yükle
        try {
            var stream = getClass().getResourceAsStream(
                    "/bm/erciyes/robotvacuumsim/images/robot-vacuum-cleaner.png"
            );
            if (stream != null) {
                robotImage = new Image(stream);
            } else {
                robotImage = null;
            }
        } catch (Exception e) {
            robotImage = null;
        }

        // duvar resmini yükle
        try {
            var wallStream = getClass().getResourceAsStream(
                    "/bm/erciyes/robotvacuumsim/images/bricks-wall.png"
            );
            if (wallStream != null) {
                wallImage = new Image(wallStream);
            } else {
                wallImage = null;
            }
        } catch (Exception e) {
            wallImage = null;
        }
        // sarj istasyonu resmini yükle
        try {
            var stationStream = getClass().getResourceAsStream(
                    "/bm/erciyes/robotvacuumsim/images/placeholder.png"
            );
            if (stationStream != null) {
                stationImage = new Image(stationStream);
            } else {
                stationImage = null;
            }
        } catch (Exception e) {
            stationImage = null;
        }
        // koltuk resmini yükle
        try {
            var furnitureStream = getClass().getResourceAsStream(
                    "/bm/erciyes/robotvacuumsim/images/armchair.png"
            );
            furnitureImage = furnitureStream != null ? new Image(furnitureStream) : null;
        } catch (Exception e) {
            furnitureImage = null;
        }
        // toz resmini yükle
        try {
            var dustStream = getClass().getResourceAsStream(
                    "/bm/erciyes/robotvacuumsim/images/dust.png"
            );
            dustImage = dustStream != null ? new Image(dustStream) : null;
        } catch (Exception e) { dustImage = null; }

        // Sıvı resmini (su) yükle
        try {
            var liquidStream = getClass().getResourceAsStream(
                    "/bm/erciyes/robotvacuumsim/images/splash.png"
            );
            liquidImage = liquidStream != null ? new Image(liquidStream) : null;
        } catch (Exception e) { liquidImage = null; }

        // Leke resmini yükle
        try {
            var stainStream = getClass().getResourceAsStream(
                    "/bm/erciyes/robotvacuumsim/images/spot.png"
            );
            stainImage = stainStream != null ? new Image(stainStream) : null;
        } catch (Exception e) { stainImage = null; }
    }

    // canvas boyutuna göre dinamik hücre boyutu hesapla
    private int getCellSize() {
        Room room = controller.getRoom();
        int availableWidth = (int) canvas.getWidth();
        int availableHeight = (int) canvas.getHeight();

        // canvas henüz boyutlanmadıysa varsayılan değer döndür
        if (availableWidth <= 0 || availableHeight <= 0) return 32;

        int cellW = availableWidth / (room.getWidth() + 2);
        int cellH = availableHeight / (room.getHeight() + 2);
        return Math.min(cellW, cellH);
    }

    public void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Room room = controller.getRoom();
        Robot robot = controller.getRobot();

        int cellSize = getCellSize();

        // odayı ortala
        int totalWidth = (room.getWidth() + 2) * cellSize;
        int totalHeight = (room.getHeight() + 2) * cellSize;
        int startX = (int)(canvas.getWidth() - totalWidth) / 2;
        int startY = (int)(canvas.getHeight() - totalHeight) / 2;
        int offset = cellSize;

        // canvas'ı temizle
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // arka planı koyu yap — boşluk alanı
        gc.setFill(Color.rgb(30, 30, 46));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // duvarları çiz
        if (wallImage != null && !wallImage.isError()) {
            for (int x = 0; x < room.getWidth() + 2; x++) {
                gc.drawImage(wallImage, startX + x * cellSize, startY, cellSize, cellSize);
                gc.drawImage(wallImage, startX + x * cellSize, startY + (room.getHeight() + 1) * cellSize, cellSize, cellSize);
            }
            for (int y = 0; y < room.getHeight() + 2; y++) {
                gc.drawImage(wallImage, startX, startY + y * cellSize, cellSize, cellSize);
                gc.drawImage(wallImage, startX + (room.getWidth() + 1) * cellSize, startY + y * cellSize, cellSize, cellSize);
            }
        } else {
            gc.setFill(Color.DARKGRAY);
            gc.fillRect(startX, startY, totalWidth, cellSize);
            gc.fillRect(startX, startY + totalHeight - cellSize, totalWidth, cellSize);
            gc.fillRect(startX, startY, cellSize, totalHeight);
            gc.fillRect(startX + totalWidth - cellSize, startY, cellSize, totalHeight);
        }

        // hücreleri çiz
        for (int x = 0; x < room.getWidth(); x++) {
            for (int y = 0; y < room.getHeight(); y++) {
                Cell cell = room.getCell(x, y);
                drawCell(gc, cell, x, y, cellSize, startX + offset, startY + offset);
            }
        }

        // ızgara numaralarını çiz
        gc.setFill(Color.CRIMSON);
        gc.setFont(Font.font("Segoe UI", Math.max(9, cellSize / 3)));
        for (int x = 0; x < room.getWidth(); x++) {
            gc.fillText(String.valueOf(x), startX + x * cellSize + offset + cellSize / 4, startY + cellSize - 4);
        }
        for (int y = 0; y < room.getHeight(); y++) {
            gc.fillText(String.valueOf(y), startX + 4, startY + y * cellSize + offset + cellSize * 2 / 3);
        }

        // mobilyaları çiz
        for (Furniture f : room.getFurnitures()) {
            if (furnitureImage != null && !furnitureImage.isError()) {
                gc.drawImage(furnitureImage,
                        startX + offset + f.getX() * cellSize,
                        startY + offset + f.getY() * cellSize,
                        f.getWidth() * cellSize,
                        f.getHeight() * cellSize
                );
            } else {
                gc.setFill(Color.SADDLEBROWN);
                gc.fillRect(
                        startX + offset + f.getX() * cellSize,
                        startY + offset + f.getY() * cellSize,
                        f.getWidth() * cellSize,
                        f.getHeight() * cellSize
                );
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(1);
                gc.strokeRect(
                        startX + offset + f.getX() * cellSize,
                        startY + offset + f.getY() * cellSize,
                        f.getWidth() * cellSize,
                        f.getHeight() * cellSize
                );
            }
        }

        // şarj istasyonunu çiz
        int stX = startX + offset + room.getStation().getX() * cellSize;
        int stY = startY + offset + room.getStation().getY() * cellSize;
        if (stationImage != null && !stationImage.isError()) {
            gc.drawImage(stationImage, stX, stY, cellSize, cellSize);
        } else {
            gc.setFill(Color.YELLOW);
            gc.fillRect(stX, stY, cellSize, cellSize);
            gc.setFill(Color.BLACK);
            gc.fillText("⚡", stX + cellSize / 4, stY + cellSize * 2 / 3);
        }

        // robotu çiz
        int rX = startX + offset + robot.getX() * cellSize;
        int rY = startY + offset + robot.getY() * cellSize;
        if (robotImage != null && !robotImage.isError()) {
            gc.drawImage(robotImage, rX, rY, cellSize, cellSize);
        } else {
            gc.setFill(Color.DODGERBLUE);
            gc.fillOval(rX + 2, rY + 2, cellSize - 4, cellSize - 4);
            gc.setFill(Color.WHITE);
            gc.fillText("R", rX + cellSize / 3, rY + cellSize * 2 / 3);
        }
    }

    private void drawCell(GraphicsContext gc, Cell cell, int x, int y, int cellSize, int offsetX, int offsetY) {
        int px = offsetX + x * cellSize;
        int py = offsetY + y * cellSize;

        if (cell.isObstacle()) {
            gc.setFill(Color.DARKGRAY);
        } else if (cell.isVisited()) {
            gc.setFill(Color.rgb(173, 216, 230, 0.5)); // yarı saydam açık mavi
        } else {
            gc.setFill(Color.rgb(245, 235, 220)); // açık krem — temiz zemin
        }
        gc.fillRect(px, py, cellSize, cellSize);

        if (cell.hasDirt()) {
            DirtType type = cell.getDirt().getType();
            Image dirtImg = switch (type) {
                case DUST   -> dustImage;
                case LIQUID -> liquidImage;
                case STAIN  -> stainImage;
            };

            if (dirtImg != null && !dirtImg.isError()) {
                gc.drawImage(dirtImg, px + 2, py + 2, cellSize - 4, cellSize - 4);
            } else {
                // görsel yoksa düz renk
                if (type == DirtType.DUST) {
                    gc.setFill(Color.GRAY);
                } else if (type == DirtType.LIQUID) {
                    gc.setFill(Color.DEEPSKYBLUE);
                } else {
                    gc.setFill(Color.BROWN);
                }
                gc.fillOval(px + cellSize / 4, py + cellSize / 4, cellSize / 2, cellSize / 2);
            }
        }

        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(0.5);
        gc.strokeRect(px, py, cellSize, cellSize);
    }
}
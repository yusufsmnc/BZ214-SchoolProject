package bm.erciyes.robotvacuumsim.view;

import bm.erciyes.robotvacuumsim.controller.SimulationController;
import bm.erciyes.robotvacuumsim.controller.SimulationLoop;
import bm.erciyes.robotvacuumsim.model.Room;
import bm.erciyes.robotvacuumsim.util.DirtType;
import bm.erciyes.robotvacuumsim.util.StrategyType;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

public class MainController {

    // FXML elemanları — Scene Builder'daki fx:id'ler ile eşleşmeli
    @FXML private Button startButton;
    @FXML private Button pauseButton;
    @FXML private Button resetButton;
    @FXML private Button returnButton;
    @FXML private Button addDirtButton;
    @FXML private Button addFurnitureButton;
    @FXML private Slider speedSlider;
    @FXML private ProgressBar batteryBar;
    @FXML private RadioButton randomRadio;
    @FXML private RadioButton spiralRadio;
    @FXML private RadioButton wallRadio;
    @FXML private RadioButton dustRadio;
    @FXML private RadioButton liquidRadio;
    @FXML private RadioButton stainRadio;
    @FXML private Canvas roomCanvas;
    @FXML private Label positionLabel;
    @FXML private Label cleanedLabel;
    @FXML private Label remainingLabel;
    @FXML private Label timeLabel;
    @FXML private Label dirtLabel;
    @FXML private Label directionLabel;
    @FXML private Label batteryLabel;
    @FXML private Label totalAreaLabel;
    @FXML private Label speedLabel;
    @FXML private Pane canvasPane;


    // Controller ve Loop
    private SimulationController simulationController;
    private SimulationLoop simulationLoop;
    private RoomCanvas roomCanvasRenderer;

    private boolean dirtMode = false;
    private boolean furnitureMode = false;

    // FXML yüklenince otomatik çağrılır
    @FXML
    public void initialize() {
        // controller ve loop oluştur
        simulationController = new SimulationController();
        simulationLoop = new SimulationLoop(simulationController);
        simulationController.setMainController(this);

        // canvas renderer oluştur
        roomCanvasRenderer = new RoomCanvas(roomCanvas, simulationController);

        // toplam alan — başlangıçta bir kere hesapla
        totalAreaLabel.setText(simulationController.getRoom().getTotalCleanableCells() + " hücre");

        // canvas boyutunu pane'e bağla — sahne hazır olunca
        roomCanvas.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                roomCanvas.widthProperty().bind(canvasPane.widthProperty());
                roomCanvas.heightProperty().bind(canvasPane.heightProperty());
                canvasPane.widthProperty().addListener((o, old, newVal) -> roomCanvasRenderer.draw());
                canvasPane.heightProperty().addListener((o, old, newVal) -> roomCanvasRenderer.draw());
                roomCanvasRenderer.draw();
            }
        });

        // radio button grubu — sadece biri seçilebilsin
        ToggleGroup strategyGroup = new ToggleGroup();
        randomRadio.setToggleGroup(strategyGroup);
        spiralRadio.setToggleGroup(strategyGroup);
        wallRadio.setToggleGroup(strategyGroup);
        randomRadio.setSelected(true); // varsayılan

        // kir türü radio group
        ToggleGroup dirtGroup = new ToggleGroup();
        dustRadio.setToggleGroup(dirtGroup);
        liquidRadio.setToggleGroup(dirtGroup);
        stainRadio.setToggleGroup(dirtGroup);
        dustRadio.setSelected(true);

        // slider değişince hız güncelle
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            simulationLoop.setSpeed(newVal.doubleValue());
            speedLabel.setText(String.format("%.1f", newVal.doubleValue()) + "x");
        });

        // radio button değişince strateji güncelle
        strategyGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == randomRadio) simulationController.setStrategy(StrategyType.RANDOM);
            else if (newVal == spiralRadio) simulationController.setStrategy(StrategyType.SPIRAL);
            else if (newVal == wallRadio) simulationController.setStrategy(StrategyType.WALL_FOLLOW);
        });

        // canvas tıklama olayı
        roomCanvas.setOnMouseClicked(event -> {
            Room room = simulationController.getRoom();
            int cellSize = (int) Math.min(
                    roomCanvas.getWidth() / (room.getWidth() + 2),
                    roomCanvas.getHeight() / (room.getHeight() + 2)
            );
            int totalWidth = (room.getWidth() + 2) * cellSize;
            int totalHeight = (room.getHeight() + 2) * cellSize;
            int startX = (int)(roomCanvas.getWidth() - totalWidth) / 2;
            int startY = (int)(roomCanvas.getHeight() - totalHeight) / 2;

            int cellX = (int) ((event.getX() - startX - cellSize) / cellSize);
            int cellY = (int) ((event.getY() - startY - cellSize) / cellSize);

            if (!room.isInBounds(cellX, cellY)) return;

            if (dirtMode) {
                DirtType type = DirtType.DUST;
                if (liquidRadio.isSelected()) type = DirtType.LIQUID;
                else if (stainRadio.isSelected()) type = DirtType.STAIN;
                simulationController.addDirt(cellX, cellY, type);
                roomCanvasRenderer.draw();
            } else if (furnitureMode) {
                if (!room.isInBounds(cellX + 1, cellY + 1)) return;
                simulationController.addFurniture(cellX, cellY, 2, 2, "Mobilya");
                roomCanvasRenderer.draw();
            }
        });
    }

    // Başlat butonu
    @FXML
    private void onStartClicked() {
        simulationController.startSimulation();
        simulationLoop.start();
    }

    // Duraklat butonu
    @FXML
    private void onPauseClicked() {
        simulationController.pauseSimulation();
        simulationLoop.pause();
    }

    // Sıfırla butonu
    @FXML
    private void onResetClicked() {
        simulationLoop.stop();
        simulationController.resetSimulation();
        roomCanvasRenderer.draw();
        updateStatus();
    }

    // İstasyona Dön butonu
    @FXML
    private void onReturnClicked() {
        simulationController.returnToStation();
    }

    @FXML
    private void onAddDirtClicked() {
        dirtMode = !dirtMode;
        furnitureMode = false;
        addDirtButton.setText(dirtMode ? "Kir Ekle Modu ✓" : "Kir Ekle Modu");
        addFurnitureButton.setText("Mobilya Ekle Modu");
    }

    @FXML
    private void onAddFurnitureClicked() {
        furnitureMode = !furnitureMode;
        dirtMode = false;
        addFurnitureButton.setText(furnitureMode ? "Mobilya Ekle Modu ✓" : "Mobilya Ekle Modu");
        addDirtButton.setText("Kir Ekle Modu");
    }

    // Her tick'te çağrılır — label'ları güncelle ve canvas'ı yeniden çiz
    public void updateStatus() {
        var robot = simulationController.getRobot();
        var stats = simulationController.getStats();
        var state = simulationController.getState();
        var room = simulationController.getRoom();

        String dirText = switch (robot.getDir()) {
            case NORTH -> "Kuzey";
            case SOUTH -> "Guney";
            case EAST  -> "Dogu";
            case WEST  -> "Bati";
        };

        int visited = room.getVisitedCells();
        int total = room.getTotalCleanableCells();

        positionLabel.setText("Konum: (" + robot.getX() + "," + robot.getY() + ")");
        directionLabel.setText("Yon: " + dirText);
        batteryBar.setProgress(robot.getBat().getLevel() / 100.0);
        batteryLabel.setText("%" + robot.getBat().getLevel());

        cleanedLabel.setText(visited + " hücre (" + String.format("%.1f", stats.getCleanedPercentage()) + "%)");
        remainingLabel.setText((total - visited) + " hücre (" + String.format("%.1f", stats.getRemainingPercentage()) + "%)");
        timeLabel.setText(state.getFormattedTime());
        dirtLabel.setText(String.valueOf(stats.getCollectedDirt()));


        roomCanvasRenderer.draw();
    }
}
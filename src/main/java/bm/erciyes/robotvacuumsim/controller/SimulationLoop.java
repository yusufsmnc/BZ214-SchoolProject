package bm.erciyes.robotvacuumsim.controller;

import javafx.animation.AnimationTimer;

public class SimulationLoop{
    private AnimationTimer timer;
    private SimulationController controller;
    private double speed;

    /*
     lastUpdate son tick'in zamanını nanosaniye cinsinden tutuyor.
     JavaFX AnimationTimer'ın "handle()" metodu "long now" parametresi alır — bu değer nanosaniye cinsinden
     Bu çok büyük bir sayı — int sığmaz, long gerekir.
    */
    private long lastUpdate;

    public SimulationLoop(SimulationController controller){
        this.controller = controller;
        this.speed = 1.0;
        this.lastUpdate = 0;
    }
    public void start() {
        // AnimationTimer oluşturuluyor — her frame'de handle() çağrılır
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // now: nanosaniye cinsinden şu anki zaman
                // 500_000_000 nanosaniye = 0.5 saniye
                // speed ile bölerek hızı ayarlıyoruz
                if (now - lastUpdate >= 500_000_000L / speed) {
                    // son tick zamanını güncelle
                    lastUpdate = now;
                    // simülasyonu bir adım ilerlet
                    controller.onTick();
                }
            }
        };
        // timer'ı başlat
        timer.start();
    }

    public void pause() {
        if (timer != null)
            timer.stop();
    }

    public void stop() {
        if (timer != null)
            timer.stop();
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}

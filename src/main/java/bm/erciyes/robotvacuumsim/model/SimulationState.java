package bm.erciyes.robotvacuumsim.model;

public class SimulationState {

    private int collectedDirt;

    // Java'da zaman almak için:
    // System.currentTimeMillis() kullanılır ve long döndürür
    // bu yüzden startTime ve elapsedTime 'ı long türünde tanımladık

    private long startTime;
    private long elapsedTime;

    private boolean running;
    private boolean paused;

    public SimulationState(){
        this.running = false;
        this.paused = false;
        this.collectedDirt = 0;
        this.elapsedTime = 0;
    }

    public void start(){
        this.running = true;
        this.paused = false; // önceden duraklatılmış olabilir, sıfırla
        this.startTime = System.currentTimeMillis();
    }

    public void pause(){
        this.running = false;
        this.paused = true;
        // toplam geçen zaman tutulur;
        this.elapsedTime += System.currentTimeMillis() - this.startTime;
    }

    public void resume(){
        this.running = true;
        this.paused = false;
        // pause edilmişken (yani tekrar çalıştırılana kadar)
        // geçen süreyi elapsed time'da tutmamak için startTime burda güncellenyior
        this.startTime = System.currentTimeMillis();
    }
    public void reset(){
        this.running = false;
        this.paused = false;
        this.elapsedTime = 0;
        this.collectedDirt = 0;
    }
    public void incrementDirt(){
        this.collectedDirt++;
    }

    public boolean isRunning() {return running;}
    public boolean isPaused() {return paused;}

    public int getCollectedDirt() {return collectedDirt;}

    public long getTotalElapsedTime() {
        if(running && !paused)
            // çalışıyorsa anlık süreyi de ekle
            return elapsedTime + (System.currentTimeMillis() - startTime);
        return elapsedTime;
    }
    public String getFormattedTime() {
        long totalSeconds = getTotalElapsedTime() / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}

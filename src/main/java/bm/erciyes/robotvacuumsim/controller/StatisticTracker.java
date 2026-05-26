package bm.erciyes.robotvacuumsim.controller;

import bm.erciyes.robotvacuumsim.model.Room;
import bm.erciyes.robotvacuumsim.model.SimulationState;

public class StatisticTracker {
    private Room room;
    private SimulationState state;
    private int initialDirtCount; // başlangıçtaki kir sayısı

    public StatisticTracker(Room room, SimulationState state){
        this.room = room;
        this.state = state;
        // başlangıçta kaç kirli hücre var, kaydet
        this.initialDirtCount = room.getDirtyCells();
    }

    // robotun gezdiği alan yüzdesi
    public double getCleanedPercentage() {
        // ziyaret edilen hücre / toplam temizlenebilir hücre * 100
        return (double) room.getVisitedCells() / room.getTotalCleanableCells() * 100;
    }

    // gezilmemiş alan yüzdesi
    public double getRemainingPercentage() {
        return (double) 100 - getCleanedPercentage();
    }

    // toplanan kir yüzdesi
    public double getDirtCollectedPercentage() {
        if (initialDirtCount == 0) return 0; // sıfıra bölme hatası önlendi
        return (double) state.getCollectedDirt() / initialDirtCount * 100;
    }

    // geçen süre — "02:15" formatında
    public String getElapsedTime() {
        return state.getFormattedTime();
    }

    // toplanan kir sayısı
    public int getCollectedDirt() {
        return state.getCollectedDirt();
    }
}

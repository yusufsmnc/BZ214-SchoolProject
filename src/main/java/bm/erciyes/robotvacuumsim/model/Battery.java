package bm.erciyes.robotvacuumsim.model;

public class Battery {
    private int level;
    private static final int MAX = 100;
    private static final int LOW_THRESHOLD = 20;

    public Battery(){
        // ilk deger contructor ile MAX'a atandı
        this.level = MAX;
    }

    public void drain (int amount){
        // sarj azaltılıyor
        this.level = Math.max(0,level - amount);
        // sıfırın altına düşmesin diye MAX kullanıldı
    }

    public void charge(){
        // MAX'a şarj ediyor
        this.level = MAX;
    }

    public boolean isLow(){
        // sarj seviyesini kontrol eder
        return this.level <= LOW_THRESHOLD;
    }

    public boolean isDead(){
        // şarj varmı yokmu kontol eder
        return this.level == 0;
    }

    public void setLevel(int level) {
        // sarj değeri kontrollü ataması gerçekleştirilir
        if (level < 0)  // 0'dan küçük girilirse; 0 döndürülür
            this.level = 0;
        else this.level = Math.min(level, MAX); // 100 den büyük girilirse MAX döndürülür
    }
    public int getLevel() {return level;} // level değerini iletir
    public int getMax() {return MAX;} // MAX değerini iletir


}

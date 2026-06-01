package bm.erciyes.robotvacuumsim.model;

public class Battery {
    private int level;
    private static final int MAX = 100;  // static final çünkü tüm Battery nesneleri için aynı ve değişmez

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

    public void setLevel(int level) {
        // sarj değeri kontrollü ataması gerçekleştirilir
        if (level < 0)  // 0'dan küçük girilirse; 0 döndürülür
            this.level = 0;
        else this.level = Math.min(level, MAX); // 100 den büyük girilirse MAX döndürülür
    }
    public int getLevel() {return level;} // level değerini iletir

}

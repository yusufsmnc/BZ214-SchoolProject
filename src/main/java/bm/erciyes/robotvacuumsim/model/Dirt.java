package bm.erciyes.robotvacuumsim.model;

import bm.erciyes.robotvacuumsim.util.DirtType;

public abstract class Dirt {

    // Dust, Liquid, Stain birbirinden farklı davranır ama hepsi "kir"dir.
    // Ortak alanları burada tanımlayıp alt sınıflara miras bırakıyoruz.
    // Dirt nesnesi direkt oluşturulamaz, sadece alt sınıflardan oluşturulur.
    // Protected yapılmasının sebebi alt sınıfların erişebilmesi içindir

    // Kir türü — DUST, LIQUID veya STAIN
    protected DirtType type;

    // Toplam kaç tick sürer temizlemek
    protected int cleaningTime;

    // Temizlerken ekstra ne kadar batarya harcar
    protected int batteryCost;

    // Kalan temizleme süresi — her tick'te azalır
    protected int remainingTime;

    // Her tick'te çağrılır — alt sınıflar implement eder
    // abstract çünkü her kir türü farklı davranabilir
    public abstract void clean();

    // Temizlendi mi? — remainingTime sıfırsa bitti
    public boolean isClean() {
        return remainingTime <= 0;
    }

    public DirtType getType() { return type; }
    public int getBatteryCost() { return batteryCost; }
}
package bm.erciyes.robotvacuumsim.model;

/*
OOP prensibine uygun olması ve
ileride şarj hızı, şarj kapasitesi gibi özellikler eklenebilmesi için ayrı bir sınıf olarak yazıldı
 */

public class ChargingStation {
    private int x;
    private int y;

    public ChargingStation(int x, int y){
        // station koordinatları belirleniyor
        this.x = x;
        this.y = y;

    }

    // SimulationController'da istasyona dönüş yolu hesabında,
    // RoomPane'de istasyon ikonunu çizmek için kullanılır
    public int getX() {return x;}
    public int getY() {return y;}
}

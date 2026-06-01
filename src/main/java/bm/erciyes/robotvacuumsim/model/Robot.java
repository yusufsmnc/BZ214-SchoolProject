package bm.erciyes.robotvacuumsim.model;

import bm.erciyes.robotvacuumsim.util.Direction;
import bm.erciyes.robotvacuumsim.util.RobotStatus;

import java.util.ArrayList;
import java.util.List;

public class Robot {

    // prevX, prevY robotun bir önceki konumu, animateRobotMove() için gerekli nereden nereye gittiğini bilmek için
    private int prevX;
    private int prevY;

    private int x;
    private int y;
    private Direction dir;
    private Battery bat;

    //  robotun durumu. MOVING, CLEANING, CHARGING, RETURNING, IDLE. UI'da gösterilir ve SimulationController'da karar vermede kullanılır
    private RobotStatus robotStatus;

    // esneklik olması için list yapıldı
    // ilerde linkedlist yapmam gerekirse, constructorda arraylist yerine linkedlist yazarım
    private List<int[]> path;


    public Robot(int x, int y){
        this.x = x;
        this.y = y;
        this.dir = Direction.EAST;
        this.bat = new Battery();
        this.robotStatus = RobotStatus.IDLE;
        this.path = new ArrayList<>(); // liste oluşturuldu
        this.path.add(new int[]{x,y});  // ilk konum listeye eklendi
    }
    public void move(Direction dir) {
        /*
        4 şey yapar: prevX/prevY günceller, yönü set eder, koordinatları günceller, bataryayı azaltır, path'e ekler
        */
        prevX = x;
        prevY = y;
        this.dir = dir;

        // dx ve dy ile konum güncellendi — switch'e gerek kalmadı
        this.x += dir.getDx();
        this.y += dir.getDy();

        // her harekette batarya azalır
        bat.drain(1);

        // geçilen hücre yola eklendi
        path.add(new int[]{x, y});
    }
    public void clean(Cell cell){
        /*
         kiri bir tick temizler, batarya harcar, kir bittiyse hücreden kaldırır
        */

        // hücrede kir var mı kontrol et
        if(cell.hasDirt()){

            // kiri bir tick temizle (remainingTime azalır)
            cell.getDirt().clean();

            // kir türüne göre ekstra batarya harca
            // Dust=1, Liquid=3, Stain=5
            bat.drain(cell.getDirt().getBatteryCost());

            // temizlendi mi? (remainingTime == 0)
            if(cell.getDirt().isClean())
                // kiri hücreden kaldır
                cell.removeDirt();
        }
    }

    public int getX() {return x;}
    public int getY() {return y;}

    public int getPrevX() { return prevX; }
    public int getPrevY() { return prevY; }

    public Direction getDir() {return dir;}
    public void setDir(Direction dir) {this.dir = dir;}

    public Battery getBat() {return bat;}
    public List<int[]> getPath() {return path;}

    public RobotStatus getRobotStatus() {return robotStatus;}
    public void setRobotStatus(RobotStatus robotStatus) {this.robotStatus =robotStatus;}


}

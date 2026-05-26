module bm.erciyes.robotvacuumsim {
    requires javafx.controls;
    requires javafx.fxml;

    opens bm.erciyes.robotvacuumsim to javafx.graphics, javafx.fxml;
    opens bm.erciyes.robotvacuumsim.view to javafx.fxml;
    opens bm.erciyes.robotvacuumsim.controller to javafx.fxml;

    exports bm.erciyes.robotvacuumsim;
    exports bm.erciyes.robotvacuumsim.view;
    exports bm.erciyes.robotvacuumsim.controller;
}
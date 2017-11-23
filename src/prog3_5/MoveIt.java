package prog3_5;

/**
 * Created by tabea sometime in 2017
 */
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.stage.Stage;

public class MoveIt extends Application {

    // Class TimeIt to start a Timer doesn't work yet
    public class TimeIt implements Runnable {
        
        TimeIt(){
            
        }

        @Override
        public void run() {
            Timer t = new Timer();
            t.startTimer(00);
            while (BUBBLECOUNT > 0) {
                lb.setText(t.toString());    // supposed to update the label but doesnt
            }
        }

    }

    private List<MovingEllipse> ovals = new ArrayList<MovingEllipse>();     // List of Bällchen
    private Group group = new Group();  //root node for the play window     // Gruppe of Bällchen
    private final Random random = new Random();                             // Random für buntes
    private final Button bt = new Button("Restart");                        // Button
    private Label lb = new Label("Time: 00:00:00");                         // Label
    private Slider sl = new Slider();
    private final Label easy = new Label("easy");
    private final Label hard = new Label("hard");
   // Scene scene = new Scene(lb);
    public static double SPEED = 0.99;                                      // Abnahme Speed, je größer desto langsamer
    public static double GRAVITY = 0.05;                                    // Schwerkraft
    public int BUBBLECOUNT = 0;                                             // Zählt Bläschen auf Feld
    public int INITIAL_BUBBLES = 30;                                        // Legt Anfangsbläschenzahl fest
    public boolean MOVE = true;                                             // Bubblebewegung
    
    // main Methode
    public static void main(String[] args) {
        Application.launch(args);
    }

    // javafx start
    @Override
    public void start(Stage primaryStage) {
        final BorderPane borderPane;        // großes Layout
        final Scene scene;                  // große Szene
        final Pane pane;                    // Layoutmanager intern
        final FlowPane oben;                // Layoutmanager oben
        primaryStage.setTitle("Crazy Bubbles");
        
        //create a pane for a group with all moving objects
        pane = new Pane(group);
        pane.setPrefSize(500, 500);
        pane.setStyle("-fx-background-color: black;");
        
        //create a restart button
        bt.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                ovals.clear();              //clear List with references
                group.getChildren().clear();//clear all moving objects  
                int count = 0;
                do {
                    generate((new Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), random.nextFloat())), random.nextFloat() + pane.getWidth() / 2, random.nextFloat() + pane.getHeight() / 2, random.nextFloat() * 25.0 + 20.0, true);
                    count++;
                    BUBBLECOUNT++;
                } while (count < INITIAL_BUBBLES);
                // TimeIt zeit = new TimeIt();
                // zeit.run();

            }
        });

        //create the main window layout
        borderPane = new BorderPane();
        oben = new FlowPane();
        oben.getChildren().add(bt);     // button und label ins Layout 
        oben.getChildren().add(lb);
        oben.getChildren().add(easy);
        oben.getChildren().add(sl);
        oben.getChildren().add(hard);
        borderPane.setTop(oben);
        borderPane.setCenter(pane);

        scene = new Scene(borderPane, 500, 500, Color.ANTIQUEWHITE);
        primaryStage.setScene(scene);
        
        //set pane autoresizable
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                pane.setPrefWidth(scene.getWidth());
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                pane.setPrefHeight(scene.getHeight());
            }
        });
        
        // Bläschen aufschrecken bei Mausklick
        pane.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public synchronized void handle(MouseEvent e) {
                System.out.println(BUBBLECOUNT);    // Debug
                if (MOVE) {
                    for (int i = 0; i < ovals.size(); i++) {    // Setzt Punkte für Rand und Mausklick
                        double radius = ovals.get(i).getEllipse().getRadiusX();
                        double rechts = ovals.get(i).getEllipse().getCenterX() + radius;
                        double links = ovals.get(i).getEllipse().getCenterX() - radius;
                        double unten = ovals.get(i).getEllipse().getCenterY() + radius;
                        double oben = ovals.get(i).getEllipse().getCenterY() - radius;
                        double x = e.getX();
                        double y = e.getY();

                        // schnelle Richtungsänderung wenn zu nah an Bläschen dran
                        if (rechts < x && rechts + 30 >= x && oben - 30 <= y && unten + 30 >= y) {
                            ovals.get(i).setStepX(ovals.get(i).getStepX() - 5);
                        }
                        if (links > x && links - 30 <= x && oben - 30 <= y && unten + 30 >= y) {
                            ovals.get(i).setStepX(ovals.get(i).getStepX() + 5);
                        }
                        if (unten < y && unten + 30 >= y && links - 30 <= x && rechts + 30 >= x) {
                            ovals.get(i).setStepY(ovals.get(i).getStepY() - 5);
                        }
                        if (oben > y && oben - 30 <= y && links - 30 <= x && rechts + 30 >= x) {
                            ovals.get(i).setStepY(ovals.get(i).getStepY() + 5);
                        }
                    }

                }
            }
        }
        );
        primaryStage.show();

        new AnimationTimer() { //animate all circles
            @Override
            public void handle(long now
            ) {
                for (MovingEllipse e : ovals) {
                    e.setStepY(e.getStepY() + GRAVITY); //needs + GRAVITY for gravity

                    if (e.getEllipse().getCenterX() + e.getEllipse().getRadiusX() >= pane.getPrefWidth()
                            || e.getEllipse().getCenterX() - e.getEllipse().getRadiusX() <= 0) {
                        e.setStepX(e.getStepX() * -1 * SPEED);
                        e.setStepY(e.getStepY() * SPEED);
                    }

                    if (e.getEllipse().getCenterY() + e.getEllipse().getRadiusY() >= pane.getPrefHeight() - bt.getHeight()
                            || e.getEllipse().getCenterY() - e.getEllipse().getRadiusY() <= 0) {
                        e.setStepX(e.getStepX() * SPEED);
                        e.setStepY(e.getStepY() * -1 * SPEED);
                    }
                    // normal case
                    e.getEllipse().setCenterX(e.getEllipse().getCenterX() + e.getStepX());
                    e.getEllipse().setCenterY(e.getEllipse().getCenterY() + e.getStepY());
                }
            }
        }
                .start();
    }

    // eine neue Ellipse
    private void generate(Color c, Double x, Double y, Double radius, boolean clickable) {
        Ellipse localCircle = new Ellipse(x, y, radius, radius);
        localCircle.setStrokeWidth(3);
        localCircle.setStroke(Color.WHITE);
        localCircle.setFill(c);
        if (clickable) {  //add event handler to delete on click
            localCircle.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent e) { //delete the cirlce
                    group.getChildren().remove(localCircle);
                    BUBBLECOUNT--;
                }
            });
        }

        // set direction of flow
        double direction = random.nextDouble();
        if (direction > 0.75) {
            ovals.add(new MovingEllipse(localCircle, random.nextDouble() * 2, random.nextDouble() * 2));
            group.getChildren().add(localCircle); //add obect to the group
        } else if (direction > 0.5) {
            ovals.add(new MovingEllipse(localCircle, random.nextDouble() * -2, random.nextDouble() * 2));
            group.getChildren().add(localCircle); //add obect to the group
        } else if (direction > 0.25) {
            ovals.add(new MovingEllipse(localCircle, random.nextDouble() * -2, random.nextDouble() * -2));
            group.getChildren().add(localCircle); //add obect to the group
        } else {
            ovals.add(new MovingEllipse(localCircle, random.nextDouble() * 2, random.nextDouble() * -2));
            group.getChildren().add(localCircle); //add obect to the group
        }
    }

    // Bläschen
    private class MovingEllipse {

        private double stepX; //
        private double stepY;
        private Ellipse c; //reference on a circle

        MovingEllipse(Ellipse c, double dx, double dy) {
            this.c = c;
            stepX = dx;
            stepY = dy;
        }

        public double getStepX() {
            return stepX;
        }

        public void setStepX(double stepX) {
            this.stepX = stepX;
        }

        public double getStepY() {
            return stepY;
        }

        public void setStepY(double stepY) {
            this.stepY = stepY;
        }

        public Ellipse getEllipse() {
            return c;
        }

        public void setEllipse(Ellipse c) {
            this.c = c;
        }

        double getDistance(Ellipse e) {
            double x = c.getCenterX() - e.getCenterX();
            double y = c.getCenterY() - e.getCenterY();
            return Math.sqrt(x * x + y * y);
        }

        double getDistance(double coordX, double coordY) {
            double x = c.getCenterX() - coordX;
            double y = c.getCenterY() - coordY;
            return Math.sqrt(x * x + y * y);
        }
    }
}

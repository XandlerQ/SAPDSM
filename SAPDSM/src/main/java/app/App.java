package app;

import aviary.Aviary;

import controlP5.*;
import processing.core.*;

import java.awt.*;

public class App extends PApplet {

    public static PApplet processingRef;

    ControlP5 cp5;
    Aviary AV;

    boolean pause = false;
    boolean firstRun = true;
    boolean autoRun = true;

    int screenshotNum = 1;
    int scrShCounter = 0;

    public void settings() {
        size(2000, 1200);

    }

    public void setup() {
        frameRate(1000);
        processingRef = this;
        background(0);
        ellipseMode(CENTER);

        int bSSX = 250;
        int bSSX2 = 250;
        int bSSY = 10;
        int bGap = 23;

        int sSSX = 200;
        int sSSY = 8;
        int sGap = 18;


        cp5 = new ControlP5(this);

        cp5.addBang("BgStart")
                //.setLabel("Старт")
                .setPosition(65, configuration.Render.ORIGINY + configuration.Aviary.DEFY + 20)
                .setSize(45, 30);

        cp5.addBang("BgPause")
                //.setLabel("Пауза")
                .setPosition(65, configuration.Render.ORIGINY + configuration.Aviary.DEFY + 70)
                .setSize(45, 30);

        cp5.addBang("BgScreenshot")
                .setPosition(65, configuration.Render.ORIGINY + configuration.Aviary.DEFY + 120)
                .setSize(45, 30);

        PFont font = createFont("PressStart2P-Regular.ttf", 8);
        textFont(font);
    }

    public void BgStart() {
        background(0);
        AV = new Aviary();
        AV.initialize();

        firstRun = false;

        if (pause) {
            fill(0, 100);
            stroke(0, 0);
            rect(configuration.Render.ORIGINX, configuration.Render.ORIGINY, configuration.Aviary.DEFX, configuration.Aviary.DEFY);
            fill(Color.WHITE.getRGB());
            textSize(20);
            text("PAUSE", configuration.Aviary.DEFX / 2 - 40, configuration.Aviary.DEFY / 2);
        }
    }


    public void BgPause() {
        if (pause)
            pause = false;
        else {
            if (!firstRun) {
                fill(0, 80);
                stroke(0, 0);
                rect(configuration.Render.ORIGINX, configuration.Render.ORIGINY, configuration.Aviary.DEFX, configuration.Aviary.DEFY);
                fill(Color.WHITE.getRGB(), 100);
                rect(configuration.Render.ORIGINX + 5, configuration.Render.ORIGINY + 5, 12, 30);
                rect(configuration.Render.ORIGINX + 25, configuration.Render.ORIGINY + 5, 12, 30);
                pause = true;
            }
        }
    }


    public void BgScreenshot() {
        saveFrame("Screenshots/Aviary_Run-" + screenshotNum + ".png");
        screenshotNum++;
        scrShCounter = 120;
    }

    public void draw() {
        pushMatrix();

        boolean finished = false;

        if (!pause) {
            if (AV != null) {
                background(0);
                finished = AV.run();
                fill(Color.WHITE.getRGB());
                text((int) (frameRate), 5, 10);
            }
        }

        if (finished) {
            if (autoRun) {
//                App.BASERES += 0.05;
//                App.RESREPSPEED = App.BASERES / App.RESREPSPEEDMULTIPLIER;
//                if (App.BASERES > 1.001) {
//                    App.BASERES = 0.1;
//                    App.RESREPSPEEDMULTIPLIER += 30;
//                    App.RESREPSPEED = App.BASERES / App.RESREPSPEEDMULTIPLIER;
//                    if (App.RESREPSPEEDMULTIPLIER > 241) {
//                        BgPause();
//                    }
//                    else {
//                        BgStart();
//                    }
//                }
//                else {
//                    BgStart();
//                }
            } else {
                BgPause();
            }
        }

        if (scrShCounter > 0) {
            scrShCounter--;
            fill(Color.WHITE.getRGB(), 100);
            stroke(Color.WHITE.getRGB(), 255);
            rect(1200 - 47 + 5, configuration.Render.ORIGINY + configuration.Aviary.DEFY + 170, 2, 15);
            triangle(1200 - 50 + 5, configuration.Render.ORIGINY + configuration.Aviary.DEFY + 185, 1200 - 42 + 5, configuration.Render.ORIGINY + configuration.Aviary.DEFY + 185, 1200 - 46 + 5, configuration.Render.ORIGINY + configuration.Aviary.DEFY + 195);
            rect(1200 - 55 + 5, configuration.Render.ORIGINY + configuration.Aviary.DEFY + 195, 20, 2);
        }

        popMatrix();
    }

    public void keyPressed() {
        switch (key) {
            case 'r':
            case 'R':
                firstRun = false;
                background(0);
                AV = new Aviary();
                AV.initialize();
                if (pause) {
                    fill(0, 80);
                    stroke(0, 0);
                    rect(configuration.Render.ORIGINX, configuration.Render.ORIGINY, configuration.Aviary.DEFX, configuration.Aviary.DEFY);
                    fill(Color.WHITE.getRGB(), 100);
                    rect(configuration.Render.ORIGINX + 5, configuration.Render.ORIGINY + 5, 12, 30);
                    rect(configuration.Render.ORIGINX + 25, configuration.Render.ORIGINY + 5, 12, 30);
                }
                break;
            case ' ':
                if (pause)
                    pause = false;
                else {
                    if (!firstRun) {
                        fill(0, 80);
                        stroke(0, 0);
                        rect(configuration.Render.ORIGINX, configuration.Render.ORIGINY, configuration.Aviary.DEFX, configuration.Aviary.DEFY);
                        fill(Color.WHITE.getRGB(), 100);
                        rect(configuration.Render.ORIGINX + 5, configuration.Render.ORIGINY + 5, 12, 30);
                        rect(configuration.Render.ORIGINX + 25, configuration.Render.ORIGINY + 5, 12, 30);
                        pause = true;
                    } else {
                        firstRun = false;
                        background(0);
                        AV = new Aviary();
                        AV.initialize();
                        pause = false;
                    }
                }
                break;
            case 's':
            case 'S':
                saveFrame("Screenshots/Aviary_Run-" + screenshotNum + ".png");
                screenshotNum++;
                scrShCounter = 120;
                break;
        }
    }

//    public void mouseClicked() {
//        if(AV != null) System.out.println(AV.getPropertyGrid().getPropertyAreaIndex(mouseX - App.ORIGINX, mouseY - App.ORIGINY));
//    }

    public static void main(String... args) {
        PApplet.main("app.App");
    }
}
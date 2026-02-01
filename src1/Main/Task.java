package Main;

import Display.*;


public class Task {
    public void load(int seconds) {
        int milliseconds = seconds * 1000;
        int interval = milliseconds / 3;
        TextDisplay text = new TextDisplay();

        System.out.print("\t\t");

        for (int i = 0; i < 3; i++) {
            text.yellowTextV2(".");
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println();
    }

    public void delay(int seconds){
        int milliseconds = seconds * 1000;

        try{
            Thread.sleep(milliseconds);
        }
        catch(InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }
}


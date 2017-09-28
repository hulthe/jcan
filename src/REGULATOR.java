import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import static java.lang.Thread.interrupted;
import static java.lang.Thread.sleep;

/**
 * Created by Macken on 2017-09-25.
 */
public class REGULATOR implements Observer {

    double v1;
    double vDes;
    double accFactor;
    double a0;
    double k;
    double i;
    double i_acc;
    double dist1;
    double dist2;
    double deltaDist;
    double d;
    double lastEr;
    int s;

    public REGULATOR () {

        v1 = 0;
        a0 = 0.40;
        k = 236;
        s = 0;
        i = 0.05;
        i_acc = 0;
        d = 0.2;
        lastEr = 0;

        deltaDist = 0.40;

        dist1 = a0;
        dist2 = 0;
        Sim sim = new Sim(this);

        new Thread(sim).start();

    }
    private double readSensor(double s){
        dist1 += (s/(3.6*40));
        dist2 += (v1/(3.6*40));

        return dist1 - dist2;
    }

    private void calcNewSpeed (double speed2) {

        deltaDist = readSensor(speed2);
        double error = -0.40 + deltaDist;
        i_acc += error * i;

        vDes = error * k; //+ i_acc + (-error +lastEr) * d;
        v1 += accFactor * (vDes - v1);

        lastEr = error;
    }

    @Override
    public void update(Observable o, Object arg) {
        s++;
        double s2 = (double)arg;
        calcNewSpeed(s2);
        if (s % 40 == 0) {
            System.out.println("Car 1 has traveled: " + dist1 + ". Car 2 has traveled: " + dist2 + ". The sensors reads: " + deltaDist);
            System.out.println("Car 2 now travel at: " + v1);
        }
    }

    private class Sim extends Observable implements Runnable {

       double speed2;
       Random r;

        public Sim(Observer o){

            speed2 = 5;
            r = new Random();
            this.addObserver(o);


       }

        public void run(){

            while(!interrupted())
            try {
                speed2 = 5;
                setChanged();
                notifyObservers(speed2);

                sleep(2, 500);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

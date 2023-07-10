import java.util.Random;

import static java.lang.Thread.sleep;

public class DeskTask implements Runnable
{
    public static Random  rand = new Random();
    @Override
    public void run() {
        try {
            sleep(rand.nextInt(500));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

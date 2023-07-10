
import VitoBarra.JavaUtil.ServerHelper.NonBlockingLineReader;
import VitoBarra.JavaUtil.String.StringUtil;

import java.util.ArrayList;
import java.util.Random;

public class Main {
    private static PostOffice uffice;
    private static final int MAX_TASK = 10000;
    private static final int INITIAL_TASK = 10000;

    private static boolean Terminate = false;

    public static void main(String[] args) {


        var CommandThread = new Thread(Main::ConsoleCommandHandler);
        CommandThread.setDaemon(true);
        CommandThread.start();

        var Tasks = new ArrayList<Runnable>();
        for (int i = 0; i < INITIAL_TASK; i++)
            Tasks.add(new DeskTask());


        uffice = new PostOffice(5, 25);
        uffice.AcceptFirstHall(Tasks);

        var TaskProducer = new Thread(Main::AddTaskToOffice);
        TaskProducer.start();
    }


    public static void AddTaskToOffice() {
        Random random = new Random();
        while (true) {
            try {
                Thread.sleep(random.nextInt(500));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (Terminate) {

                uffice.CloseDesk();
                break;
            }
            uffice.AcceptFirstHall(new DeskTask());
        }
    }

    public static void ConsoleCommandHandler() {
        System.out.print("Menu:\n X,x,C,c) Close Task Producer\n>");
        NonBlockingLineReader lr = new NonBlockingLineReader(System.in);
        while (true) {
            String StringRead = lr.TryRead();
            if (StringUtil.IsEqualToAny(StringRead, "X", "x", "C", "c")) {
                System.out.println("ShutDown begin");
                Terminate = true;
                lr.close();
                break;
            } else System.out.println("Command Not Found");
        }
    }
}
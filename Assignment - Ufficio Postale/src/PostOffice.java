
import VitoBarra.JavaUtil.Other.ThreadPoolUtil;

import java.util.List;
import java.util.concurrent.*;

public class PostOffice {
    private final int SmallHallSpace;
    private final BlockingQueue<Runnable> FirstHallQueue;
    private final ThreadPoolExecutor DeskThreadPool;

    public PostOffice(int NumberOfDesks, int _smallHallSpace) {
        SmallHallSpace = _smallHallSpace;
        FirstHallQueue = new LinkedBlockingQueue<>();

        DeskThreadPool = new ThreadPoolExecutor(0, NumberOfDesks, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(_smallHallSpace));
        DeskThreadPool.prestartAllCoreThreads();

        var TaskHandler = new Thread(this::ReceiveTasksWorker);
        TaskHandler.setDaemon(true);
        TaskHandler.start();
    }

    public void AcceptFirstHall(Runnable task) {
        FirstHallQueue.add(task);
    }

    public void AcceptFirstHall(List<Runnable> tasks) {
        FirstHallQueue.addAll(tasks);
    }

    public synchronized void CloseDesk() {
        System.out.println("Office is Closing...");
        DeskThreadPool.shutdown();
        ThreadPoolUtil.TryAwaitTermination(DeskThreadPool,90,TimeUnit.SECONDS);
    }

    private void ReceiveTasksWorker() {
        while (true) {
            if (SmallHallSpace - DeskThreadPool.getQueue().size() > 0) {
                Runnable DeskTask;
                try {
                    DeskTask = FirstHallQueue.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                try {
                    DeskThreadPool.execute(DeskTask);
                } catch (RejectedExecutionException e) {
                    break;
                }
            }

            Thread.yield();
        }
    }
}

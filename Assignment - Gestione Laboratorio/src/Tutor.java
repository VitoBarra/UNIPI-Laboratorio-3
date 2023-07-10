
import VitoBarra.JavaUtil.Other.ThreadPoolUtil;

import java.util.AbstractQueue;
import java.util.concurrent.*;

public class Tutor
{
    private final Laboratory Laboratory;

    public Laboratory getLaboratory()
    {
        return Laboratory;
    }

    private final AbstractQueue<LaboratoryUser> Requests = new PriorityBlockingQueue<>(10, (o1, o2) -> Integer.compare(o2.Priority, o1.Priority));
    private final ScheduledExecutorService AssignWorker;
    private final ExecutorService UserUsage;


    public  Tutor(Laboratory _lab)
    {
        Laboratory = _lab;
        AssignWorker = Executors.newSingleThreadScheduledExecutor();
        AssignWorker.scheduleAtFixedRate(this::Assign, 500, 500, TimeUnit.MILLISECONDS);


        UserUsage = Executors.newCachedThreadPool();
    }

    public void MakeRequest(LaboratoryUser user)
    {
        if (Requests.contains(user)) return;
        Requests.add(user);
    }

    public void ReleasePosition(LaboratoryUser user)
    {
        Laboratory.ReleaseOccupiedPositions(user);
    }

    public void Assign()
    {
         var Failed=new LinkedBlockingDeque<LaboratoryUser>();
        while(Requests.size()>0)
        {
            var User = Requests.poll();

            if (Laboratory.TryAcquirePosition(User))
                UserUsage.execute(User);
            else
                Failed.add(User);
        }
        Requests.addAll(Failed);

    }


    public synchronized void Close()
    {
        System.out.println("Tutor Is Closing...");
        ThreadPoolUtil.TryAwaitTermination(AssignWorker, 10, TimeUnit.SECONDS);
        ThreadPoolUtil.TryAwaitTermination(UserUsage, 10, TimeUnit.SECONDS);
    }


}

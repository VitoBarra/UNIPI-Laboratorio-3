
import VitoBarra.JavaUtil.Other.Lock1Cond;

import java.util.concurrent.ThreadLocalRandom;

public abstract class LaboratoryUser implements Runnable
{
    protected int UseTimes;
    protected Tutor Tutor;
    protected int Priority;
    protected Laboratory lab;
    protected Request Request;

    private Computer Held;
    private int PositionNumber = -2;

    private final String Name;
    private final String UserType;
    private final Lock1Cond l1c;

    public LaboratoryUser(int _priority, int _useTimes, Tutor tutor, String _UserType, String _name, Lock1Cond _l1c)
    {
        Priority = _priority;
        UseTimes = _useTimes;
        Tutor = tutor;
        UserType = _UserType;
        Name = _name;
        l1c = _l1c;
        lab = Tutor.getLaboratory();
    }

    //    public abstract boolean TryAcquirePosition();

    @Override
    public void run()
    {
        TaskStartsToRunPrint();
        var ran = ThreadLocalRandom.current();
        var time = ran.nextInt(10000);
        try
        {
            Thread.sleep(1000, time);
        } catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
        TaskEndedPrint(time);

        Tutor.ReleasePosition(this);

        if (--UseTimes > 0)
            MakeRequest();
        else
            l1c.SignalAll();
    }


    public Request GetRequest()
    {
        return Request;
    }

    public void MakeRequest()
    {
        Tutor.MakeRequest(this);
    }


    void TaskStartsToRunPrint()
    {
        System.out.println(GetUserName() + ": begin, have " + UseTimes + " time left");
    }

    void TaskEndedPrint(int WaitingTime)
    {
        System.out.println(GetUserName() + ": finish after sleep for: " + WaitingTime + "ms and released PC " + PositionNumber);
    }

    public String GetUserName()
    {
        return UserType + " " + Name;
    }

    public Computer GetPC() {return Held;}

    public void ReleaseHeld()
    {
        if (Held != null)
        {
            Held.ReleasePosition();
            Held = null;
            PositionNumber = -1;
        }
        else
            System.out.println("ERROR: TRIED TO TAKE A PC FROM A USER WHO DIDN’T HAVE ONE");
    }

    public void HoldComputer(Computer computer, int positionNumber)
    {
        if (Held == null)
        {
            Held = computer;
            Held.AcquirePosition();
            PositionNumber = positionNumber;
        }
        else
            System.out.println("ERROR: ATTEMPT TO ASSIGN A PC TO A USER ALREADY ASSIGNED");
    }
}


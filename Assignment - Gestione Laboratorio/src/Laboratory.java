import java.util.ArrayList;
import java.util.List;

public class Laboratory
{
    private final List<Computer> Computers;

    Laboratory()
    {
        Computers = new ArrayList<>();
        for (int i = 0; i < 20; i++)
            Computers.add(new Computer());
    }


    public synchronized void ReleaseOccupiedPositions( LaboratoryUser user)
    {
        var Request = user.GetRequest();

        if (Request.RequestType == RequestType.All)
        {
            for (int i = 0; i < Computers.size(); i++)
            {
                Computer Computer = Computers.get(i);
                Computer.ReleasePosition();
                LaboratoryGUI.ReleasePosition(i);
            }

        }
        else
        {
            var Computer = user.GetPC();
            if (Computer != null)
            {
                user.ReleaseHeld();
                LaboratoryGUI.ReleasePosition(Computers.indexOf(Computer));
            }
        }
        notifyAll();
    }


    public boolean TryAcquirePosition( LaboratoryUser a)
    {
        var Request = a.GetRequest();

        return switch (Request.RequestType)
                {
                    case All ->
                    {
                        try
                        {
                            yield AcquireAllPosition(a);
                        } catch (InterruptedException e)
                        {
                            throw new RuntimeException(e);
                        }
                    }
                    case Any -> AcquireAnyPosition(a);
                    case Specific -> AcquirePosition(Request.PositionNeeded, a);
                };
    }


    private synchronized boolean AcquireAnyPosition(LaboratoryUser user)
    {
        var Computer = isSomePositionFree();
        if (Computer == null) return false;

        var b = Computers.indexOf(Computer);
        user.HoldComputer(Computer, b);
        LaboratoryGUI.TakePosition(user.GetUserName(), b);
        return true;
    }

    private synchronized boolean AcquireAllPosition(LaboratoryUser user) throws InterruptedException
    {
        while (!IsAllPositionFree())
            wait();

        for (int i = 0; i < Computers.size(); i++)
        {
            Computer computer = Computers.get(i);
            computer.AcquirePosition();
            LaboratoryGUI.TakePosition(user.GetUserName(), i);
        }
        return true;
    }

    private synchronized boolean AcquirePosition(Integer positionNeeded, LaboratoryUser user)
    {
        if (positionNeeded == null || !isPositionFree(positionNeeded)) return false;
        var Computer = Computers.get(positionNeeded);
        user.HoldComputer(Computer, positionNeeded);
        LaboratoryGUI.TakePosition(user.GetUserName(), positionNeeded);
        return true;
    }

    private boolean isPositionFree(int positionNeeded)
    {
        return Computers.get(positionNeeded).IsFree();
    }

    private Computer isSomePositionFree()
    {
        return Computers.stream().filter(Computer::IsFree).findAny().orElse(null);
    }

    private boolean IsAllPositionFree()
    {
        return Computers.stream().allMatch(Computer::IsFree);
    }



}

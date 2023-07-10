public class Computer
{
    private boolean IsFree = true;

    public boolean IsFree() {return IsFree;}

    public void AcquirePosition()
    {
        if (IsFree)
            IsFree = false;
    }

    public  void ReleasePosition()
    {
        if (!IsFree)
            IsFree = true;

    }

}

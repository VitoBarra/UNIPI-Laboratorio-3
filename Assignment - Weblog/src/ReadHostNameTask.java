import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;

public class ReadHostNameTask implements Runnable
{
    public BlockingQueue<String> RisQueue;
    public final String IpString;
    public final String OtherHostInfo;

    public ReadHostNameTask(BlockingQueue<String> _risQueue, String _ipString, String _otherHostInfo)
    {
        RisQueue = _risQueue;
        IpString = _ipString;
        OtherHostInfo = _otherHostInfo;
    }

    @Override
    public void run()
    {
        InetAddress Address = null;
        try
        {
            Address = InetAddress.getByName(IpString);
            var RisString = Address.getHostName() + " - - " + OtherHostInfo;

//            System.out.println(RisString);
            RisQueue.add(RisString);
        } catch (UnknownHostException e)
        {
            throw new RuntimeException(e);
        }

    }


}

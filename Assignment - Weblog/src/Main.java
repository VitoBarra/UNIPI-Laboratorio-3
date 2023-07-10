

import VitoBarra.JavaUtil.IO.ConsoleInput;
import VitoBarra.JavaUtil.IO.FileUtil;
import VitoBarra.JavaUtil.Other.ThreadPoolUtil;
import VitoBarra.JavaUtil.Time.TimeUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public class Main
{
    public static void main(String[] args) throws IOException
    {
        String FilePath = ConsoleInput.Prompt("PathFile");

        var InputDataStr = FileUtil.ReadAllLine(FilePath).stream().map(x -> x.split(" - - ")).toList();


        long startTime = System.nanoTime();
        FileUtil.WriteAllLine(ReplaceWithHostName(InputDataStr), "Ris_File.txt", true);
        long elapsedTime = System.nanoTime() - startTime;

        System.out.println("non Threaded Time:  " + TimeUtil.TimeConverter(elapsedTime));

        startTime = System.nanoTime();
        FileUtil.WriteAllLine(ReplaceWithHostNameThread(InputDataStr), "Ris_File.txt", true);
        long elapsedTime2 = System.nanoTime() - startTime;

        System.out.println("Threaded Time:  " +TimeUtil.TimeConverter(elapsedTime2));

        System.out.println("Time Difference:  "+TimeUtil.TimeConverter(elapsedTime-elapsedTime2));
    }

    private static List<String> ReplaceWithHostNameThread(List<String[]> inputDataStr)
    {
        BlockingQueue<String> b = new LinkedBlockingQueue<>();
        ExecutorService ThreadPool = Executors.newCachedThreadPool();

        for (var s : inputDataStr)
            ThreadPool.execute(new ReadHostNameTask(b,s[0],s[1]));

        ThreadPoolUtil.TryAwaitTermination(ThreadPool,5, TimeUnit.MINUTES);

        return new LinkedList<>(b.stream().toList()) ;
    }

    private static List<String> ReplaceWithHostName(List<String[]> a) throws UnknownHostException
    {
        List<String> RisStr = new ArrayList<>();
        for (var str : a)
        {
            var Address = InetAddress.getByName(str[0]);
            var RisString = Address.getHostName() + " - - " + str[1];

//            System.out.println(RisString);
            RisStr.add(RisString);
        }
        return RisStr;
    }


}
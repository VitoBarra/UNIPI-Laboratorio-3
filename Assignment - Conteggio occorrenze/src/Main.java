

import VitoBarra.JavaUtil.IO.ConsoleInput;
import VitoBarra.JavaUtil.IO.FileUtil;
import VitoBarra.JavaUtil.IO.PathUtil;
import VitoBarra.JavaUtil.Other.ThreadPoolUtil;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main
{
    public static void main(String[] args) throws IOException
    {
        var FileList = PathUtil.FilesFromPaths(ConsoleInput.Prompt("paths divided by \",\""),",");

        ExecutorService Threadpool = Executors.newCachedThreadPool();

        var CharSet = new ConcurrentHashMap<Character, Integer>();


        for (var file : FileList)
            Threadpool.execute(new CountTask(CharSet, file));


        ThreadPoolUtil.TryAwaitTermination(Threadpool, 90, TimeUnit.SECONDS);


        StringBuilder s = new StringBuilder();
        for (var key : CharSet.keySet())
            s.append(key).append(",").append(CharSet.get(key)).append("\n");

        FileUtil.WriteFile("out.txt", s.toString());
    }




}
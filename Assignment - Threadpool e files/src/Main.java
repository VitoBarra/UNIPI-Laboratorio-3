

import VitoBarra.JavaUtil.IO.ConsoleInput;
import VitoBarra.JavaUtil.IO.PathUtil;
import VitoBarra.JavaUtil.Other.ThreadPoolUtil;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class Main
{
    public static final String FOLDER_HOLDER_NAME = "ZFile";
    private static final String FOLDER_NAME = FOLDER_HOLDER_NAME + "\\" + "ToZip";

    public static void main(String[] args)
    {
        //Find Arguments
        var FilesPath = PathUtil.FilesFromPaths(ConsoleInput.Prompt(""),",");
        //Setup Task
        var Tasks  = FilesPath.stream().map(File::getAbsolutePath).map(ZipTask::new).toList();
        //Thread Pool
        var FixedThreadPool = Executors.newFixedThreadPool(10);
        Tasks.forEach(FixedThreadPool::execute);


        ThreadPoolUtil.TryAwaitTermination(FixedThreadPool,90, TimeUnit.SECONDS);
    }




}

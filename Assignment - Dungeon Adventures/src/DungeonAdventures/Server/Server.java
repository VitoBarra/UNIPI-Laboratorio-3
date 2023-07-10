package DungeonAdventures.Server;

import VitoBarra.JavaUtil.Other.ThreadPoolUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

public class Server
{

    ServerSocket WelcomeSocket;
    ExecutorService ClientConnections;
    volatile Boolean CloseServe = false;

    public Server() throws IOException
    {
        ClientConnections = Executors.newCachedThreadPool();

        WelcomeSocket = new ServerSocket(3838);
        var SeverAccepter = new Thread(this::StartAccept);
        SeverAccepter.start();
    }

    public void StartAccept()
    {
        do
        {
            try
            {
                ClientConnections.execute(new Game(WelcomeSocket.accept()));
            } catch (RejectedExecutionException e)
            {
                System.out.println("can't handle connection");
            } catch (IOException e)
            {
                System.out.println("Closed!");
            }
        } while (!CloseServe);

    }


    public void Close()
    {
        System.out.println("Server Closing...");
        try
        {
            WelcomeSocket.close();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        CloseServe = true;

        ThreadPoolUtil.TryAwaitTermination(ClientConnections, 30, TimeUnit.SECONDS);
    }
}
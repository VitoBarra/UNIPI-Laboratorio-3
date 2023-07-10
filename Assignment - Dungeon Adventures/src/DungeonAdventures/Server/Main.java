package DungeonAdventures.Server;

import VitoBarra.JavaUtil.ServerHelper.NonBlockingLineReader;
import VitoBarra.JavaUtil.String.StringUtil;

import java.io.IOException;

public class Main
{
    private static Server Server;

    public static void main(String[] args) throws IOException
    {
        var CommandThread = new Thread(Main::ConsoleCommandHandler);
        CommandThread.setDaemon(true);
        CommandThread.start();

        Server = new Server();
    }

    public static void ConsoleCommandHandler()
    {
        System.out.print("Menu:\n X,x,C,c) Close server\n>");
        try (var lr = new NonBlockingLineReader(System.in))
        {
            while (true)
            {
                String StringRead = lr.TryRead();
                if (StringUtil.IsEqualToAny(StringRead, "X", "x", "C", "c"))
                {
                    Server.Close();
                    break;
                }
                else System.out.println("Command Not Found");
            }
        }
    }
}
package DungeonAdventures.Client;

import DungeonAdventures.InteractionProtocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client
{
    public Client()
    {
        var t = new Thread(this::Connect);
        t.start();
    }

    public void Connect()
    {
        InetAddress local = null;
        try
        {
            local = InetAddress.getLocalHost();
        } catch (UnknownHostException e)
        {
            throw new RuntimeException(e);
        }

        try (var server = new Socket(local, 3838))
        {
            var scanner = new Scanner(System.in);
            var FromServerReader = new BufferedReader(new InputStreamReader(server.getInputStream()));
            var ToServerWriter = new PrintWriter(server.getOutputStream(), true);
            while (true)
            {
                var c = FromServerReader.readLine();
                if (c == null)
                    break;
                var input = c.split(InteractionProtocol.CommandRegex);
                System.out.println(input[0]);
                if (input.length > 1 && input[1].equals(InteractionProtocol.ServerWaitForInput))
                    ToServerWriter.println(scanner.nextLine());
            }
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}

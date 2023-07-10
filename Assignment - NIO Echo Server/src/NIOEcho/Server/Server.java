package NIOEcho.Server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Server
{
    ByteBuffer ServerBuffer;

    boolean Closed = false;

    public Server() throws IOException
    {
        ServerBuffer = ByteBuffer.allocate(1024 * 4);
        var t = new Thread(() -> {
            try
            {
                ConfigureSelector();
            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        });
        t.start();
    }

    private void ConfigureSelector() throws IOException
    {
        try (var selector = Selector.open();
             var WelcomeChannel = ServerSocketChannel.open())
        {
            WelcomeChannel.bind(new InetSocketAddress(InetAddress.getLocalHost(), 3838));
            WelcomeChannel.configureBlocking(false);
            WelcomeChannel.register(selector, SelectionKey.OP_ACCEPT);


            while (!Closed)
            {
                int readyChannels = selector.select(1000);
                if (readyChannels == 0) continue;
                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                while (keyIterator.hasNext())
                {
                    SelectionKey key = keyIterator.next();
                    if (key.isAcceptable()) // a connection was accepted by a ServerSocketChannel.
                        RegisterClient(selector, WelcomeChannel);
                    else if (key.isReadable()) // a channel is ready for reading
                        EchoMessage(key);

                    keyIterator.remove();
                }
            }
        }
    }

    private void RegisterClient(Selector selector, ServerSocketChannel welcomeChannel) throws IOException
    {
        SocketChannel client = welcomeChannel.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
    }


    public void EchoMessage(SelectionKey key) throws
            IOException
    {

        SocketChannel Client = (SocketChannel) key.channel();
        Client.read(ServerBuffer);

        ServerBuffer.flip();
        byte[] bytes = new byte[ServerBuffer.remaining()];
        ServerBuffer.get(bytes);

        ServerBuffer.clear();
        var a =("echoed by server: " + new String(bytes)).getBytes();
        ServerBuffer.put(a);

        ServerBuffer.flip();
        Client.write(ServerBuffer);
        ServerBuffer.clear();

        key.cancel();
        Client.socket().close();

    }

    public void Close()
    {
        Closed = true;
    }
}





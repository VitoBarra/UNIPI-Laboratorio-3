import VitoBarra.JavaUtil.IO.PathUtil;
import VitoBarra.JavaUtil.String.StringUtil;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.List;

public class Main
{
    private static final String OUT_DIR = "outFile";

    public static void main(String[] args) throws IOException
    {
        var files = PathUtil.FilesFromPath("in");
        int kbn = 5;
        int bufferSize = 1024 * kbn;

        PathUtil.DeleteDir(OUT_DIR);
        PrintBunchMark("file Creation", "indirect buffer", "size: " + kbn + "kb",
                CopyWithChannelsBuffer(files, ByteBuffer.allocate(bufferSize)));
        PrintBunchMark("NO file Creation", "indirect buffer", "size: " + kbn + "kb",
                CopyWithChannelsBuffer(files, ByteBuffer.allocate(bufferSize)));

        PathUtil.DeleteDir(OUT_DIR);
        PrintBunchMark("file Creation", "direct buffer", "size: " + kbn + "kb",
                CopyWithChannelsBuffer(files, ByteBuffer.allocateDirect(bufferSize)));
        PrintBunchMark("NO file Creation", "direct buffer", "size: " + kbn + "kb",
                CopyWithChannelsBuffer(files, ByteBuffer.allocateDirect(bufferSize)));


        PathUtil.DeleteDir(OUT_DIR);
        PrintBunchMark("file Creation", "transfer to", "",
                CopyWithChannelsTransfer(files));
        PrintBunchMark("NO file Creation", "transfer to", "",
                CopyWithChannelsTransfer(files));


        PathUtil.DeleteDir(OUT_DIR);
        PrintBunchMark("file Creation", "Buffered Stream", "",
                CopyWithStream(files));
        PrintBunchMark("NO file Creation", "Buffered Stream", "",
                CopyWithStream(files));

        PathUtil.DeleteDir(OUT_DIR);
        PrintBunchMark("file Creation", "Manual Management", "size: " + kbn + "kb",
                CopyManualManagement(files,kbn));
        PrintBunchMark("NO file Creation", "Manual Management", "size: " + kbn + "kb",
                CopyManualManagement(files,kbn));


    }

    private static long CopyManualManagement(List<File> files,int bufferSize)
    {
        PathUtil.CreateDir(OUT_DIR);
        long startTime = System.nanoTime();

        for (var FileToRead : files)
        {
            var FileToWrite = new File(OUT_DIR + "\\" + FileToRead.toPath().getFileName());
            byte[] bytearray = new byte[1024*bufferSize];


            try (var fr = new FileInputStream(FileToRead);
                 var fw = new FileOutputStream(FileToWrite))
            {

                while (fr.read(bytearray) != -1)
                    fw.write(bytearray);

            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        return (System.nanoTime() - startTime) / 1000000;
    }

    private static long CopyWithStream(List<File> files)
    {

        PathUtil.CreateDir(OUT_DIR);
        long startTime = System.nanoTime();

        for (var FileToRead : files)
        {

            var FileToWrite = new File(OUT_DIR + "\\" + FileToRead.toPath().getFileName());


            try (var br = new BufferedInputStream(new FileInputStream(FileToRead));
                 var bw = new BufferedOutputStream(new FileOutputStream(FileToWrite)))
            {

                int str;
                while ((str = br.read()) != -1)
                    bw.write((char) str);

            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }


        }
        return (System.nanoTime() - startTime) / 1000000;
    }

    private static long CopyWithChannelsBuffer(List<File> files, ByteBuffer Buffer) throws IOException
    {
        var OptionSet = new HashSet<StandardOpenOption>();
        OptionSet.add(StandardOpenOption.WRITE);
        OptionSet.add(StandardOpenOption.CREATE);
        OptionSet.add(StandardOpenOption.TRUNCATE_EXISTING);
        PathUtil.CreateDir(OUT_DIR);

        long startTime = System.nanoTime();
        for (var file : files)
        {

            var pathFileToRead = file.toPath();
            var PathFileToWrite = new File(OUT_DIR + "\\" + pathFileToRead.getFileName()).toPath();

            try (var RFileC = FileChannel.open(pathFileToRead, StandardOpenOption.READ);
                 var WFileC = FileChannel.open(PathFileToWrite, OptionSet))
            {
                channelCopy1(RFileC, WFileC, Buffer);
            }
        }
        return (System.nanoTime() - startTime) / 1000000;
    }

    static void PrintBunchMark(String FileCreation, String Strategy, String Extra, long milliseconds)
    {
        System.out.format("%17s | %20s | %15s | %-15s |\n",
                FileCreation,
                StringUtil.center(Strategy, 20),
                StringUtil.center(Extra, 15),
                StringUtil.center(String.format("%-5d milliseconds", milliseconds), 15));
    }

    private static long CopyWithChannelsTransfer(List<File> files) throws IOException
    {

        PathUtil.CreateDir(OUT_DIR);

        long startTime = System.nanoTime();
        for (var pathFileToRead : files)
        {

            var PathFileToWrite = new File(OUT_DIR + "\\" + pathFileToRead.toPath().getFileName());

            RandomAccessFile fromFile = new RandomAccessFile(pathFileToRead, "r");
            FileChannel fromChannel = fromFile.getChannel();
            RandomAccessFile toFile = new RandomAccessFile(PathFileToWrite, "rw");
            FileChannel toChannel = toFile.getChannel();

            long position = 0, count = fromChannel.size();
            toChannel.transferFrom(fromChannel, position, count);
        }
        return (System.nanoTime() - startTime) / 1000000;
    }


    private static void channelCopy1(ReadableByteChannel src, WritableByteChannel dest, ByteBuffer buffer) throws IOException
    {
        while (src.read(buffer) != -1)
        {
            // Prepare the buffer to be drained
            buffer.flip();
            // Write to the channel; may block
            dest.write(buffer);
            // If partial transfer, shift remainder down
            // If buffer is empty, same as doing clear( )
            buffer.compact();
        }
        // EOF will leave buffer in fill state
        buffer.flip();
        // Make sure that the buffer is fully drained
        while (buffer.hasRemaining())
        {
            dest.write(buffer);
        }
    }
}
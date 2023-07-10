import VitoBarra.JavaUtil.IO.FileUtil;
import VitoBarra.JavaUtil.IO.PathUtil;

public class ZipTask implements Runnable
{
    private static final String FOLDER_NAME =  Main.FOLDER_HOLDER_NAME +"\\"+"Archive";

    private final String FileNamePath;

    public ZipTask(String _fileNamePath)
    {
        FileNamePath = _fileNamePath;
    }

    @Override
    public void run()
    {
        var fileName = PathUtil.GetFileNameWithoutExtension(FileNamePath);
        FileUtil.compressGzipFile(FileNamePath, FOLDER_NAME+"\\" +fileName + "_Gzipped.zip");
        System.out.println("Compresso il file: " + fileName);
    }

}

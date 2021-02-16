package EmmaFileSecure;

import java.io.*;

public interface FileStreamSecure {

    default File clientTempFile(String FileName, String FileExtension, File SecuredDirectory)
            throws IOException
    {
        File myTemp = null;

        //create temporary file in a hidden location on the client side
        myTemp.createTempFile(FileName, FileExtension, SecuredDirectory);
        myTemp.deleteOnExit();

        return myTemp;
    }

    default OutputStream WriteToTempFile(File myTemp)
            throws IOException
    {
        FileOutputStream tempWriter = new FileOutputStream(myTemp);
        BufferedOutputStream writeToTempFile = new BufferedOutputStream(tempWriter);

        return writeToTempFile;
        //remember to close this...

    }

    default InputStream ReadFromTempFile(File myTemp)
            throws IOException
    {
        FileInputStream tempReader = new FileInputStream(myTemp);
        BufferedInputStream readFromTempFile = new BufferedInputStream(tempReader);

        return readFromTempFile;
        //remember to close this....
    }

}

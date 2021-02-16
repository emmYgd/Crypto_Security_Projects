package EmmaFileSecure;

import java.nio.file.*;
import java.io.*;

public class FileMonitor implements EmmaFileSecure.FileMask {

    protected WatchService realWatchService() throws IOException{
         FileSystem myFileSystem = FileSystems.getDefault();
        WatchService FileSystemWatch = myFileSystem.newWatchService();

        return FileSystemWatch;
    }

    //create a file watcher to monitor event:
    protected WatchKey FileEvent(Path dirPath) throws IOException, InterruptedException {

        //watch any file that ends with .emma in the current dir:..
        WatchService FileSystemWatch = realWatchService();
        WatchKey eventRegister =
        dirPath.register(
                FileSystemWatch, StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE
                //StandardWatchEventKinds.OVERFLOW
        );
        return eventRegister;
    }

    protected Object EventKind(Path dirPath) throws IOException, InterruptedException{

        WatchService FileSystemWatch = realWatchService();
        WatchKey eventWatch = FileEvent(dirPath);

        while(null != (eventWatch = FileSystemWatch.take())) {
            for(WatchEvent<?> event : eventWatch.pollEvents()){
                return event.kind();
            }
            eventWatch.reset();
        }
        return null;
    }


    protected Object EventContext(Path dirPath) throws IOException, InterruptedException{

        WatchService FileSystemWatch = realWatchService();
        WatchKey eventWatch = FileEvent(dirPath);

        while(null != (eventWatch = FileSystemWatch.take())) {
            for(WatchEvent<?> event : eventWatch.pollEvents()){
                 return event.context();
            }
            eventWatch.reset();
        }
        return null;
    }
}

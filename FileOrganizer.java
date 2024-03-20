import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class FileOrganizer {
    private static final String DOWNLOADS_PATH = "";  // put the absolute path of the downloads folder here
    private static final Map<String, String> extensionToFolderMap = new HashMap<>();
    

    static {
        
        extensionToFolderMap.put("pdf", DOWNLOADS_PATH + "/PDFs");
        extensionToFolderMap.put("jpg", DOWNLOADS_PATH + "/Images");
        extensionToFolderMap.put("png", DOWNLOADS_PATH + "/Images");
        
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Path downloadsPath = Paths.get(DOWNLOADS_PATH);
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            downloadsPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

            while (true) {
                WatchKey key = watchService.take(); 
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                
                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        continue; 
                    }
                
                    
                    @SuppressWarnings("unchecked") 
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path filename = ev.context();
                    Path child = downloadsPath.resolve(filename);
                
                    if (!Files.isDirectory(child)) {
                        moveFileToFolder(child);
                    }
                }
                

                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }
        }
    }

    private static void moveFileToFolder(Path file) throws IOException {
        String extension = getFileExtension(file);

        if (extensionToFolderMap.containsKey(extension)) {
            Path destinationFolder = Paths.get(extensionToFolderMap.get(extension));
            if (!Files.exists(destinationFolder)) {
                Files.createDirectories(destinationFolder);
            }
            Files.move(file, destinationFolder.resolve(file.getFileName()), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Moved file: " + file + " to " + destinationFolder);
        }
    }

    private static String getFileExtension(Path file) {
        String fileName = file.toString();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        } else {
            return "";
        }
    }
}

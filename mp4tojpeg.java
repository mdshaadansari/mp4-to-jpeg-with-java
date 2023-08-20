import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class MP4ToJPEGConverter {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the folder path containing the MP4 file: ");
        String folderPath = scanner.nextLine();
        
        File folder = new File(folderPath);
        if (!folder.isDirectory()) {
            System.out.println("Invalid folder path.");
            return;
        }

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".mp4"));
        if (files == null || files.length == 0) {
            System.out.println("No MP4 files found in the specified folder.");
            return;
        }

        File mp4File = files[0];
        String mp4FileName = mp4File.getName();
        String outputFolderPath = folderPath + File.separator + mp4FileName.substring(0, mp4FileName.lastIndexOf('.'));
        
        File outputFolder = new File(outputFolderPath);
        if (!outputFolder.exists()) {
            outputFolder.mkdir();
        }

        try {
            VideoFrameExtractor.extractFrames(mp4File.getAbsolutePath(), outputFolderPath);
            System.out.println("JPEG frames extracted and saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class VideoFrameExtractor {

    public static void extractFrames(String videoPath, String outputFolderPath) throws IOException {
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoPath);
        grabber.start();

        int frameNumber = 0;
        while (true) {
            Frame frame = grabber.grabFrame();
            if (frame == null) {
                break;
            }

            BufferedImage image = Java2DFrameUtils.toBufferedImage(frame);
            File outputFile = new File(outputFolderPath + File.separator + "frame" + frameNumber + ".jpeg");
            ImageIO.write(image, "jpeg", outputFile);

            frameNumber++;
        }

        grabber.stop();
    }
}

package com.slipgaji.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class PhotoUtil {

    public static String savePhoto(File sourceFile, int employeeId) {
        try {
            Constants.ensureDirectories();
            String ext = getExtension(sourceFile.getName());
            String fileName = "karyawan_" + employeeId + "." + ext;
            File dest = new File(Constants.PHOTO_DIR, fileName);
            Files.copy(sourceFile.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return dest.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String savePhoto(BufferedImage image, int employeeId, String format) {
        try {
            Constants.ensureDirectories();
            String fileName = "karyawan_" + employeeId + "." + format;
            File dest = new File(Constants.PHOTO_DIR, fileName);
            ImageIO.write(image, format, dest);
            return dest.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean deletePhoto(String photoPath) {
        if (photoPath == null || photoPath.isEmpty()) return false;
        File file = new File(photoPath);
        return file.exists() && file.delete();
    }

    private static String getExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot > 0 ? fileName.substring(dot + 1) : "jpg";
    }
}

package utils;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

public class ToolUtilities {
    public static void copyFile(String srcPath, String desPath) {
        File src = new File(srcPath);
        File des = new File(desPath);

        try {
            FileChannel srcChannel = new FileInputStream(src).getChannel();
            FileChannel desChannel = new FileInputStream(des).getChannel();
            try {
                desChannel.transferFrom(srcChannel, 0, srcChannel.size());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (srcChannel != null)
                        srcChannel.close();
                    if (desChannel != null)
                        desChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String copyFileFromDataDirToSDCard(String desDirectory, String srcDirectory) {
        File sdCard = Environment.getExternalStorageDirectory();
        String realPath = sdCard.getAbsolutePath();

        File data = Environment.getDataDirectory();
        String desDir = realPath + "/" + desDirectory;

        File directory = new File(desDir);
        if (!directory.exists())
            directory.mkdir();

        String dataPath = srcDirectory;
        File dataDir = new File(data, dataPath);
        File[] fileList = dataDir.listFiles();

        String result = "Copied Files: \n";

        if (fileList != null) {
            result += "- Files located at directory: \n";
            for (int i = 0; i < fileList.length; i++) {
                File f = fileList[i];
                result += f.getName() + "\n";
                copyFile(f.getAbsolutePath(), desDir + "/" + f.getName());
            }
        } else {
            result += "Files located at directory: No File\n";
        }
        return result;
    }

    public static boolean checkDB(Context context, String dbName) {
        boolean result = false;
        File dbFile = null;
        try {
            String dbPath = "/data/data/" + context.getPackageName() + "/databases/" + dbName;
            Log.d("DBHelper", "dbPath " + dbPath);
            dbFile = new File(dbPath);
            result = dbFile.exists();
        } catch (SQLiteException e) {
            Log.d("DB errors: ", "DB doesn't exist!!!");
        } finally {
            if (dbFile != null) {
                dbFile = null;
            }
        }
        return result;
    }

    public static void copyDB(InputStream is, OutputStream os) throws IOException {
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) > 0) {
            os.write(buffer, 0, length);
        }
        os.flush();
        is.close();
        os.close();
    }
}


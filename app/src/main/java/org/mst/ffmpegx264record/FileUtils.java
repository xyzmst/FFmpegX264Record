//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.mst.ffmpegx264record;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileUtils {
    public FileUtils() {
    }

    public static String concatPath(String... paths) {
        StringBuilder result = new StringBuilder();
        if(paths != null) {
            String[] var5 = paths;
            int var4 = paths.length;

            for(int var3 = 0; var3 < var4; ++var3) {
                String path = var5[var3];
                if(path != null && path.length() > 0) {
                    int len = result.length();
                    boolean suffixSeparator = len > 0 && result.charAt(len - 1) == File.separatorChar;
                    boolean prefixSeparator = path.charAt(0) == File.separatorChar;
                    if(suffixSeparator && prefixSeparator) {
                        result.append(path.substring(1));
                    } else if(!suffixSeparator && !prefixSeparator) {
                        result.append(File.separatorChar);
                        result.append(path);
                    } else {
                        result.append(path);
                    }
                }
            }
        }

        return result.toString();
    }

    public static String calculateMD5(File updateFile) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException var19) {
            Log.e("FileUtils", "Exception while getting digest", var19);
            return null;
        }

        FileInputStream is;
        try {
            is = new FileInputStream(updateFile);
        } catch (FileNotFoundException var18) {
            Log.e("FileUtils", "Exception while getting FileInputStream", var18);
            return null;
        }

        byte[] buffer = new byte[8192];

        String var9;
        try {
            int read;
            while((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }

            byte[] e = digest.digest();
            BigInteger bigInt = new BigInteger(1, e);
            String output = bigInt.toString(16);
            output = String.format("%32s", new Object[]{output}).replace(' ', '0');
            var9 = output;
        } catch (IOException var20) {
            throw new RuntimeException("Unable to process file for MD5", var20);
        } finally {
            try {
                is.close();
            } catch (IOException var17) {
                Log.e("FileUtils", "Exception on closing MD5 input stream", var17);
            }

        }

        return var9;
    }

    public static String calculateMD5(File updateFile, int offset, int partSize) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException var24) {
            Log.e("FileUtils", "Exception while getting digest", var24);
            return null;
        }

        FileInputStream is;
        try {
            is = new FileInputStream(updateFile);
        } catch (FileNotFoundException var23) {
            Log.e("FileUtils", "Exception while getting FileInputStream", var23);
            return null;
        }

        boolean buffSize = true;
        byte[] buffer = new byte[8192];

        try {
            if(offset > 0) {
                is.skip((long)offset);
            }

            int e = Math.min(8192, partSize);
            int byteLen = 0;

            int read;
            while((read = is.read(buffer, 0, e)) > 0 && byteLen < partSize) {
                digest.update(buffer, 0, read);
                byteLen += read;
                if(byteLen + 8192 > partSize) {
                    e = partSize - byteLen;
                }
            }

            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            String output = bigInt.toString(16);
            output = String.format("%32s", new Object[]{output}).replace(' ', '0');
            String var14 = output;
            return var14;
        } catch (IOException var25) {
            throw new RuntimeException("Unable to process file for MD5", var25);
        } finally {
            try {
                is.close();
            } catch (IOException var22) {
                Log.e("FileUtils", "Exception on closing MD5 input stream", var22);
            }

        }
    }

    public static boolean checkFile(File f) {
        return f != null && f.exists() && f.canRead() && (f.isDirectory() || f.isFile() && f.length() > 0L);
    }

    public static boolean checkFile(String path) {
        if(StringUtils.isNotEmpty(path)) {
            File f = new File(path);
            if(f != null && f.exists() && f.canRead() && (f.isDirectory() || f.isFile() && f.length() > 0L)) {
                return true;
            }
        }

        return false;
    }

    public static String getExternalStorageDirectory() {
        String path = Environment.getExternalStorageDirectory().getPath();
        if(DeviceUtils.isZte()) {
            path = path.replace("/sdcard", "/sdcard-ext");
        }

        return path;
    }

    public static long getFileSize(String fn) {
        File f = null;
        long size = 0L;

        try {
            f = new File(fn);
            size = f.length();
        } catch (Exception var8) {
            var8.printStackTrace();
        } finally {
            f = null;
        }

        return (size < 0L?null:Long.valueOf(size)).longValue();
    }

    public static long getFileSize(File fn) {
        return fn == null?0L:fn.length();
    }

    public static String getFileType(String fn, String defaultType) {
        FileNameMap fNameMap = URLConnection.getFileNameMap();
        String type = fNameMap.getContentTypeFor(fn);
        return type == null?defaultType:type;
    }

    public static String getFileType(String fn) {
        return getFileType(fn, "application/octet-stream");
    }

    public static String getFileExtension(String filename) {
        String extension = "";
        if(filename != null) {
            int dotPos = filename.lastIndexOf(".");
            if(dotPos >= 0 && dotPos < filename.length() - 1) {
                extension = filename.substring(dotPos + 1);
            }
        }

        return extension.toLowerCase();
    }

    public static boolean deleteFile(File f) {
        return f != null && f.exists() && !f.isDirectory()?f.delete():false;
    }

    public static void deleteDir(File f) {
        if(f != null && f.exists() && f.isDirectory()) {
            File[] var4;
            int var3 = (var4 = f.listFiles()).length;

            for(int var2 = 0; var2 < var3; ++var2) {
                File file = var4[var2];
                if(file.isDirectory()) {
                    deleteDir(file);
                }

                file.delete();
            }

            f.delete();
        }

    }

    public static void deleteDir(String f) {
        if(f != null && f.length() > 0) {
            deleteDir(new File(f));
        }

    }

    public static boolean deleteFile(String f) {
        return f != null && f.length() > 0?deleteFile(new File(f)):false;
    }

    public static String readFile(File file, String charsetName) {
        StringBuilder fileContent = new StringBuilder("");
        if(file != null && file.isFile()) {
            BufferedReader reader = null;

            try {
                InputStreamReader e = new InputStreamReader(new FileInputStream(file), charsetName);
                reader = new BufferedReader(e);

                for(String line = null; (line = reader.readLine()) != null; fileContent.append(line)) {
                    if(!fileContent.toString().equals("")) {
                        fileContent.append("\r\n");
                    }
                }

                reader.close();
                return fileContent.toString();
            } catch (IOException var13) {
                throw new RuntimeException("IOException occurred. ", var13);
            } finally {
                if(reader != null) {
                    try {
                        reader.close();
                    } catch (IOException var12) {
                        throw new RuntimeException("IOException occurred. ", var12);
                    }
                }

            }
        } else {
            return fileContent.toString();
        }
    }

    public static String readFile(String filePath, String charsetName) {
        return readFile(new File(filePath), charsetName);
    }

    public static String readFile(File file) {
        return readFile(file, "utf-8");
    }

    public static boolean fileCopy(String from, String to) {
        boolean result = false;
        short size = 1024;
        FileInputStream in = null;
        FileOutputStream out = null;

        try {
            in = new FileInputStream(from);
            out = new FileOutputStream(to);
            byte[] e = new byte[size];
            boolean bytesRead = true;

            int bytesRead1;
            while((bytesRead1 = in.read(e)) != -1) {
                out.write(e, 0, bytesRead1);
            }

            out.flush();
            result = true;
        } catch (FileNotFoundException var26) {
            var26.printStackTrace();
        } catch (IOException var27) {
            var27.printStackTrace();
        } catch (Exception var28) {
            var28.printStackTrace();
        } finally {
            try {
                if(in != null) {
                    in.close();
                }
            } catch (IOException var25) {
                ;
            }

            try {
                if(out != null) {
                    out.close();
                }
            } catch (IOException var24) {
                ;
            }

        }

        return result;
    }
}

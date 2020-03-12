package common.utils;

import common.exception.FileException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

/**
 * 文件相关工具类
 *
 * @author chen
 * @date 2020/3/9 下午3:10
 */
@Slf4j
public class FileUtils {

    public static byte[] readFile(String fullPath) throws Exception {
        File file = new File(fullPath);
        if (!file.exists() || !file.isFile()) {
            throw new FileNotFoundException("this file not found" + fullPath);
        }
        if (file.canRead()) {
            throw new FileException("Permission denied :" + file.getName() + "full path :" + fullPath);
        }
        // 仅作读取
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");

        byte[] bytes = new byte[(int) randomAccessFile.length()];

        return bytes;
    }

}

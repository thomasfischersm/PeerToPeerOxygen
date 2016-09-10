package com.playposse.peertopeeroxygen.android.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * A utility for dealing with streams.
 */
public class StreamUtil {

    public static String readTextStream(File file) throws IOException {
        return readTextStream(new FileInputStream(file));
    }

    public static String readTextStream(InputStream input) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input, "utf-8"));

        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }
        reader.close();
        return sb.toString();
    }

    public static void writeTextStream(File file, String content) throws  IOException {
        writeTextStream(new FileOutputStream(file), content);
    }

    public static void writeTextStream(OutputStream output, String content) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));

        try {
            writer.write(content);
        } finally {
            writer.close();
        }
    }

    public static byte[] readByteStream(String filePath) throws IOException {
        FileInputStream inputStream = new FileInputStream(filePath);
        return readByteStream(inputStream);
    }

    public static byte[] readByteStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        try {
            int readSize;
            byte[] data = new byte[1024];
            while ((readSize = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, readSize);
            }
        } finally {
            inputStream.close();
        }

        return buffer.toByteArray();
    }

    public static void writeByteStream(byte[] data, String filePath) throws IOException {
        FileOutputStream outputStream
                = new FileOutputStream(filePath, true);
        try {
            outputStream.write(data);
        } finally {
            outputStream.close();
        }
    }
}

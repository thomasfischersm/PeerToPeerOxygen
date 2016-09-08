package com.playposse.peertopeeroxygen.android.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * A utility for dealing with streams.
 */
public class StreamUtil {

    public static String readStream(InputStream input) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input, "utf-8"));

        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }
        reader.close();
        return sb.toString();
    }

    public static void writeStream(OutputStream output, String content) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));

        try {
            writer.write(content);
        } finally {
            writer.close();
        }
    }
}

package io.corbs.stocks;

import org.apache.commons.codec.Charsets;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public class BlockingIO {

    private static final int MIN_BUFFER_SIZE     = 1;
    private static final int MAX_BUFFER_SIZE     = 1024 * 128000; // 128 MB
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 64;     //  64 MB

    private static final int EOF = -1;

    public static String asString(final Path path) throws IOException {
        Reader reader = Files.newBufferedReader(path);
        String value = asString(reader);
        closeQuietly(reader);
        return value;
    }

    public static String asString(final File file) throws IOException {
        Reader reader = new FileReader(file);
        String value = asString(reader);
        closeQuietly(reader);
        return value;
    }

    public static StringBuffer asStringBuffer(final File file) throws IOException {
        Reader reader = new FileReader(file);
        StringBuffer value = asStringBuffer(reader);
        closeQuietly(reader);
        return value;
    }

    public static StringBuilder asStringBuilder(final File file) throws IOException {
        Reader reader = new FileReader(file);
        StringBuilder value = asStringBuilder(reader);
        closeQuietly(reader);
        return value;
    }

    public static String asString(final Reader reader) throws IOException {
        final StringWriter writer = new StringWriter();
        cp(reader, writer);
        closeQuietly(reader);
        return writer.toString();
    }

    public static StringBuffer asStringBuffer(final Reader reader) throws IOException {
        final StringWriter writer = new StringWriter();
        cp(reader, writer);
        closeQuietly(reader);
        return writer.getBuffer();
    }

    public static StringBuilder asStringBuilder(final Reader reader) throws IOException {
        final StringWriter writer = new StringWriter();
        cp(reader, writer);
        closeQuietly(reader);
        return new StringBuilder(writer.toString());
    }

    public static String asString(final Reader reader, int bufferSize) throws IOException {
        final StringWriter writer = new StringWriter();
        if(!checkInclusive(bufferSize, MIN_BUFFER_SIZE, MAX_BUFFER_SIZE)) {
            bufferSize = DEFAULT_BUFFER_SIZE;
        }
        char[] buffer = new char[bufferSize];
        cp(reader, writer, buffer);
        closeQuietly(reader);
        return writer.toString();
    }

    public static String asString(final InputStream input) throws IOException {
        String value = asString(input, Charsets.UTF_8);
        closeQuietly(input);
        return value;
    }

    public static String asString(final InputStream input, String encoding) throws IOException {
        String value = asString(input, Charsets.toCharset(encoding));
        closeQuietly(input);
        return value;
    }

    public static String asString(final InputStream input, final Charset encoding) throws IOException {
        final StringWriter writer = new StringWriter();
        InputStreamReader reader = new InputStreamReader(input, Charsets.toCharset(encoding));
        cp(reader, writer);
        closeQuietly(reader);
        return writer.toString();
    }

    public static long cp(final Reader reader, final Writer writer) throws IOException {
        checkArg(reader);
        checkArg(reader);

        char[] buffer = new char[DEFAULT_BUFFER_SIZE];
        long value = cp(reader, writer, buffer);
        closeQuietly(reader);
        return value;
    }

    public static long cp(final Reader reader, final Writer writer, int bufferSize) throws IOException {
        checkArg(reader);
        checkArg(reader);
        if(!checkInclusive(bufferSize, MIN_BUFFER_SIZE, MAX_BUFFER_SIZE)) {
            bufferSize = DEFAULT_BUFFER_SIZE;
        }
        char[] buffer = new char[bufferSize];
        long value = cp(reader, writer, buffer);
        closeQuietly(reader);
        return value;
    }

    public static long cp(final Reader reader, final Writer writer, final char[] buffer) throws IOException {
        checkArg(reader);
        checkArg(reader);

        long checkSum = 0;
        int n = 0;
        while(EOF != (n = reader.read(buffer))) {
            writer.write(buffer, 0, n);
            checkSum += n;
        }
        closeQuietly(reader);
        closeQuietly(writer);
        return checkSum;
    }

    /**
     * This class method will buffer the whole file
     *
     * TODO consider placing an upper limit on the buffer to protect against OoM
     * @param file
     */
    public static StringBuffer bufferFile(File file) throws IOException {

        InputStream in = null;
        StringBuffer buffer = new StringBuffer();
        try {
            in = new BufferedInputStream(new FileInputStream(file));
            byte[] b = new byte[DEFAULT_BUFFER_SIZE];
            for (int n; (n = in.read(b)) != -1; ) {
                buffer.append(new String(b, 0, n));
            }
        } finally {
            closeQuietly(in);
        }

        return buffer;
    }

    public static void checkArg(Object object) {
        if(object == null) {
            throw new IllegalArgumentException();
        }
    }

    public static boolean checkInclusive(int actual, int min, int max) {
        return actual >= min && actual <= max;
    }

    public static void closeQuietly(Closeable closeable) {
        try {
            if(closeable != null) { closeable.close(); }
        } catch (final IOException ignore) { }
    }

    public static Writer asWriter(final File file) throws IOException {
        Writer writer = new BufferedWriter(new FileWriter(file));
        return writer;
    }

    public static Writer asWriter(String path) throws IOException {
        return asWriter(new File(path));
    }

    public static Reader asReader(final File file) throws IOException {
        Reader reader = new BufferedReader(new FileReader(file));
        return reader;
    }

    public static BufferedReader asBufferedReader(final File file) throws IOException {
        return new BufferedReader(new FileReader(file));
    }

    public static long countLines(final File file) throws IOException {
        BufferedReader reader = asBufferedReader(file);
        long count = reader.lines().count();
        closeQuietly(reader);
        return count;
    }

    public static OutputStream asOutputStream(final File file) throws IOException {
        OutputStream stream = new FileOutputStream(file);
        return stream;
    }

    public static OutputStream asOutputStream(String path) throws IOException {
        return asOutputStream(new File(path));
    }
}

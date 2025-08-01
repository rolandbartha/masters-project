package net.codemarked.masters.util;

import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Paths;

public class IOUtil {
    public static String getAbsolutePath(String resourcePath) {
        URL resource = IOUtil.class.getClassLoader().getResource(resourcePath);
        if (resource == null)
            throw new IllegalArgumentException("Couldn't find resource at: " + resourcePath);
        return Paths.get(resource.getPath().substring(1)).toAbsolutePath().toString();
    }

    public static ByteBuffer bufferedResource(String resource, int bufferSize) throws IOException {
        try (InputStream source = IOUtil.class.getClassLoader().getResourceAsStream(resource);
             ReadableByteChannel channel = Channels.newChannel(source)) {
            ByteBuffer buffer = BufferUtils.createByteBuffer(bufferSize);
            while (channel.read(buffer) != -1) {
                if (buffer.remaining() == 0) {
                    ByteBuffer newBuffer = BufferUtils.createByteBuffer(buffer.capacity() * 2);
                    buffer.flip();
                    newBuffer.put(buffer);
                    buffer = newBuffer;
                }
            }
            buffer.flip();
            return buffer;
        }
    }
}

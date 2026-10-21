package jadx.zip.parser;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * Android 兼容版 ZipDeflate。
 *
 * 上游 jadx 1.5.6 使用了 Java 11 才有的
 *   Inflater.setInput(ByteBuffer) / Inflater.inflate(ByteBuffer)
 * Android 的 java.util.zip.Inflater 没有这两个方法，运行时会抛
 *   NoSuchMethodError: No virtual method setInput(Ljava/nio/ByteBuffer;)V
 *
 * 这里改用 byte[] 版本实现，行为与上游保持一致。
 * 编译时会把 jar 中的原始 ZipDeflate.class 剔除，避免重复类。
 */
final class ZipDeflate {

    private static final int BUFFER_SIZE = 4096;

    private ZipDeflate() {
    }

    static byte[] decompressEntryToBytes(ByteBuffer buf, JadxZipEntry entry) throws DataFormatException {
        long uncompressedSize = entry.getUncompressedSize();
        if (uncompressedSize > Integer.MAX_VALUE) {
            throw new DataFormatException("Uncompressed size is too big: " + entry);
        }
        int dataStart = entry.getDataStart();
        int compressedSize = (int) entry.getCompressedSize();

        ByteBuffer slice = buf.duplicate();
        slice.position(dataStart);
        slice.limit(dataStart + compressedSize);

        byte[] input = new byte[slice.remaining()];
        slice.get(input);

        byte[] out = new byte[(int) uncompressedSize];
        Inflater inflater = new Inflater(true);
        try {
            inflater.setInput(input);
            int size = inflater.inflate(out);
            if (size != out.length) {
                throw new DataFormatException("Inflate size mismatch: " + entry
                        + ", inflated: " + size + ", expected: " + out.length);
            }
            return out;
        } finally {
            inflater.end();
        }
    }

    static InputStream decompressEntryToStream(ByteBuffer buf, JadxZipEntry entry) {
        InputStream in = JadxZipParser.bufferToStream(buf, entry.getDataStart(), (int) entry.getCompressedSize());
        Inflater inflater = new Inflater(true);
        return new InflaterInputStream(in, inflater, BUFFER_SIZE);
    }
}
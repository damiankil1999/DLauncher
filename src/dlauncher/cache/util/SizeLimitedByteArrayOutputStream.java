/*
 * This work is licensed under a Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * http://creativecommons.org/licenses/by-sa/3.0/
 * Authors: Damian Lamers, Fernando van Loenhout
 */
package dlauncher.cache.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

public class SizeLimitedByteArrayOutputStream extends OutputStream {

    protected final byte buf[];

    protected int count;

    public SizeLimitedByteArrayOutputStream(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Negative initial size: " + size);
        }
        buf = new byte[size];
    }

    private void ensureCapacity(int minCapacity) throws IOException {
        if (minCapacity - buf.length > 0) {
            throw new IOException("Size limit reached, size of " + buf.length
                    + " reached");
        }
    }

    @Override
    public void write(int b) throws IOException {
        ensureCapacity(count + 1);
        buf[count] = (byte) b;
        count += 1;
    }

    @Override
    public void write(byte b[], int off, int len)
            throws IOException {
        if ((off < 0) || (off > b.length) || (len < 0)
                || ((off + len) - b.length > 0)) {
            throw new IndexOutOfBoundsException();
        }
        ensureCapacity(count + len);
        System.arraycopy(b, off, buf, count, len);
        count += len;
    }

    public void reset() {
        count = 0;
    }

    public byte[] toByteArray() {
        return Arrays.copyOf(buf, count);
    }

    public int size() {
        return count;
    }

    @Override
    public void close() {
    }

}

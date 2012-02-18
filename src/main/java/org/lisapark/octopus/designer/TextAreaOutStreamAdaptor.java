package org.lisapark.octopus.designer;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;

import static com.jgoodies.common.base.Preconditions.checkArgument;

/**
 * @author dave sinclair(dsinclair@chariotsolutions.com)
 */
class TextAreaOutStreamAdaptor extends OutputStream {
    private JTextArea textArea;

    public TextAreaOutStreamAdaptor(JTextArea textArea) {
        checkArgument(textArea != null, "textArea cannot be null");
        this.textArea = textArea;
    }

    @Override
    public void write(int b) throws IOException {
        String str = new String(new byte[b], 0, 1);
        textArea.append(str);
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        this.write(bytes, 0, bytes.length);
    }

    @Override
    public void write(byte[] bytes, int off, int len) throws IOException {
        String str = new String(bytes, off, len);
        textArea.append(str);
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

    @Override
    public void flush() throws IOException {
        textArea.append("\n");
    }

    @Override
    public void close() throws IOException {

    }
}

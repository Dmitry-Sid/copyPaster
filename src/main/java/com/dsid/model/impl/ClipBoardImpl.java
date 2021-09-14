package com.dsid.model.impl;


import com.dsid.model.ClipBoard;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClipBoardImpl implements ClipBoard, ClipboardOwner {
    private final Map<Integer, String> map = new ConcurrentHashMap<>();

    @Override
    public void copy(int key) {
        new Thread(() -> {
            try {
                final String valueFromMap = map.get(key);
                for (int i = 0; i < 10; i++) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    final String value = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
                    if ((valueFromMap != null && !value.equals(valueFromMap)) || i > 8) {
                        map.put(key, value);
                        break;
                    }
                }
            } catch (UnsupportedFlavorException | IOException e) {
                throw new RuntimeException("error during clipboard handling (copy)", e);
            }
        }).start();
    }

    @Override
    public void paste(int key) {
        try {
            final String valueFromMap = map.get(key);
            if (valueFromMap == null) {
                return;
            }
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(valueFromMap), this);
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor).equals(valueFromMap)) {
                    break;
                }
            }
        } catch (UnsupportedFlavorException | IOException e) {
            throw new RuntimeException("error during clipboard handling (paste)", e);
        }
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {

    }
}

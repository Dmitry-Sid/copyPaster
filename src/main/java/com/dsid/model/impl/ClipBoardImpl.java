package com.dsid.model.impl;


import com.dsid.model.ClipBoard;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ClipBoardImpl implements ClipBoard, ClipboardOwner {
    private final Map<Integer, String> map = new HashMap<>();

    @Override
    public void copy(int key) {
        try {
            final String value = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
            if (value != null) {
                map.put(key, value);
                System.out.println("copy " + value);
            }
        } catch (UnsupportedFlavorException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void paste(int key) {
        final String data = map.get(key);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(data), this);
        System.out.println("pasted " + data);
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {

    }
}

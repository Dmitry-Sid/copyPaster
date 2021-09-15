package com.dsid.model;

import java.util.function.Consumer;

public interface ClipBoard {

    void copy(int key);

    void paste(int key);

    void set(int key, String value);

    void subscribeChanges(Consumer<ClipBoardItem> consumer);

    class ClipBoardItem {
        public final int key;
        public final String value;

        public ClipBoardItem(int key, String value) {
            this.key = key;
            this.value = value;
        }
    }

}

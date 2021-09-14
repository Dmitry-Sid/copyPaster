package com.dsid.model.impl;

import com.dsid.model.ClipBoard;
import com.dsid.model.KeyBoardService;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.NativeInputEvent;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KeyBoardServiceImpl implements KeyBoardService {
    private static final Set<Integer> keySet = new HashSet<>(Arrays.asList(NativeKeyEvent.VC_1, NativeKeyEvent.VC_2,
            NativeKeyEvent.VC_3, NativeKeyEvent.VC_4, NativeKeyEvent.VC_5, NativeKeyEvent.VC_6, NativeKeyEvent.VC_7,
            NativeKeyEvent.VC_8, NativeKeyEvent.VC_9, NativeKeyEvent.VC_0));

    private final ClipBoard clipBoard;
    private final Map<Integer, Boolean> map;
    private Integer key;

    public KeyBoardServiceImpl(ClipBoard clipBoard) {
        this.clipBoard = clipBoard;
        this.map = new HashMap<Integer, Boolean>() {{
            put(NativeKeyEvent.VC_CONTROL, false);
            put(NativeKeyEvent.VC_C, false);
            put(NativeKeyEvent.VC_V, false);
        }};
    }

    @Override
    public void run() {
        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.setEventDispatcher(new CustomExecutorService());
            GlobalScreen.addNativeKeyListener(new NativeKeyListener() {

                @Override
                public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {

                }

                @Override
                public void nativeKeyPressed(NativeKeyEvent e) {
                    System.out.println(e.getKeyCode() + " pressed");
                    map.computeIfPresent(e.getKeyCode(), (key, value) -> true);
                    if (keySet.contains(e.getKeyCode())) {
                        key = e.getKeyCode();
                        System.out.println("Attempting to consume key event..." + key);
                        if (map.get(NativeKeyEvent.VC_CONTROL)) {
                            disableNativeKeyEvent(e);
                        }
                    }
                    if (key == null || !map.get(NativeKeyEvent.VC_CONTROL)) {
                        return;
                    }
                    if (map.get(NativeKeyEvent.VC_C)) {
                        System.out.println("ctrl + shift + c + " + key);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        clipBoard.copy(key);
                    } else if (map.get(NativeKeyEvent.VC_V)) {
                        System.out.println("ctrl + shift + v + " + key);
                        clipBoard.paste(key);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                }

                @Override
                public void nativeKeyReleased(NativeKeyEvent e) {
                    System.out.println(e.getKeyCode() + " released");
                    map.computeIfPresent(e.getKeyCode(), (key, value) -> false);
                    if (keySet.contains(e.getKeyCode())) {
                        key = null;
                    }
                }
            });
        } catch (NativeHookException e) {
            e.printStackTrace();
        }
        final Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);
    }

    private static class CustomExecutorService extends AbstractExecutorService {
        private boolean running;

        private CustomExecutorService() {
            running = true;
        }

        public void shutdown() {
            running = false;
        }

        public List<Runnable> shutdownNow() {
            running = false;
            return new ArrayList<>(0);
        }

        public boolean isShutdown() {
            return !running;
        }

        public boolean isTerminated() {
            return !running;
        }

        public boolean awaitTermination(long timeout, TimeUnit unit) {
            return true;
        }

        public void execute(Runnable r) {
            r.run();
        }
    }

    private void disableNativeKeyEvent(NativeKeyEvent event) {
        try {
            final Field field = NativeInputEvent.class.getDeclaredField("reserved");
            field.setAccessible(true);
            field.setShort(event, (short) 0x01);
            System.out.print("disabled\n");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

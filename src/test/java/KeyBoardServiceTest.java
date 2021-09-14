import com.dsid.model.ClipBoard;
import com.dsid.model.impl.KeyBoardServiceImpl;
import org.junit.Test;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public class KeyBoardServiceTest {

    @Test
    public void fullTest() throws AWTException {
        final AtomicReference<String> text = new AtomicReference<>();
        final Map<Integer, String> map = new HashMap<>();
        final ClipBoard clipBoard = mock(ClipBoard.class);
        doAnswer(invocation -> {
            map.put((int) invocation.getArguments()[0], text.get());
            return null;
        }).when(clipBoard).copy(anyInt());
        doAnswer(invocation -> {
            text.set(map.get(invocation.getArguments()[0]));
            return null;
        }).when(clipBoard).paste(anyInt());
        new KeyBoardServiceImpl(clipBoard);
        final Robot robot = new Robot(); // creating keyboard
        text.set("test1");
        try {
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyRelease(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_1);
            robot.keyPress(KeyEvent.VK_C);
        } finally {
            robot.keyRelease(KeyEvent.VK_1);
            robot.keyRelease(KeyEvent.VK_C);
        }
        assertNull(map.get(2));
        try {
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_1);
            robot.keyRelease(KeyEvent.VK_1);
            robot.keyPress(KeyEvent.VK_C);
        } finally {
            robot.keyRelease(KeyEvent.VK_CONTROL);
            robot.keyRelease(KeyEvent.VK_C);
        }
        assertNull(map.get(2));
        try {
            robot.keyPress(KeyEvent.VK_SHIFT);
            robot.keyPress(KeyEvent.VK_1);
            robot.keyPress(KeyEvent.VK_C);
        } finally {
            robot.keyRelease(KeyEvent.VK_SHIFT);
            robot.keyRelease(KeyEvent.VK_1);
            robot.keyRelease(KeyEvent.VK_C);
        }
        assertNull(map.get(2));
        callCtrlC(robot, 1);
        assertEquals("test1", map.get(2));
        text.set("test0");
        callCtrlV(robot, 1);
        assertEquals("test1", text.get());
        text.set("test2");
        callCtrlC(robot, 2);
        assertEquals("test2", map.get(3));
        text.set("test0");
        callCtrlV(robot, 2);
        assertEquals("test2", text.get());

        assertEquals("test1", map.get(2));
        assertEquals("test2", map.get(3));
        text.set("test0");
        callCtrlV(robot, 1);
        assertEquals("test1", text.get());
        callCtrlV(robot, 2);
        assertEquals("test2", text.get());
    }

    private void callCtrlC(Robot robot, int number) {
        try {
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(48 + number);
            robot.keyPress(KeyEvent.VK_C);
        } finally {
            robot.keyRelease(KeyEvent.VK_CONTROL);
            robot.keyRelease(48 + number);
            robot.keyRelease(KeyEvent.VK_C);
        }
    }

    private void callCtrlV(Robot robot, int number) {
        try {
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(48 + number);
            robot.keyPress(KeyEvent.VK_V);
        } finally {
            robot.keyRelease(KeyEvent.VK_CONTROL);
            robot.keyRelease(48 + number);
            robot.keyRelease(KeyEvent.VK_V);
        }
    }
}
import com.dsid.model.ClipBoard;
import com.dsid.model.impl.ClipBoardImpl;
import org.junit.Test;

import java.awt.*;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ClipBoardTest {

    @Test
    public void fullTest() throws IOException, UnsupportedFlavorException, InterruptedException {
        final ClipBoard clipBoard = new ClipBoardImpl();
        final Map<Integer, String> map = new HashMap<>();
        clipBoard.subscribeChanges(item -> map.put(item.key, item.value));
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection("test1"), (ClipboardOwner) clipBoard);
        clipBoard.copy(1);
        Thread.sleep(1000);

        clipBoard.paste(1);
        assertEquals("test1", Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor));
        Thread.sleep(200);

        clipBoard.paste(2);
        assertEquals("test1", Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor));
        assertEquals("test1", map.get(1));
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection("test2"), (ClipboardOwner) clipBoard);
        clipBoard.copy(2);
        Thread.sleep(1000);

        clipBoard.paste(2);
        Thread.sleep(200);
        assertEquals("test2", Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor));
        assertEquals("test2", map.get(2));

        clipBoard.paste(1);
        Thread.sleep(200);
        assertEquals("test1", Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor));
        assertEquals("test1", map.get(1));

        clipBoard.paste(2);
        Thread.sleep(200);
        assertEquals("test2", Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor));
        assertEquals("test2", map.get(2));
    }
}
package com.dsid;

import com.dsid.model.SpringFxmlLoader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Objects;

public class Main extends Application {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static final int PORT = 9999;
    private static final int TRAY_ICON_WIDTH = 16;
    private static final int TRAY_ICON_HEIGHT = 16;

    public static void main(String[] args) {
        launch(args);
    }

    private static synchronized void checkIfRunning() {
        try {
            //Bind to localhost adapter with a zero connection queue
            new ServerSocket(PORT, 0, InetAddress.getByAddress(new byte[]{127, 0, 0, 1}));
        } catch (BindException e) {
            log.error("Application already running");
            System.exit(1);
        } catch (IOException e) {
            log.error("Unexpected error when checking if application is already running");
            System.exit(2);
        }
    }

    private static void addToTray(Stage primaryStage) throws IOException {
        if (!SystemTray.isSupported()) {
            log.error("SystemTray is not supported");
            return;
        }
        final Image image;
        try {
            image = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResource("img/icon.png")))
                    .getScaledInstance(TRAY_ICON_WIDTH, TRAY_ICON_HEIGHT, Image.SCALE_DEFAULT);
        } catch (IOException e) {
            throw e;
        }
        final PopupMenu popup = new PopupMenu();
        final TrayIcon trayIcon = new TrayIcon(image, "CopyPaster", popup);
        final SystemTray tray = SystemTray.getSystemTray();

        final MenuItem openItem = new MenuItem("Show");
        openItem.addActionListener(e -> Platform.runLater(primaryStage::show));
        popup.add(openItem);

        final MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(1));
        popup.add(exitItem);

        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Platform.runLater(primaryStage::show);
            }
        });
        trayIcon.setPopupMenu(popup);
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        PropertyConfigurator.configure(getClass().getClassLoader().getResource("log4j.properties"));
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            log.error("Error", e);
        });
        checkIfRunning(); //Check first if the Application is aready running.
        addToTray(primaryStage);
        SpringFxmlLoader.init("Beans.xml");
        final Parent root = (Parent) SpringFxmlLoader.load("fxml/main.fxml");

        primaryStage.setTitle("CopyPaster");
        primaryStage.getIcons().add(new javafx.scene.image.Image(Objects.requireNonNull(Main.class.getClassLoader().getResource("img/icon.png")).openStream()));
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
        Platform.setImplicitExit(false); //Prevent the Application from Terminating when it's close
    }
}
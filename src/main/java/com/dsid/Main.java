package com.dsid;

import com.dsid.model.KeyBoardService;
import com.dsid.model.SpringFxmlLoader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
    private static final int PORT = 9999;

    @Override
    public void start(Stage primaryStage) throws Exception {
        checkIfRunning(); //Check first if the Application is aready running.
        addToTray(primaryStage);
        SpringFxmlLoader.init("Beans.xml");
        final Parent root = (Parent) SpringFxmlLoader.load("fxml/main.fxml");

        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
        Platform.setImplicitExit(false); //Prevent the Application from Terminating when it's close
        ((KeyBoardService) SpringFxmlLoader.getContext().getBean("keyBoardService")).run();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static synchronized void checkIfRunning() {
        try {
            //Bind to localhost adapter with a zero connection queue
            new ServerSocket(PORT, 0, InetAddress.getByAddress(new byte[]{127, 0, 0, 1}));
        } catch (BindException e) {
            System.err.println("Application already running.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Unexpected error when checking if application is already running.");
            e.printStackTrace();
            System.exit(2);
        }
    }

    private static void addToTray(Stage primaryStage) throws IOException {
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }
        final Image image;
        try {
            image = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResource("img/tray_image.png")));
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
            e.printStackTrace();
        }
    }
}
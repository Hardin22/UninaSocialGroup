package it.uninaSocialGroup.Utils;

import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.util.logging.Logger;

public class ScreenPreloader {
    private Parent preloadedScreen;
    private static final Logger LOGGER = Logger.getLogger(ScreenPreloader.class.getName());

    public ScreenPreloader(String screenToPreload) {
        preloadedScreen = preloadScreen(screenToPreload);
    }

    private Parent preloadScreen(String fxmlPath) {
        Task<Parent> loadScreenTask = new Task<Parent>() {
            @Override
            protected Parent call() throws Exception {
                Parent screen = FXMLLoader.load(getClass().getResource(fxmlPath));
                LOGGER.info("Screen " + fxmlPath + " has been preloaded successfully.");
                return screen;
            }
        };

        new Thread(loadScreenTask).start();

        return loadScreenTask.getValue();
    }

    public Parent getPreloadedScreen() {
        return preloadedScreen;
    }
}
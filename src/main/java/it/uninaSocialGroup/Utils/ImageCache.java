package it.uninaSocialGroup.Utils;

import javafx.scene.image.Image;
import java.util.HashMap;
import java.util.Map;

public class ImageCache {
    private Map<String, Image> cache = new HashMap<>();

    public Image getImage(String imageUrl, double width, double height) {
        if (cache.containsKey(imageUrl)) {
            return cache.get(imageUrl);
        } else {
            return downloadImage(imageUrl, width, height);
        }
    }

    private Image downloadImage(String imageUrl, double width, double height) {
        Image image = new Image(imageUrl, width, height, true, true);
        cache.put(imageUrl, image);
        return image;
    }
}
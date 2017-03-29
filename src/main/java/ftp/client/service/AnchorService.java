package ftp.client.service;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

/**
 * Created by a.kalenkevich on 29.03.2017.
 */
public class AnchorService {
    private static AnchorService ourInstance = new AnchorService();

    public static AnchorService getInstance() {
        return ourInstance;
    }

    private AnchorService() {

    }

    public void anchorNode(Node node) {
        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);
    }
}

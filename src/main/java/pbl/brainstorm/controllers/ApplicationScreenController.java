package pbl.brainstorm.controllers;

import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import pbl.brainstorm.IdeaNode;

public class ApplicationScreenController {

    private final List<IdeaNode> list = new ArrayList<>();

    private IdeaNode mainNode = null;
    private double collisionX;
    private double collisionY;

    private MainController mainController;

    public void setMainController(MainController mainController) {

        this.mainController = mainController;

    }

    @FXML
    private Pane applicationScreen;

    final ContextMenu cm = new ContextMenu();

    final MenuItem addNewNode = new MenuItem("Add a new node...");
    final MenuItem addMainNode = new MenuItem("Add the main node...");

    @FXML
    private void handleMousePressed(final MouseEvent event) {

        if (event.isSecondaryButtonDown()) {

            if (!cm.getItems().isEmpty()) {

                cm.getItems().clear();

            }

            addNewNode.setOnAction(new EventHandler<ActionEvent>() {

                double xCentre;
                double yCentre;

                @Override
                public void handle(ActionEvent e) {

                    final ContextMenu subMenu = new ContextMenu();

                    for (final IdeaNode x : list) {

                        String s = new String();

                        if (x.isMain()) {

                            s = "(MAIN NODE)";

                        }

                        final MenuItem item = new MenuItem(x.getContent() + " " + s);

                        subMenu.getItems().add(item);

                        item.setOnAction(new EventHandler<ActionEvent>() {

                            @Override
                            public void handle(ActionEvent t) {

                                xCentre = x.getX();
                                yCentre = x.getY();
                            }

                        });
                    }

                    subMenu.show(applicationScreen, event.getScreenX(), event.getScreenY());

                    final TextField tf = new TextField();

                    tf.setPromptText("Add a new item...");

                    tf.requestFocus();

                    tf.relocate(event.getSceneX(), event.getSceneY());

                    applicationScreen.getChildren().add(tf);

                    tf.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {

                        public void handle(final KeyEvent keyEvent) {

                            if (keyEvent.getCode() == KeyCode.ENTER) {

                                refreshingNode(tf, event, xCentre, yCentre);

                            }
                        }

                    });

                }

            });

            addMainNode.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent e) {

                    final TextField tf = new TextField();

                    tf.setPromptText("Add the main topic...");

                    tf.requestFocus();

                    tf.relocate(event.getSceneX(), event.getSceneY());

                    applicationScreen.getChildren().add(tf);

                    tf.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {

                        public void handle(final KeyEvent keyEvent) {

                            if (keyEvent.getCode() == KeyCode.ENTER) {

                                refreshingMainNode(tf, event);

                            }
                        }

                    });

                }

            });

            if (mainNode != null) {

                addMainNode.setDisable(true);
                addNewNode.setDisable(false);

            }

            if (mainNode == null) {

                addNewNode.setDisable(true);

            }

            cm.getItems().add(addMainNode);
            cm.getItems().add(addNewNode);

            cm.show(applicationScreen, event.getScreenX(), event.getScreenY());

        }

        if (event.isPrimaryButtonDown()) {

            cm.hide();

        }
    }

    private void refreshingNode(final TextField tf, MouseEvent event, double xCentre, double yCentre) {

        Text text = createText(tf.getText(), 15);

        double textWidth = text.getLayoutBounds().getWidth();

        final Rectangle shape = createRectangle(textWidth, event.getScreenX(), event.getScreenY(), xCentre, yCentre);

        list.add(new IdeaNode(text.getText(), event.getScreenX(), event.getScreenY(), false));

        Group group = new Group(shape, text);

        applicationScreen.getChildren().add(group);

        moveText(text, event.getScreenX(), event.getScreenY());

        tf.setVisible(false);

    }

    private void refreshingMainNode(final TextField tf, MouseEvent event) {

        Text text = createText(tf.getText(), 15);

        double textWidth = text.getLayoutBounds().getWidth();

        final Circle circle = createMainNode(textWidth / 2 + 15, event.getScreenX(), event.getScreenY());

        mainNode = new IdeaNode(text.getText(), event.getScreenX(), event.getScreenY(), true);

        list.add(mainNode);

        Group group = new Group(circle, text);

        applicationScreen.getChildren().add(group);

        moveText(text, event.getScreenX(), event.getScreenY());

        tf.setVisible(false);

    }

    private Circle createMainNode(double radius, double x, double y) {

        final Circle circle = new Circle(radius);

        circle.setStroke(Color.BROWN);
        circle.setStrokeWidth(10);
        circle.setStrokeType(StrokeType.INSIDE);
        circle.setFill(Color.ANTIQUEWHITE);
        circle.relocate(x - radius, y - radius);

        return circle;
    }

    private void moveText(Text text, double x, double y) {

        double width = text.getBoundsInLocal().getWidth();
        double height = text.getBoundsInLocal().getHeight();

        text.relocate(x - width / 2, y - height / 2);

    }

    private Rectangle createRectangle(double width, double x, double y, double xCentre, double yCentre) {

        final Rectangle shape = new Rectangle(width + 20, 40);

        shape.relocate(x - (width + 20) / 2, y - 20);
        shape.setStrokeType(StrokeType.OUTSIDE);
        shape.setStrokeLineJoin(StrokeLineJoin.ROUND);
        shape.setStrokeWidth(10);
        shape.setFill(Color.web("#85bade"));
        shape.setStroke(Color.web("#3b596b"));
        Line line = drawLine(cm.getX(), cm.getY(), xCentre, yCentre);
        detectIntersection(line, shape);
        createArrow();

        return shape;
    }

    private Text createText(String name, int fontSize) {

        final Text text = new Text(name);

        text.setFont(new Font(fontSize));
        text.setBoundsType(TextBoundsType.VISUAL);

        return text;

    }

    private Line drawLine(double startX, double startY, double endX, double endY) {

        Line line = new Line();

        line.setStroke(Color.LIGHTGRAY);
        line.setStrokeWidth(3);
        line.setStartX(startX);
        line.setStartY(startY);
        line.setEndX(endX);
        line.setEndY(endY);
        applicationScreen.getChildren().add(line);
        line.toBack();
        return line;
    }

    private void createArrow() {
        Polygon arrow = new Polygon();
        arrow.getPoints().addAll(new Double[]{
            0.0, 0.0,
            1000.0, 0.0,
            600.0, 600.0});

        arrow.setFill(Color.web("#85bade"));
        
        applicationScreen.getChildren().add(arrow);
        arrow.toFront();
    }

    private void detectIntersection(Line line, Shape shape) {
        Shape collisionArea = Shape.intersect(line, shape);
        collisionX = collisionArea.getBoundsInParent().getMinX();
        collisionY = collisionArea.getBoundsInParent().getMinY();
    }
}

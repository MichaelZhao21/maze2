package xyz.michaelzhao.maze2;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import xyz.michaelzhao.maze2.MazeRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class MazeApplication extends Application {
    VBox mainMenu;
    Stage stage;
    MazeRunner mazeRunner;
    Text infoText;
    Button[] menuItems;

    @Override
    public void start(Stage stage) {
        // Create main stage and scene
        this.stage = stage;
        Group root = new Group();
        Scene scene = new Scene(root, 200, 250);

        // Instantiate main algorithm runner
        mazeRunner = new MazeRunner();

        // Create the main menu
        mainMenu = new VBox();
        mainMenu.setAlignment(Pos.CENTER);
        mainMenu.setPadding(new Insets(20, 20, 20, 20));
        mainMenu.setSpacing(5);

        // Create all menu buttons
        menuItems = new Button[6];
        menuItems[0] = new Button("Generate Maze");
        menuItems[0].setOnAction(action -> menuOption(0));
        menuItems[1] = new Button("Import Maze");
        menuItems[1].setOnAction(action -> menuOption(1));
        menuItems[2] = new Button("Export Maze");
        menuItems[2].setOnAction(action -> menuOption(2));
        menuItems[2].setDisable(true);
        menuItems[3] = new Button("Display Maze");
        menuItems[3].setOnAction(action -> menuOption(3));
        menuItems[3].setDisable(true);
        menuItems[4] = new Button("Solve Maze");
        menuItems[4].setOnAction(action -> menuOption(4));
        menuItems[4].setDisable(true);
        menuItems[5] = new Button("Solve Maze with Wall Breaks");
        menuItems[5].setOnAction(action -> menuOption(5));
        menuItems[5].setDisable(true);

        // Create info text
        infoText = new Text("Maze: None");

        // Add all items to menu container and container to root
        mainMenu.getChildren().addAll(infoText, menuItems[0], menuItems[1], menuItems[2], menuItems[3], menuItems[4], menuItems[5]);
        root.getChildren().add(mainMenu);

        // Show stage
        stage.setTitle("Maze");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Changes sidebar to display other content
     *
     * @param value value of menu button
     */
    public void menuOption(int value) {
        if (value == 0) {
            // Create stage to prompt for n and m
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(stage);
            Group dialogRoot = new Group();
            Scene dialogScene = new Scene(dialogRoot, 150, 100);

            // Create input grid
            GridPane grid = new GridPane();
            grid.setPadding(new Insets(10, 10, 10, 10));
            grid.setVgap(5);
            grid.setHgap(5);

            // Create n value label
            Text nLabel = new Text();
            nLabel.setText("n");
            grid.add(nLabel, 0, 0);

            // Create m value label
            Text mLabel = new Text();
            mLabel.setText("m");
            grid.add(mLabel, 0, 1);

            // Create n value input box grid
            TextField nField = new TextField();
            nField.setPromptText("Enter the n value");
            nField.setPrefColumnCount(8);
            grid.add(nField, 1, 0);

            // Create m value input box grid
            TextField mField = new TextField();
            mField.setPromptText("Enter the m value");
            mField.setPrefColumnCount(8);
            grid.add(mField, 1, 1);

            // Create submit button
            Button submit = new Button();
            submit.setText("Generate!");
            submit.setOnAction(action -> {
                try {
                    // Parse n and m and check for bounds
                    int n = Integer.parseInt(nField.getText());
                    int m = Integer.parseInt(mField.getText());
                    if (n > 225 || n < 3 || m > 225 || m < 3) {
                        showErrorPopup(dialog, "ERROR: Please enter 2 integers between 3 and 225 for n and m values.");
                    } else {
                        // If all conditions met, generate maze
                        mazeRunner.generateMaze(n, m);

                        // Close dialog and show final maze!
                        dialog.close();
                        showMaze(false, false);
                        updateInfo();
                    }
                } catch (NumberFormatException e) {
                    showErrorPopup(dialog, "ERROR: Please enter 2 integers between 3 and 225 for n and m values.");
                }
            });
            grid.add(submit, 1, 2);

            // Show dialog
            dialogRoot.getChildren().add(grid);
            dialog.setScene(dialogScene);
            dialog.show();
        } else if (value == 1) {
            // Import Maze
            // Open the file chooser dialog
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open saved maze file");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text files", "*.txt"));
            File file = fileChooser.showOpenDialog(stage);

            // Check to make sure file is chosen (cancel = do nothing)
            if (file != null) {
                try {
                    // Check to make sure file type is plaintext and load maze
                    if (Files.probeContentType(file.toPath()).equals("text/plain")) {
                        mazeRunner.loadMaze(file);
                        updateInfo();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (value == 2) {
            // Export Maze
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Pick where to save the maze file");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text files", "*.txt"));
            File file = fileChooser.showSaveDialog(stage);

            // Check to make sure file is chosen (cancel = do nothing)
            if (file != null) {
                try {
                    // Check to make sure file type is plaintext and save maze
                    if (Files.probeContentType(file.toPath()).equals("text/plain")) {
                        mazeRunner.saveMaze(file);
                        updateInfo();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else if (value == 3) {
            // Show Maze
            showMaze(false, false);
        } else if (value == 4) {
            // Solve Maze
            boolean solveSuccess = mazeRunner.solveMaze();
            if (solveSuccess) showMaze(true, false);
            else {
                showErrorPopup(stage, "ERROR: No possible path found!!!");
            }
        } else if (value == 5) {
            // Solve Maze with Breaks
            // Create stage to prompt for p
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(stage);
            Group dialogRoot = new Group();
            Scene dialogScene = new Scene(dialogRoot, 150, 100);

            // Create input grid
            GridPane grid = new GridPane();
            grid.setPadding(new Insets(10, 10, 10, 10));
            grid.setVgap(5);
            grid.setHgap(5);

            // Create p value label
            Text pLabel = new Text();
            pLabel.setText("penalty");
            grid.add(pLabel, 0, 0);

            // Create p value input box grid
            TextField pField = new TextField();
            pField.setPromptText("Enter the penalty value");
            pField.setPrefColumnCount(4);
            grid.add(pField, 1, 0);

            // Create submit button
            Button submit = new Button();
            submit.setText("Solve!");
            submit.setOnAction(action -> {
                try {
                    // Get p value and get the lowest cost path
                    int p = Integer.parseInt(pField.getText());

                    // If p is an invalid number, show error
                    if (p < 2) {
                        showErrorPopup(stage, "Please enter a p value that is greater than 1!");
                        return;
                    }

                    // Close p value dialog and solve the maze!
                    dialog.close();
                    boolean solveSuccess = mazeRunner.solveMazeWithBreaks(p);

                    // Show the maze when it is solved, and it should not error ://
                    if (solveSuccess) showMaze(true, true);
                    else
                        showErrorPopup(stage, "ERROR: Path should be possible because all walls can be broken, but I cannot find a path TwT");
                } catch (NumberFormatException e) {
                    // Show error if user does not enter an integer
                    showErrorPopup(dialog, "ERROR: Please enter an integer for the penalty value");
                }
            });
            grid.add(submit, 1, 2);

            // Show dialog
            dialogRoot.getChildren().add(grid);
            dialog.setScene(dialogScene);
            dialog.show();
        }
    }

    /**
     * Displays the maze in a dialog
     */
    public void showMaze(boolean solved, boolean breakWalls) {
        // Don't show maze if maze is null
        if (mazeRunner.maze == null) return;

        // Calculate the ideal dimensions
        int cellSize = 2;
        if (mazeRunner.m < 100 && mazeRunner.n < 100) cellSize = 4;
        if (mazeRunner.m < 50 && mazeRunner.n < 50) cellSize = 7;
        if (mazeRunner.m < 25 && mazeRunner.n < 25) cellSize = 18;
        if (mazeRunner.m < 10 && mazeRunner.n < 10) cellSize = 30;

        // Calculate dimensions
        int width = cellSize * (mazeRunner.m * 2 + 1);
        int height = cellSize * (mazeRunner.n * 2 + 1);

        // Create stage to show maze
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);
        Group dialogRoot = new Group();
        Scene dialogScene = new Scene(dialogRoot, width, height + (solved ? (mazeRunner.m * mazeRunner.n > 10000 ? 150 : (mazeRunner.m * mazeRunner.n > 2500 ? 100 : 50)) : 0));

        // Create VBox for text/canvas
        VBox box = new VBox();
        box.setSpacing(5);
        box.setPadding(Insets.EMPTY);
        box.setAlignment(Pos.CENTER);

        // Create canvas and get graphics context
        Canvas canvas = new Canvas();
        canvas.setWidth(width);
        canvas.setHeight(height);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);

        // Print out all walls
        int rx = 0, ry = 0;
        for (int i = 0; i < mazeRunner.m + 1; i++, ry += 2 * cellSize) {
            gc.fillRect(rx, ry, cellSize * (mazeRunner.m * 2 + 1), cellSize);
        }
        ry = 0;
        for (int j = 0; j < mazeRunner.m + 1; j++, rx += 2 * cellSize) {
            gc.fillRect(rx, ry, cellSize, cellSize * (mazeRunner.n * 2 + 1));
        }

        // BREAK WALLS >:DDDDD
        ry = cellSize;
        gc.setFill(Color.WHITE);
        for (int i = 0; i < mazeRunner.n; i++, ry += 2 * cellSize) {
            rx = cellSize;
            for (int j = 0; j < mazeRunner.m; j++, rx += 2 * cellSize) {
                if (mazeRunner.maze[i][j].east)
                    gc.fillRect(rx + cellSize, ry, cellSize, cellSize);
                if (mazeRunner.maze[i][j].south)
                    gc.fillRect(rx, ry + cellSize, cellSize, cellSize);
            }
        }

        // Clear start and finish block
        gc.fillRect(0, cellSize, cellSize, cellSize);
        gc.fillRect(cellSize * (mazeRunner.m * 2), cellSize * (mazeRunner.n * 2 - 1), cellSize, cellSize);

        // Create output list if solved
        MazeNode curr = mazeRunner.maze[0][0];
        StringBuilder sb = new StringBuilder();
        if (solved) {
            while (curr.child != null) {
                if (curr.child.y < curr.y) sb.append("N");
                else if (curr.child.x > curr.x) sb.append("E");
                else if (curr.child.y > curr.y) sb.append("S");
                else sb.append("W");
                curr = curr.child;
            }
        }

        // Add text
        Text path = new Text(sb.toString());
        path.setTextAlignment(TextAlignment.CENTER);
        path.setWrappingWidth(width);

        // Add children
        box.getChildren().addAll(canvas, path);
        dialogRoot.getChildren().add(box);
        dialog.setScene(dialogScene);
        dialog.show();

        // If the maze is solved, print that solution
        try {
            if (solved) printPath(cellSize, gc, breakWalls);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void printPath(int cellSize, GraphicsContext gc, boolean breakWalls) throws InterruptedException {
        // Colors
        Paint pathColor = Paint.valueOf("#a69ff4");
        Paint wallBreakColor = Paint.valueOf("#9c16fc");
        // Paint starting and ending cells with wall
        gc.setFill(pathColor);
        gc.fillRect(0, cellSize, cellSize, cellSize);

        // MazeNode
        MazeNode curr = mazeRunner.maze[0][0];
        while (curr.child != null) {
            int rx = (2 * curr.x + 1) * cellSize;
            int ry = (2 * curr.y + 1) * cellSize;

            // Color in current square
            if (breakWalls) gc.setFill(pathColor);
            gc.fillRect(rx, ry, cellSize, cellSize);

            // Color in space between current and next square
            if (curr.y > 0 && curr.child.equals(mazeRunner.maze[curr.y - 1][curr.x])) {
                if (breakWalls && !curr.north) gc.setFill(wallBreakColor);
                gc.fillRect(rx, ry - cellSize, cellSize, cellSize);
            } else if (curr.x < mazeRunner.m - 1 && curr.child.equals(mazeRunner.maze[curr.y][curr.x + 1])) {
                if (breakWalls && !curr.east) gc.setFill(wallBreakColor);
                gc.fillRect(rx + cellSize, ry, cellSize, cellSize);
            } else if (curr.y < mazeRunner.n - 1 && curr.child.equals(mazeRunner.maze[curr.y + 1][curr.x])) {
                if (breakWalls && !curr.south) gc.setFill(wallBreakColor);
                gc.fillRect(rx, ry + cellSize, cellSize, cellSize);
            } else {
                if (breakWalls && !curr.west) gc.setFill(Paint.valueOf("#4332fc"));
                gc.fillRect(rx - cellSize, ry, cellSize, cellSize);
            }

            // Move to next node in path
            curr = curr.child;
        }

        // Draw ending squares
        if (breakWalls) gc.setFill(Paint.valueOf("#a69ff4"));
        gc.fillRect(cellSize * (mazeRunner.m * 2), cellSize * (mazeRunner.n * 2 - 1), cellSize, cellSize);
        gc.fillRect(cellSize * (mazeRunner.m * 2 - 1), cellSize * (mazeRunner.n * 2 - 1), cellSize, cellSize);
    }

    public void showErrorPopup(Stage owner, String message) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(owner);
        Group dialogRoot = new Group();
        Scene dialogScene = new Scene(dialogRoot, 300, 50);

        // Create internal text
        Text text = new Text(10, 20, message);
        text.setWrappingWidth(280);
        text.setTextAlignment(TextAlignment.CENTER);

        // Show dialog
        dialogRoot.getChildren().add(text);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    /**
     * Updates the text and buttons when
     * maze is imported/generated
     */
    public void updateInfo() {
        // If no maze, don't do anything
        if (mazeRunner.maze == null) return;

        // Set info text to size of maze
        infoText.setText(String.format("Maze: %d x %d", mazeRunner.n, mazeRunner.m));

        // Enable maze actions
        for (int i = 2; i < 6; i++)
            menuItems[i].setDisable(false);

    }

    public static void main(String[] args) {
        launch();
    }
}
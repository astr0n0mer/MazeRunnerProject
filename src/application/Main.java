package application;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Creates a random maze, then solves it by finding a path from the upper left
 * corner to the lower right corner. After doing one maze, it waits a while then
 * starts over by creating a new random maze. The point of the program is to
 * visualize the process.
 */

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}
	// ------------------------------------------------------------------------

	int[][] maze; // Description of state of maze. The value of maze[i][j]
					// is one of the constants wallCode, pathcode, emptyCode,
					// or visitedCode. (Value can also be negative, temporarily,
					// inside createMaze().)
					// A maze is made up of walls and corridors. maze[i][j]
					// is either part of a wall or part of a corridor. A cell
					// that is part of a corridor is represented by pathCode
					// if it is part of the current path through the maze, by
					// visitedCode if it has already been explored without finding
					// a solution, and by emptyCode if it has not yet been explored.

	final static int backgroundCode = 0;
	final static int wallCode = 1;
	final static int pathCode = 2;
	final static int emptyCode = 3;
	final static int visitedCode = 4;

	Canvas canvas; // the canvas where the maze is drawn and which fills the whole window
	GraphicsContext g; // graphics context for drawing on the canvas

	Color color[];

	// int rows = 31; // number of rows of cells in maze, including a wall around
	// edges
	// int columns = 41; // number of columns of cells in maze, including a wall
	// around edges

	int rows;
	int columns;
	int blockSize; // size of each cell
	Color passageColor;
	Color wallColor;
	Scene scene1, scene2, scene3;

	// animation stuff
//	int sleepTime = 5; // wait time after solving one maze before making another, in milliseconds
//	int speedSleep = 20; // short delay between steps in making and solving maze

	public void start(Stage stage) {

		// Made changes here
		color = new Color[] { Color.rgb(200, 0, 0), Color.rgb(200, 0, 0), Color.rgb(128, 128, 255), Color.WHITE,
				Color.rgb(200, 200, 200) };

		GridPane menu = new GridPane();

		Group group = new Group(menu);

//		VBox container1 = new VBox();
		Group container1 = new Group();
		scene1 = new Scene(container1, 1366, 707);
		scene2 = new Scene(group, scene1.getWidth(), scene1.getHeight());

		// Scene 1
		Label label1 = new Label("Maze Runner");
		label1.setTextFill(Color.CORNFLOWERBLUE);
		label1.setFont(new Font("forte", 105));
		label1.setLayoutX((scene1.getWidth() / 2) - 280);
		label1.setLayoutY(200);

		Label label2 = new Label("Press Any Key to Continue");
		Font welcomeFont = Font.font("Agency FB", FontWeight.BOLD, 30);
		label2.setFont(welcomeFont);
		label2.setLayoutX(scene1.getWidth() / 2 - 90);
		label2.setLayoutY(340);
		label2.setTextFill(Color.GREY);

		FadeTransition ft = new FadeTransition(Duration.seconds(3), label2);
		ft.setFromValue(1);
		ft.setToValue(0);
		ft.setCycleCount(Animation.INDEFINITE);
		ft.play();

		container1.getChildren().addAll(label1, label2);
//		container1.setAlignment(Pos.CENTER);

		scene1.setOnKeyPressed((KeyEvent ke) -> stage.setScene(scene2));

		// Scene 2
		menu.setHgap(100);
		menu.setVgap(40);
		menu.setPadding(new Insets(20));
		int rowCounter = 0;

		Label xCellsLabel = new Label("Cells in each row:");
		Font normalFont = Font.font("Agency FB", FontWeight.BOLD, 35);
		xCellsLabel.setFont(normalFont);

		Label yCellsLabel = new Label("Cells in each column:");
		yCellsLabel.setFont(normalFont);

		Label passageLengthLabel = new Label("Passage length (px):");
		passageLengthLabel.setFont(normalFont);

		Spinner<Integer> xCellsInput = new Spinner<>(2, 100, 20);
		xCellsInput.setEditable(true);
		xCellsInput.setScaleX(1.15);
		xCellsInput.setScaleY(1.15);

		Spinner<Integer> yCellsInput = new Spinner<>(2, 100, 20);
		yCellsInput.setEditable(true);
		yCellsInput.setScaleX(1.15);
		yCellsInput.setScaleY(1.15);

		Spinner<Integer> passageLengthInput = new Spinner<>(5, 50, 25);
		passageLengthInput.setScaleX(1.15);
		passageLengthInput.setScaleY(1.15);

		menu.addRow(rowCounter++, xCellsLabel, xCellsInput);
		menu.addRow(rowCounter++, yCellsLabel, yCellsInput);
		menu.addRow(rowCounter++, passageLengthLabel, passageLengthInput);

		Label passageColorLabel = new Label("Passage Color:");
		passageColorLabel.setFont(normalFont);

		Label wallColorLabel = new Label("Wall Color:");
		wallColorLabel.setFont(normalFont);

		ColorPicker passageColorPicker = new ColorPicker(Color.RED);
		passageColorPicker.setScaleX(1.15);
		passageColorPicker.setScaleY(1.15);

		ColorPicker wallColorPicker = new ColorPicker(Color.BLACK);
		wallColorPicker.setScaleX(1.15);
		wallColorPicker.setScaleY(1.15);

		menu.addRow(rowCounter++, passageColorLabel, passageColorPicker);
		menu.addRow(rowCounter++, wallColorLabel, wallColorPicker);

		Button generateMazeBtn = new Button("Generate Maze");
		generateMazeBtn.setLayoutX((scene2.getWidth() / 2) - 300);
		generateMazeBtn.setLayoutY(scene2.getHeight() - 150);
		generateMazeBtn.setScaleX(1.15);
		generateMazeBtn.setScaleY(1.15);

		Font buttonTextFont = Font.font("Agency FB", FontWeight.BOLD, 28);
		generateMazeBtn.setFont(buttonTextFont);

		group.getChildren().add(generateMazeBtn);

		generateMazeBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				if ((xCellsInput.getValue() & 1) == 1)
					columns = xCellsInput.getValue();
				else
					columns = xCellsInput.getValue() + 1;

				if ((yCellsInput.getValue() & 1) == 1)
					rows = yCellsInput.getValue();
				else
					rows = yCellsInput.getValue() + 1;

				blockSize = passageLengthInput.getValue();
				passageColor = passageColorPicker.getValue();

				color[pathCode] = passageColor;

				wallColor = wallColorPicker.getValue();
				maze = new int[rows][columns];
				canvas = new Canvas(columns * blockSize, rows * blockSize);
				g = canvas.getGraphicsContext2D();
				g.setFill(wallColor);
				g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

				Pane root = new Pane(canvas);
				ScrollPane sp = new ScrollPane();
				sp.setLayoutY(30);
				sp.setLayoutX(20);
				sp.setPrefSize(scene2.getWidth() - generateMazeBtn.getWidth(), scene2.getHeight() - 50);

				sp.setContent(root);

				Button solveMazeBtn = new Button("Solve Maze");

				solveMazeBtn.setLayoutX(sp.getLayoutX() + sp.getPrefWidth() + 10);
				solveMazeBtn.setLayoutY(sp.getLayoutY() + 10);
				solveMazeBtn.setScaleX(1.15);
				solveMazeBtn.setScaleY(1.15);

//				Font font9 = Font.font("Agency FB", FontWeight.BOLD, 28);
				solveMazeBtn.setFont(buttonTextFont);

				solveMazeBtn.setOnAction(ev -> solveMaze(1, 1, 100));

				Button mainMenuBtn = new Button("Main Menu");
				mainMenuBtn.setLayoutX(solveMazeBtn.getLayoutX());
				mainMenuBtn.setLayoutY(sp.getLayoutY() + 100);
				mainMenuBtn.setScaleX(1.15);
				mainMenuBtn.setScaleY(1.15);

//				Font font8 = Font.font("Agency FB", FontWeight.BOLD, 28);
				mainMenuBtn.setFont(buttonTextFont);

				mainMenuBtn.setOnAction(ev -> stage.setScene(scene2));

				Group group2 = new Group(sp);

				group2.getChildren().addAll(solveMazeBtn, mainMenuBtn);

				scene3 = new Scene(group2, scene1.getWidth(), scene1.getHeight());
				scene3.setFill(Color.PAPAYAWHIP);

				stage.setScene(scene3);

				makeMaze();

			}
		});

		scene1.setFill(Color.PAPAYAWHIP);
		scene2.setFill(Color.PAPAYAWHIP);
		stage.setScene(scene1);
		stage.setResizable(false);
//		stage.setFullScreen(true);
		stage.setTitle("Maze Runner");
		stage.show();

	}

	void drawSquare(int row, int column, int colorCode) {
		// Fill specified square of the grid with the
		// color specified by colorCode, which has to be
		// one of the constants emptyCode, wallCode, etc.

		g.setFill(color[colorCode]);
		int x = blockSize * column;
		int y = blockSize * row;
		g.fillRect(x, y, blockSize, blockSize);

	}

	void makeMaze() {
		// Create a random maze. The strategy is to start with
		// a grid of disconnected "rooms" separated by walls,
		// then look at each of the separating walls, in a random
		// order. If tearing down a wall would not create a loop
		// in the maze, then tear it down. Otherwise, leave it in place.

		int i, j;
		int emptyCt = 0; // number of rooms
		int wallCt = 0; // number of walls
		int[] wallrow = new int[(rows * columns) / 2]; // position of walls between rooms
		int[] wallcol = new int[(rows * columns) / 2];
		for (i = 0; i < rows; i++) // start with everything being a wall
			for (j = 0; j < columns; j++)
				maze[i][j] = wallCode;
		for (i = 1; i < rows - 1; i += 2) { // make a grid of empty rooms
			for (j = 1; j < columns - 1; j += 2) {
				emptyCt++;
				maze[i][j] = -emptyCt; // each room is represented by a different negative number
				if (i < rows - 2) { // record info about wall below this room
					wallrow[wallCt] = i + 1;
					wallcol[wallCt] = j;
					wallCt++;
				}
				if (j < columns - 2) { // record info about wall to right of this room
					wallrow[wallCt] = i;
					wallcol[wallCt] = j + 1;
					wallCt++;
				}
			}
		}

		g.setFill(color[emptyCode]);

		int ct = 50;

		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < columns; c++) {
				if (maze[r][c] < 0) {

					int f = c;
					int m = r;

					Timeline t2 = new Timeline(new KeyFrame(Duration.millis(500 + ct),
							e -> g.fillRect(f * blockSize, m * blockSize, blockSize, blockSize)));
					t2.play();

					ct += 50;
				}
			}
		}

		int r;
		for (i = wallCt - 1; i > 0; i--) {
			r = (int) (Math.random() * i); // choose a wall randomly and maybe tear it down

			tearDown(wallrow[r], wallcol[r], 500 + ct);

			wallrow[r] = wallrow[i];
			wallcol[r] = wallcol[i];

			ct += 100;
		}

		for (i = 1; i < rows - 1; i++) { // replace negative values in maze[][] with emptyCode
			for (j = 1; j < columns - 1; j++) {
				if (maze[i][j] < 0)
					maze[i][j] = emptyCode;
			}

		}
	}

	void tearDown(int row, int col, int ex) {
		// Tear down a wall, unless doing so will form a loop. Tearing down a wall
		// joins two "rooms" into one "room". (Rooms begin to look like corridors
		// as they grow.) When a wall is torn down, the room codes on one side are
		// converted to match those on the other side, so all the cells in a room
		// have the same code. Note that if the room codes on both sides of a
		// wall already have the same code, then tearing down that wall would
		// create a loop, so the wall is left in place.

		if (row % 2 == 1 && maze[row][col - 1] != maze[row][col + 1]) {
			// row is odd; wall separates rooms horizontally
			fill(row, col - 1, maze[row][col - 1], maze[row][col + 1]);
			maze[row][col] = maze[row][col + 1];
			drawSquare(row, col, emptyCode);
		}

		else if (row % 2 == 0 && maze[row - 1][col] != maze[row + 1][col]) {
			// row is even; wall separates rooms vertically

			fill(row - 1, col, maze[row - 1][col], maze[row + 1][col]);
			maze[row][col] = maze[row + 1][col];
			drawSquare(row, col, emptyCode);
		}
	}

	void fill(int row, int col, int replace, int replaceWith) {
		// called by tearDown() to change "room codes".
		// (My algorithm really should have used the standard
		// union/find data structure.)

		if (maze[row][col] == replace) {
			maze[row][col] = replaceWith;
			fill(row + 1, col, replace, replaceWith);
			fill(row - 1, col, replace, replaceWith);
			fill(row, col + 1, replace, replaceWith);
			fill(row, col - 1, replace, replaceWith);
		}
	}

	boolean solveMaze(int row, int col, int ct) {
		// Try to solve the maze by continuing current path from position
		// (row,col). Return true if a solution is found. The maze is
		// considered to be solved if the path reaches the lower right cell.
		if (maze[row][col] == emptyCode) {
			maze[row][col] = pathCode; // add this cell to the path

			Timeline t = new Timeline(new KeyFrame(Duration.millis(500 + ct), e -> drawSquare(row, col, pathCode)));
			t.play();
			ct += 100;

			if (row == rows - 2 && col == columns - 2)
				return true; // path has reached goal

			if (solveMaze(row - 1, col, ct) || // try to solve maze by extending path
					solveMaze(row, col - 1, ct) || // in each possible direction
					solveMaze(row + 1, col, ct) || solveMaze(row, col + 1, ct))
				return true;

			maze[row][col] = visitedCode; // mark cell as having been visited

			Timeline t2 = new Timeline(new KeyFrame(Duration.millis(500 + ct), e -> drawSquare(row, col, visitedCode)));
			t2.play();

		}
		return false;
	}
}

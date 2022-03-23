package application;

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
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Creates a random maze, then solves it by finding a path from the upper left
 * corner to the lower right corner. After doing one maze, it waits a while then
 * starts over by creating a new random maze. The point of the program is to
 * visualize the process.
 */

public class Main extends Application implements Runnable {

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

	// h --> last color changed
	Color color[];

	// colors associated with the preceding 5 constants;
	// int rows = 31; // number of rows of cells in maze, including a wall around
	// edges
	// int columns = 41; // number of columns of cells in maze, including a wall
	// around edges

	int rows;
	int columns;
	int blockSize; // size of each cell
	Color passageColor;
	Color wallColor;

	// animation stuff
	int sleepTime = 4000; // wait time after solving one maze before making another, in milliseconds
	int speedSleep = 20; // short delay between steps in making and solving maze

	public void start(Stage stage) {
		// Made changes here
		color = new Color[] { Color.rgb(200, 0, 0), Color.rgb(200, 0, 0), Color.rgb(128, 128, 255), Color.WHITE,
				Color.rgb(200, 200, 200) };

		GridPane menu = new GridPane();

		Group group = new Group(menu);

		VBox container1 = new VBox();
//		VBox container2 = new VBox();
//		Scene scene1 = new Scene(container1, 1550, 800);
		Scene scene1 = new Scene(container1, 1000, 600);
		Scene scene2 = new Scene(group, scene1.getWidth(), scene1.getHeight());
//		Scene scene3;

		// Scene 1
		Label label1 = new Label("Maze Runner");
		// label1.setFont(new Font("Consolas", 60));
		label1.setTextFill(Color.CORNFLOWERBLUE);

		// label1.setLayoutX(485);
		// label1.setLayoutY(270);
		label1.setFont(new Font("forte", 105));
		;

		// Button btn1 = new Button("Go to Scene 2");
		// btn1.setOnAction(e -> stage.setScene(scene2));

		Label label2 = new Label("Press Any Key to Continue");
		Font font = Font.font("Agency FB", FontWeight.BOLD, 30);

		label2.setFont(font);
		label2.setTextFill(Color.GREY);

		container1.getChildren().addAll(label1, label2);
		// container1.setLayoutX(485);
		// container1.setLayoutY(270);
		container1.setAlignment(Pos.CENTER);
		// container1.backgroundProperty();
		// scene1.setFill(Color.DARKGREY);

		scene1.setOnKeyPressed((KeyEvent ke) -> stage.setScene(scene2));

		// Scene 2
		menu.setHgap(100);
		menu.setVgap(40);
		menu.setPadding(new Insets(20));
		int rowCounter = 0;

		Label xCellsLabel = new Label("Cells in each row:");

		Font font2 = Font.font("Agency FB", FontWeight.BOLD, 35);
		xCellsLabel.setFont(font2);

		Label yCellsLabel = new Label("Cells in each column:");

		Font font3 = Font.font("Agency FB", FontWeight.BOLD, 35);
		yCellsLabel.setFont(font3);

		Label passageLengthLabel = new Label("Passage length (px):");

		Font font4 = Font.font("Agency FB", FontWeight.BOLD, 35);
		passageLengthLabel.setFont(font4);

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
//		menu.addRow(rowCounter++, wallLengthLabel, wallThicknessInput);

		Label passageColorLabel = new Label("Passage Color:");

		Font font5 = Font.font("Agency FB", FontWeight.BOLD, 35);
		passageColorLabel.setFont(font5);

		Label wallColorLabel = new Label("Wall Color:");

		Font font6 = Font.font("Agency FB", FontWeight.BOLD, 35);
		wallColorLabel.setFont(font6);

		ColorPicker passageColorPicker = new ColorPicker(Color.RED);
		passageColorPicker.setScaleX(1.15);
		passageColorPicker.setScaleY(1.15);

		ColorPicker wallColorPicker = new ColorPicker(Color.BLACK);
		wallColorPicker.setScaleX(1.15);
		wallColorPicker.setScaleY(1.15);

		menu.addRow(rowCounter++, passageColorLabel, passageColorPicker);
		menu.addRow(rowCounter++, wallColorLabel, wallColorPicker);

		// maze = new int[xCellsInput.getValue()][yCellsInput.getValue()];

		Button btn2 = new Button("Generate Maze");
		btn2.setLayoutX((scene2.getWidth() / 2) - 300);
		btn2.setLayoutY(scene2.getHeight() - 150);
		btn2.setScaleX(1.15);
		btn2.setScaleY(1.15);

		Font font7 = Font.font("Agency FB", FontWeight.BOLD, 28);
		btn2.setFont(font7);

		group.getChildren().add(btn2);
		// menu.addRow(rowCounter++, btn2);

		btn2.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {

				if ((xCellsInput.getValue() & 1) == 1)
					rows = xCellsInput.getValue();

				else
					rows = xCellsInput.getValue() + 1;

				if ((yCellsInput.getValue() & 1) == 1)
					columns = yCellsInput.getValue();

				else
					columns = yCellsInput.getValue() + 1;

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
//				sp.setPrefSize(scene2.getWidth(), scene2.getHeight());

				sp.setContent(root);

				Button back = new Button("Main Menu");
				back.setLayoutX(scene2.getWidth() - 300);
				back.setLayoutY(30);
				back.setScaleX(1.15);
				back.setScaleY(1.15);

				Font font8 = Font.font("Agency FB", FontWeight.BOLD, 28);
				back.setFont(font8);

				back.setOnAction(ev -> stage.setScene(scene2));

				Group group2 = new Group(sp);

				group2.getChildren().add(back);

//				Thread runner = new Thread(new Main());
//				runner.setDaemon(true); // so thread won't stop program from ending
//				runner.start();

				Scene scene3 = new Scene(group2, scene1.getWidth(), scene1.getHeight());
				// scene3.setFill(Color.GRAY);

				stage.setScene(scene3);

				run();

			}
		});

		stage.setScene(scene1);
		stage.setResizable(true);
		stage.setTitle("Maze Runner");
		stage.show();

//		Thread runner = new Thread(this);
//		//runner.setDaemon(true); // so thread won't stop program from ending
//		runner.start();
	}

	void drawSquare(int row, int column, int colorCode) {
		// Fill specified square of the grid with the
		// color specified by colorCode, which has to be
		// one of the constants emptyCode, wallCode, etc.
//		
//		Platform.runLater(() -> {
//			g.setFill(color[colorCode]);
//			int x = blockSize * column;
//			int y = blockSize * row;
//			g.fillRect(x, y, blockSize, blockSize);
//		});
//		
		g.setFill(color[colorCode]);
		int x = blockSize * column;
		int y = blockSize * row;
		g.fillRect(x, y, blockSize, blockSize);

	}

	public void run() {
		// Run method for thread repeatedly makes a maze and then solves it.
		// Note that all access to the canvas by the thread is done using
		// Platform.runLater(), so that all drawing to the canvas actually
		// takes place on the application thread.

//        while (true) {
//		try {
//			Thread.sleep(1000);
//		} // wait a bit before starting
//		catch (InterruptedException e) {
//		}

		makeMaze();
		solveMaze(1, 1);

//            synchronized(this) {
//                try { wait(sleepTime); }
//                catch (InterruptedException e) { }
//            }
//            Platform.runLater( () -> {
//                g.setFill(color[backgroundCode]);
//                g.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
//            });
//        }

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

//		Platform.runLater(() -> {
//			g.setFill(color[emptyCode]);
//			for (int r = 0; r < rows; r++) {
//				for (int c = 0; c < columns; c++) {
//					if (maze[r][c] < 0)
//						g.fillRect(c * blockSize, r * blockSize, blockSize, blockSize);
//				}
//			}
//		});
//		
//        synchronized(this) {
//            try { wait(1000); }
//            catch (InterruptedException e) { }
//        }

		g.setFill(color[emptyCode]);
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < columns; c++) {
				if (maze[r][c] < 0)
					g.fillRect(c * blockSize, r * blockSize, blockSize, blockSize);
			}
		}

		int r;
		for (i = wallCt - 1; i > 0; i--) {
			r = (int) (Math.random() * i); // choose a wall randomly and maybe tear it down
			tearDown(wallrow[r], wallcol[r]);
			wallrow[r] = wallrow[i];
			wallcol[r] = wallcol[i];
		}

		for (i = 1; i < rows - 1; i++) // replace negative values in maze[][] with emptyCode
			for (j = 1; j < columns - 1; j++)
				if (maze[i][j] < 0)
					maze[i][j] = emptyCode;
//        synchronized(this) {
//            try { wait(1000); }
//            catch (InterruptedException e) { }

	}

	void tearDown(int row, int col) {
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
//            synchronized(this) {
//                try { wait(speedSleep); }
//                catch (InterruptedException e) { }
//            }
		}

		else if (row % 2 == 0 && maze[row - 1][col] != maze[row + 1][col]) {
			// row is even; wall separates rooms vertically

			fill(row - 1, col, maze[row - 1][col], maze[row + 1][col]);
			maze[row][col] = maze[row + 1][col];
			drawSquare(row, col, emptyCode);
//            synchronized(this) {
//                try { wait(speedSleep); }
//                catch (InterruptedException e) { }
//            }
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

	boolean solveMaze(int row, int col) {
		// Try to solve the maze by continuing current path from position
		// (row,col). Return true if a solution is found. The maze is
		// considered to be solved if the path reaches the lower right cell.

		if (maze[row][col] == emptyCode) {
			maze[row][col] = pathCode; // add this cell to the path
			drawSquare(row, col, pathCode);
			if (row == rows - 2 && col == columns - 2)
				return true; // path has reached goal
//            try { Thread.sleep(speedSleep); }
//            catch (InterruptedException e) { }
			if (solveMaze(row - 1, col) || // try to solve maze by extending path
					solveMaze(row, col - 1) || // in each possible direction
					solveMaze(row + 1, col) || solveMaze(row, col + 1))
				return true;
			// maze can't be solved from this cell, so backtrack out of the cell

			maze[row][col] = visitedCode; // mark cell as having been visited
			drawSquare(row, col, visitedCode);
//            synchronized(this) {
//                try { wait(speedSleep); }
//                catch (InterruptedException e) { }
//            }
		}
		return false;
	}
//    Commented below line nos.
//    85
//    120, 
//    125-133
//    248, 
//    249
//    257
//    258-261
//    178-181
//    193-196
//    Added below line nos.
//    86
}
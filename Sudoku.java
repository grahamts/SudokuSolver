import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Sudoku {

	public static int[][] puzzle;
	public static int boardSize, miniGridSize;
	public static ArrayList<Node> open = new ArrayList<Node>();

	public static void main(String[] args) throws FileNotFoundException{
		long startTime = System.currentTimeMillis();
		@SuppressWarnings("resource")
		Scanner input = new Scanner(new File(args[0]));
//		Scanner input = new Scanner(new File("test.txt"));

		boardSize = input.nextInt();
		miniGridSize = input.nextInt();

		puzzle = new int[boardSize][boardSize];
		int i = 0;
		String x = "";
		while(input.hasNext()) {
			String line = input.nextLine();

			if(line.length() != boardSize) {
				continue;
			}
			line = line.replaceAll("-", "0");
			for(int j = 0; j < boardSize; j++) {
				x = "" + line.charAt(j);
				puzzle[i][j] = Integer.parseInt(x);
			}
			i++;
		}
		makeNodes();
		solver(puzzle);

		printPuzzle();
		System.out.println();
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Took " + totalTime + "ms");
	}
	// Drives the program setting domain after each new node is added
	public static int[][] solver(int[][] puzzle) {
		if(isSolution(puzzle, boardSize)) {
			return puzzle;
		} else {
			setDomains();
			addNum();
			return solver(puzzle);
		}
	}
	// Adds numbers to the board if they only have one number in their domain
	public static void addNum() {
		for(Node node : open) {
			if(node.domain.size() == 1) {
				puzzle[node.location.x][node.location.y] = node.domain.get(0);
			} else if(puzzle[node.location.x][node.location.y] == 0 && node.domain.size() == 0) {
				System.out.println("Need to backtrack");
			}
		}
	}
	// Makes a node for every blank space on the board with its row and column coordinate
	public static void makeNodes() {
		for(int row = 0; row < puzzle.length; row++) {
			for(int col = 0; col < puzzle.length; col++) {
				if(puzzle[row][col] == 0) {
					open.add(new Node(row,col));
				} 
			}
		}
	}
	// Checks rows, columns, and mini grid and updates each nodes domain
	public static void setDomains() {
		int row, col;
		for(Node node : open) {
			row = node.location.x;
			col = node.location.y;
			//Row domain
			for(int i = 0; i < puzzle.length; i++) {
				if(node.domain.contains(puzzle[row][i])) {
					node.domain.remove(node.domain.indexOf(puzzle[row][i]));
				}
			}
			//Column domain
			for(int j = 0; j < puzzle.length; j++) {
				if(node.domain.contains(puzzle[j][col])) {
					node.domain.remove(node.domain.indexOf(puzzle[j][col]));
				}
			}
			if(boardSize == 9) {
				checkMiniGrid9(node);
			} else {
				checkMiniGrid4(node);
			}
		}
	}
	// Prints sudoku puzzle
	public static void printPuzzle() {
		for(int i = 0; i < puzzle.length; i++) {
			for(int j = 0; j < puzzle.length; j++) {
				System.out.print(puzzle[i][j]);
			}
			System.out.println();
		}
	}
	// Checks to see if the sudoku board is valid
	public static boolean isSolution(int[][] grid, int size) {
		int miniGrid = (int) Math.sqrt(size);
		for (int i = 0; i < size; i++) {
			int[] row = new int[size];
			int[] square = new int[size];
			int[] column = grid[i].clone();

			for (int j = 0; j < size; j ++) {
				row[j] = grid[j][i];
				square[j] = grid[(i / miniGrid) * miniGrid + j / miniGrid][i * miniGrid % size + j % miniGrid];
			}
			if (!(validate(column) && validate(row) && validate(square)))
				return false;
		}
		return true;
	}

	public static boolean validate(int[] check) {
		int i = 0;
		Arrays.sort(check);
		for (int number : check) {
			if (number != ++i)
				return false;
		}
		return true;
	}
// Sets domain in mini grid of a 4x4 puzzle
	public static void checkMiniGrid4(Node node) {
		if (node.location.x < 2) {
			if (node.location.y < 2) {
				for (int i = 0; i < 2; i++) {
					for (int j = 0; j < 2; j++) {
						if (node.domain.contains(puzzle[i][j])) {
							node.domain.remove(node.domain
									.indexOf(puzzle[i][j]));
						}
					}
				}
			} else {
				for (int i = 0; i < 2; i++) {
					for (int j = 2; j < 4; j++) {
						if (node.domain.contains(puzzle[i][j])) {
							node.domain.remove(node.domain
									.indexOf(puzzle[i][j]));
						}
					}
				}
			}
		} else {
			if (node.location.y < 2) {
				for (int i = 2; i < 4; i++) {
					for (int j = 0; j < 2; j++) {
						if (node.domain.contains(puzzle[i][j])) {
							node.domain.remove(node.domain
									.indexOf(puzzle[i][j]));
						}
					}
				}
			} else {
				for (int i = 2; i < 4; i++) {
					for (int j = 2; j < 4; j++) {
						if (node.domain.contains(puzzle[i][j])) {
							node.domain.remove(node.domain
									.indexOf(puzzle[i][j]));
						}
					}
				}
			}
		}
	}
//	Sets the domain for each mini grid in a 9x9 puzzle
	public static void checkMiniGrid9(Node node) {
		if (node.location.x < 3) {
			if (node.location.y < 3) {
				for (int i = 0; i < 3; i++) {
					for (int j = 0; j < 3; j++) {
						if (node.domain.contains(puzzle[i][j])) {
							node.domain.remove(node.domain
									.indexOf(puzzle[i][j]));
						}
					}
				}

			} else if (node.location.y < 6) {
				for (int i = 0; i < 3; i++) {
					for (int j = 3; j < 6; j++) {
						if (node.domain.contains(puzzle[i][j])) {
							node.domain.remove(node.domain
									.indexOf(puzzle[i][j]));
						}
					}
				}

			} else {
				for (int i = 0; i < 3; i++) {
					for (int j = 6; j < 9; j++) {
						if (node.domain.contains(puzzle[i][j])) {
							node.domain.remove(node.domain
									.indexOf(puzzle[i][j]));
						}
					}
				}

			}

		} else if (node.location.x < 6) {
			if (node.location.y < 3) {
				for (int i = 3; i < 6; i++) {
					for (int j = 0; j < 3; j++) {
						if (node.domain.contains(puzzle[i][j])) {
							node.domain.remove(node.domain
									.indexOf(puzzle[i][j]));
						}
					}
				}

			} else if (node.location.y < 6) {
				for (int i = 3; i < 6; i++) {
					for (int j = 3; j < 6; j++) {
						if (node.domain.contains(puzzle[i][j])) {
							node.domain.remove(node.domain
									.indexOf(puzzle[i][j]));
						}
					}
				}

			} else {
				for (int i = 3; i < 6; i++) {
					for (int j = 6; j < 9; j++) {
						if (node.domain.contains(puzzle[i][j])) {
							node.domain.remove(node.domain
									.indexOf(puzzle[i][j]));
						}
					}
				}
			}
		} else {
			if (node.location.y < 3) {
				for (int i = 6; i < 9; i++) {
					for (int j = 0; j < 3; j++) {
						if (node.domain.contains(puzzle[i][j])) {
							node.domain.remove(node.domain
									.indexOf(puzzle[i][j]));
						}
					}
				}

			} else if (node.location.y < 6) {
				for (int i = 6; i < 9; i++) {
					for (int j = 3; j < 6; j++) {
						if (node.domain.contains(puzzle[i][j])) {
							node.domain.remove(node.domain
									.indexOf(puzzle[i][j]));
						}
					}
				}

			} else {
				for (int i = 6; i < 9; i++) {
					for (int j = 6; j < 9; j++) {
						if (node.domain.contains(puzzle[i][j])) {
							node.domain.remove(node.domain
									.indexOf(puzzle[i][j]));
						}
					}
				}
			}
		}
	}

	public static class Node {
		Point location = new Point();
		ArrayList<Integer> domain = new ArrayList<Integer>();
		// x = row, y = col
		public Node(int row, int col) {
			location.x = row;
			location.y = col;
			for(int i = 1; i < puzzle.length + 1; i ++) {
				domain.add(i);
			}
		}
	}
}

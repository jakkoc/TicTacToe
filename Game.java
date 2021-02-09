package tictactoe;

import java.util.Arrays;
import java.util.Scanner;

public class Game
{
    private static final Scanner scanner = new Scanner(System.in);
    private boolean turnOfX = true;

    private enum GameState
    {
        PLAYER_X_WON("X wins"),
        PLAYER_O_WON("O wins"),
        DRAW("Draw"),
        IMPOSSIBLE("Impossible"),
        GAME_NOT_FINISHED("Game not finished");

        private final String message;

        GameState(String message)
        {
            this.message = message;
        }

        public String getMessage()
        {
            return message;
        }
    }

    private Cell[][] board;
    private GameState gameState = GameState.GAME_NOT_FINISHED;

    private Game(Character[][] board)
    {
        this.board = new Cell[3][3];

        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                this.board[i][j] = new Cell(board[i][j]);
            }
        }
    }

    public static Game ofInput()
    {
        System.out.print("Enter cells: ");
        String input = scanner.nextLine();
        Character[][] charBoard = new Character[3][3];

        for (int i = 0; i < 9; i++)
        {
            charBoard[i / 3][i % 3] = input.charAt(i);
        }

        return new Game(charBoard);
    }

    public static Game emptyBoard() {
        Character[][] emptyBoard = {{' ', ' ', ' '},{' ',' ',' '},{' ',' ',' '}};
        return new Game(emptyBoard);
    }

    public void play() {
        while(!gameHasFinished()) {
            displayBoard();
            makeAMove();
            updateGameStatus();
        }
        displayBoard();
        System.out.println(gameState.getMessage());
    }

    private void displayBoard()
    {
        System.out.println(lineOfDashes());
        for (Cell[] cells : board)
        {
            System.out.print("| ");
            for (Cell cell : cells)
            {
                System.out.print(cell.getContent() + " ");
            }
            System.out.println("|");
        }
        System.out.println(lineOfDashes());
    }

    private void updateGameStatus()
    {
        isInInvalidState();
        checkRows(board);
        checkColumns();
        checkDiagonals();
        gameFinished();
    }

    private void makeAMove()
    {
        System.out.print("Enter the coordinates: ");
        String userInput = scanner.nextLine();
        if (!areIntegers(userInput))
        {
            System.out.println("You should enter numbers!");
            makeAMove();
            return;
        }

        Integer[] coordinates = getCoordinates(userInput);

        if (!areInRange(coordinates))
        {
            System.out.println("Coordinates should be from 1 to 3!");
            makeAMove();
            return;
        }

        if(board[coordinates[0] - 1][coordinates[1] - 1].isOccupied()) {
            System.out.println("This cell is occupied! Choose another one!");
            makeAMove();
            return;
        }

        board[coordinates[0] - 1][coordinates[1] - 1].setContent(turnOfX ? 'X' : 'O');
        turnOfX = !turnOfX;
    }

    private boolean gameHasFinished() {
        return gameState != GameState.GAME_NOT_FINISHED;
    }

    private boolean areInRange(Integer[] coordinates)
    {
        return coordinates[0] > 0 && coordinates[0] < 4 && coordinates[1] > 0 && coordinates[1] < 4;
    }

    private boolean areIntegers(String coordinates)
    {
        String[] splitted = coordinates.split(" ");

        for (String s : splitted)
        {
            for (int j = 0; j < s.length(); j++)
            {
                if (s.charAt(j) < '0' || s.charAt(j) > '9') return false;
            }
        }

        return true;
    }

    private Integer[] getCoordinates(String userInput)
    {
        Integer[] coordinates = new Integer[2];

        String[] splitted = userInput.split(" ");
        coordinates[0] = Integer.parseInt(splitted[0]);
        coordinates[1] = Integer.parseInt(splitted[1]);

        return coordinates;
    }

    private String lineOfDashes()
    {
        return "---------";
    }

    private void isInInvalidState()
    {
        if (Math.abs(countCells('X') - countCells('O')) > 1) gameState = GameState.IMPOSSIBLE;
    }

    private int countCells(char symbol)
    {
        int counter = 0;

        for (Cell[] cells : board)
        {
            for (Cell cell : cells)
            {
                if (cell.getContent() == symbol) counter++;
            }
        }

        return counter;
    }

    private void checkRows(Cell[][] board)
    {
        for (Cell[] cells : board)
        {
            if (Arrays.stream(cells).allMatch(cell -> cell.getContent() == 'X'))
            {
                if (gameState == GameState.GAME_NOT_FINISHED) gameState = GameState.PLAYER_X_WON;
                else gameState = GameState.IMPOSSIBLE;
            }
            if (Arrays.stream(cells).allMatch(cell -> cell.getContent() == 'O'))
            {
                if (gameState == GameState.GAME_NOT_FINISHED) gameState = GameState.PLAYER_O_WON;
                else gameState = GameState.IMPOSSIBLE;
            }
        }
    }

    private void checkColumns()
    {
        checkRows(rotateGameBoard());
    }

    private void checkDiagonals()
    {
        if (board[0][0].equals(board[1][1]) && board[1][1].equals(board[2][2]))
        {
            if (board[0][0].equals(new Cell('X')))
            {
                if (gameState == GameState.GAME_NOT_FINISHED) gameState = GameState.PLAYER_X_WON;
                else gameState = GameState.IMPOSSIBLE;
            } else if (board[0][0].equals(new Cell('O')))
            {
                if (gameState == GameState.GAME_NOT_FINISHED) gameState = GameState.PLAYER_O_WON;
                else gameState = GameState.IMPOSSIBLE;
            }
        } else if (board[2][0].equals(board[1][1]) && board[0][2].equals(board[1][1]))
        {
            if (board[1][1].equals(new Cell('X')))
            {
                if (gameState == GameState.GAME_NOT_FINISHED) gameState = GameState.PLAYER_X_WON;
                else gameState = GameState.IMPOSSIBLE;
            } else if (board[1][1].equals(new Cell('O')))
            {
                if (gameState == GameState.GAME_NOT_FINISHED) gameState = GameState.PLAYER_O_WON;
                else gameState = GameState.IMPOSSIBLE;
            }
        }
    }

    private void gameFinished()
    {
        if (gameState == GameState.GAME_NOT_FINISHED && countCells('X') + countCells('O') == 9)
        {
            gameState = GameState.DRAW;
        }
    }

    private Cell[][] rotateGameBoard()
    {
        Cell[][] rotated = new Cell[3][3];

        for (int i = 0; i < board.length; i++)
        {
            for (int j = 0; j < board[i].length; j++)
            {
                rotated[j][i] = board[i][j];
            }
        }

        return rotated;
    }

    private static class Cell
    {
        private char content;

        public Cell(char content)
        {
            this.content = content;
        }

        public void setContent(char content)
        {
            this.content = content;
        }

        public char getContent()
        {
            return content;
        }

        public boolean isOccupied()
        {
            return content == 'X' || content == 'O';
        }

        @Override
        public boolean equals(Object otherObject)
        {
            if (otherObject == null) return false;
            if (getClass() != otherObject.getClass()) return false;

            Cell otherCell = (Cell) otherObject;

            return otherCell.getContent() == content;
        }
    }
}

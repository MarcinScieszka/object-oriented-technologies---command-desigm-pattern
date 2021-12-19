// 1) Command
// 2) Receiver
//      - object that implements the method
//      - It's the component that performs the actual action when the command's execute() method is called
// 3) Invoker
//        - is an object that knows how to execute a given command but doesn't know how the command has been implemented
//        - It only knows the command's interface
//        - also stores and queues commands, aside from executing them (for redo / undo)
// 4) Client

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

// 4) Client
public class Solution {
    static ArrayList<Square> squares = new ArrayList<>();

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        while (true) {
            int squareId;
            int squareEdgeLength;
            int pixelsToMoveRight;
            int pixelsToMoveUp;
            int scaleMultiplier;
            int index;
            String userInput = scanner.nextLine();
            String[] userInputSplit = userInput.split(" ");

            char userCommandName = userInput.charAt(0);

            if (userCommandName == 'C') {
                squareId = Integer.parseInt(userInputSplit[1]);
                squareEdgeLength = Integer.parseInt(userInputSplit[2]);

                SquareCreate squareCreate = new SquareCreate(squareId, squareEdgeLength);
                SquareInvoker squareCreateInvoker = new SquareInvoker(squareCreate);
                squareCreateInvoker.execute();
            }
            else if (userCommandName == 'M') {
                squareId = Integer.parseInt(userInputSplit[1]);
                pixelsToMoveRight = Integer.parseInt(userInputSplit[2]);
                pixelsToMoveUp = Integer.parseInt(userInputSplit[3]);

                index = findSquareById(squareId);
                if (index >= 0) {
                    SquareMove squareMove = new SquareMove(squares.get(index), pixelsToMoveRight, pixelsToMoveUp);
                    SquareInvoker squareMoveInvoker = new SquareInvoker(squareMove);
                    squareMoveInvoker.execute();
                }
            }
            else if (userCommandName == 'S') {
                squareId = Integer.parseInt(userInputSplit[1]);
                scaleMultiplier = Integer.parseInt(userInputSplit[2]);

                index = findSquareById(squareId);
                if (index >= 0) {
                    SquareScale squareScale = new SquareScale(squares.get(index), scaleMultiplier);
                    SquareInvoker scaleInvoker = new SquareInvoker(squareScale);
                    scaleInvoker.execute();
                }
            }
            else if (userCommandName == 'U') {
                SquareInvoker squareUndoInvoker = new SquareInvoker(SquareInvoker.undoStore.getLast());
                squareUndoInvoker.undo();
            }
            else if (userCommandName == 'R') {
                SquareInvoker squareUndoInvoker = new SquareInvoker(SquareInvoker.redoStore.getLast());
                squareUndoInvoker.redo();
            }
            else if (userCommandName == 'P') {
                SquarePrint squarePrint = new SquarePrint(squares);
                SquareInvoker squarePrintInvoker = new SquareInvoker(squarePrint);
                squarePrintInvoker.execute();

                squarePrintInvoker.printUndoCommands();
                squarePrintInvoker.printRedoCommands();
            }
        }
    }

    public static void storeSquare(Square square) {
        squares.add(square);
    }

    public static void unstoreSquare(Square square) {
        squares.remove(square);
    }

    public static int findSquareById(int squareId) {
        int index = 0;

        for (Square square : squares) {
            if (square.getId() == squareId) {
                return index;
            }
            index++;
        }
        return -1;
    }
}

// 2) Receiver
class Square {
    int id;
    int edgeLength;
    int xPosition = 0;
    int yPosition = 0;

    Square(int id, int edgeLength) {
        this.id = id;
        this.edgeLength = edgeLength;
    }

    public Integer getId() {
        return this.id;
    }

    public Integer getEdgeLength() {
        return this.edgeLength;
    }

    public Integer getXPosition() {
        return this.xPosition;
    }

    public Integer getYPosition() {
        return this.yPosition;
    }

    public void setEdgeLength(int edgeLength) {
        this.edgeLength = edgeLength;
    }

    public void setXPosition(int xPosition) {
        this.xPosition = xPosition;
    }

    public void setYPosition(int yPosition) {
        this.yPosition = yPosition;
    }
}

interface Command {
    void execute();
    void undo();
    void redo();
}

class SquareCreate implements Command{

    private final int id;
    private final int edgeLength;

    private Square square = null;

    SquareCreate(int id, int edgeLength) {
        this.id = id;
        this.edgeLength = edgeLength;
    }

    @Override
    public void execute() {
        square = new Square(this.id, this.edgeLength);
//        System.out.println("Created " + square);
        Solution.storeSquare(square);
    }

    @Override
    public void undo() {
        Solution.unstoreSquare(square);
//        System.out.println("Destroyed square" + square);
        square = null;
    }

    @Override
    public void redo() {
        this.execute();
    }
}

class SquareMove implements Command {

    private Square square;
    private final int pixelsRight;
    private final int pixelsUp;

    SquareMove(Square square, int pixelsRight, int pixelsUp) {
        this.square = square;
        this.pixelsRight = pixelsRight;
        this.pixelsUp = pixelsUp;
    }

    @Override
    public void execute() {
        square.setXPosition(square.getXPosition() + this.pixelsRight);
        square.setYPosition(square.getYPosition() + this.pixelsUp);
    }

    @Override
    public void undo() {
        square.setXPosition(square.getXPosition() - this.pixelsRight);
        square.setYPosition(square.getYPosition() - this.pixelsUp);
    }

    @Override
    public void redo() {
        this.execute();
    }
}

class SquareScale implements Command {

    Square square;
    int scaleMultiplier;

    SquareScale(Square square, int scaleMultiplier) {
        this.square = square;
        this.scaleMultiplier = scaleMultiplier;
    }

    @Override
    public void execute() {
        square.setEdgeLength(square.getEdgeLength() * this.scaleMultiplier);
    }

    @Override
    public void undo() {
        square.setEdgeLength(square.getEdgeLength() / this.scaleMultiplier);
    }

    @Override
    public void redo() {
        this.execute();
    }
}

class SquarePrint implements Command {

    ArrayList<Square> squares;

    SquarePrint(ArrayList<Square> squares) {
        this.squares = squares;
    }

    public void execute () {
        for (Square square : squares) {
            System.out.println(square.getId() + " " + square.getXPosition() + " " + square.getYPosition() + " " + square.getEdgeLength());
        }
    }

    @Override
    public void undo() {}

    @Override
    public void redo() {}
}

// 4) Invoker
class SquareInvoker {

    Command command;
    static LinkedList<Command> undoStore = new LinkedList<>();
    static LinkedList<Command> redoStore = new LinkedList<>();

    SquareInvoker(Command command) {
        this.command = command;
    }

    public void execute() {
        if ((command instanceof SquareCreate) || (command instanceof SquareMove) || (command instanceof SquareScale)) {
            undoStore.add(command);
            redoStore.clear();
        }
        this.command.execute();
    }

    public void undo() {
        if ((command instanceof SquareCreate) || (command instanceof SquareMove) || (command instanceof SquareScale)) {
            undoStore.remove(command);
            redoStore.add(command);
            this.command.undo();
        }
    }

    public void redo() {
        if ((command instanceof SquareCreate) || (command instanceof SquareMove) || (command instanceof SquareScale)) {
            undoStore.add(command);
            redoStore.remove(command);
            this.command.redo();
        }
    }

    public void printUndoCommands() {
        System.out.println(undoStore);
    }
    public void printRedoCommands() { System.out.println(redoStore); }
}

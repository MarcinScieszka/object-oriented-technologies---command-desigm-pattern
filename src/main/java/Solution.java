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

public class Solution {
    public static void main(String[] args) {
        SquareInvoker invoker = new SquareInvoker();

        SquareCreate squareCreate = new SquareCreate(1, 2);
        invoker.storeAndExecute(squareCreate);

//        SquareMove squareMove = new SquareMove();
//        SquarePrint squarePrint = new SquarePrint();
//        squarePrint.execute();
    }
}

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

class SquareCreate implements Command {

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
        System.out.println("Created " + square);
    }

    @Override
    public void undo() {
        System.out.println("Destroyed square" + square);
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

class SquarePrint implements Command {

    Square square;

    SquarePrint(Square square) {
        this.square = square;
    }

    public void execute () {
        System.out.println(square.getId() + " " + square.getXPosition() + " " + square.getYPosition() + " " + square.getEdgeLength());
    }

    @Override
    public void undo() {}

    @Override
    public void redo() {}
}

class SquareInvoker {
    public void storeAndExecute(Command command) {
        ArrayList<Command> usedCommands = new ArrayList<>();
        usedCommands.add(command);
        command.execute();

        System.out.println(usedCommands); //TODO: temp
    }
}
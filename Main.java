/* This project solves 8-Puzzle problem using A* algorithm with manhattan heuristic.
*
* How to use: Enter the puzzle arrangement with starting from top-left in the file 8_puzzle.txt
* in the project folder and run the project. See the solution in standard output.
*
* Programmer: Hamidreza Navaei.
*/

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String... args) throws IOException {
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("8_puzzle.txt"))));
        String[] initialStatePiecesNums = inputReader.readLine().split(" ");

        Set<State> reached = new HashSet<>();
        Queue<State> frontier = new PriorityQueue<>((o1, o2) -> {
            if(o1.getF() < o2.getF()) {
                return -1;
            }
            else if(o1.getF() > o2.getF()) {
                return 1;
            }
            return 0;
        });

        int iPiecesNums = 0;
        int[][] initialStateMatrix = new int[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                initialStateMatrix[i][j] = Integer.parseInt(initialStatePiecesNums[iPiecesNums++]);
            }
        }
        frontier.offer(new State(initialStateMatrix, 0, null, ' '));

        for(int i=0;;i++){
            State currentState = frontier.poll();

            if(currentState.goalTest()){
                System.out.printf("Total %d nodes expanded.%n%n",i);
                System.out.println("Solution Action Series:");
                System.out.println(" " + currentState.getActionSeries() + '\n');
                System.out.println("Solution Path:");
                List<String> path = currentState.getPath();
                for (int j = 0; j < path.size(); j++) {
                    System.out.printf("%2d. %s%n", j, path.get(j));
                }
                return;
            }

            reached.add(currentState);
            currentState.expand(frontier, reached);
        }
    }
}

class State {
    int[][] matrix;
    int g, h, f;
    State parent;
    char action;
    boolean isGoal;

    public State(int[][] matrix, int g, State parent, char action){
        this.matrix = new int[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.matrix[i][j] = matrix[i][j];
            }
        }

        this.g = g;
        this.h = 0;
        this.parent = parent;
        this.action = action;
        isGoal = false;

        calculateHeuristic(matrix);
    }

    public void calculateHeuristic(int[][] matrix){
        Coordinate[] coordinates = new Coordinate[9];
        int cnt = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                coordinates[cnt++] = new Coordinate(i, j);
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int num = matrix[i][j];
                if(num == 0)
                    continue;
                int t = Math.abs(i - coordinates[num].getI()) + Math.abs(j - coordinates[num].getJ());
                h += t;
            }
        }

        f = h + g;
    }

    public void expand(Queue<State> frontier, Set<State> reached){
        int[][] moveUpPuzzle = new int[3][3], moveRightPuzzle = new int[3][3], moveDownPuzzle = new int[3][3], moveLeftPuzzle = new int[3][3];
        int zeroI = -1, zeroJ = -1;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if(matrix[i][j] == 0){
                    zeroI = i;
                    zeroJ = j;
                }

                moveUpPuzzle[i][j] = matrix[i][j];
                moveRightPuzzle[i][j] = matrix[i][j];
                moveDownPuzzle[i][j] = matrix[i][j];
                moveLeftPuzzle[i][j] = matrix[i][j];
            }
        }

        if(zeroI != 0){
            moveUpPuzzle[zeroI][zeroJ] = moveUpPuzzle[zeroI - 1][zeroJ];
            moveUpPuzzle[zeroI - 1][zeroJ] = 0;
            State successor = new State(moveUpPuzzle, g + 1, this, 'U');
            if(!reached.contains(successor)){
                frontier.offer(successor);
            }
        }
        if(zeroJ != 2){
            moveRightPuzzle[zeroI][zeroJ] = moveRightPuzzle[zeroI][zeroJ + 1];
            moveRightPuzzle[zeroI][zeroJ + 1] = 0;
            State successor = new State(moveRightPuzzle, g + 1, this, 'R');
            if(!reached.contains(successor)){
                frontier.offer(successor);
            }
        }
        if(zeroI != 2){
            moveDownPuzzle[zeroI][zeroJ] = moveDownPuzzle[zeroI + 1][zeroJ];
            moveDownPuzzle[zeroI + 1][zeroJ] = 0;
            State successor = new State(moveDownPuzzle, g + 1, this, 'D');
            if(!reached.contains(successor)){
                frontier.offer(successor);
            }
        }
        if(zeroJ != 0){
            moveLeftPuzzle[zeroI][zeroJ] = moveLeftPuzzle[zeroI][zeroJ - 1];
            moveLeftPuzzle[zeroI][zeroJ - 1] = 0;
            State successor = new State(moveLeftPuzzle, g + 1, this, 'L');
            if(!reached.contains(successor)){
                frontier.offer(successor);
            }
        }
    }

    public boolean goalTest(){
        int cnt = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if(matrix[i][j] != cnt++)
                    return false;
            }
        }
        isGoal = true;
        return true;
    }

    public float getF() {
        return f;
    }

    public int[][] getMatrix() {
        return matrix;
    }

    public List<String> getPath(){
        List<String> path;
        if(parent == null){
            path = new ArrayList<>();
            path.add(toString());
            return path;
        }
        path = parent.getPath();
        path.add(toString());
        return path;
    }

    public String getActionSeries(){
        if(parent == null)
            return "";
        return parent.getActionSeries() + action + (isGoal ? "":", ");
    }

    @Override
    public boolean equals(Object obj) {
        State other = (State) obj;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if(matrix[i][j] != other.getMatrix()[i][j])
                    return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < j; j++) {
                hashCode *= 10;
                hashCode += matrix[i][j];
            }
        }
        return hashCode;
    }

    @Override
    public String toString(){
        String puzzleInLine = "";
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                puzzleInLine += matrix[i][j] + " ";
            }
        }
        return puzzleInLine;
    }
}

class Coordinate{
    int i,j;

    public Coordinate(int i, int j){
        this.i = i;
        this.j = j;
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }
}
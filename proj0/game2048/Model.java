package game2048;

import java.util.Formatter;
import java.util.Observable;


/** The state of a game of 2048.
 *  @author Altoswuel
 */
public class Model extends Observable {
    /** Current contents of the board. */
    private Board board;
    /** Current score. */
    private int score;
    /** Maximum score so far.  Updated when game ends. */
    private int maxScore;
    /** True iff game is ended. */
    private boolean gameOver;

    /* Coordinate System: column C, row R of the board (where row 0,
     * column 0 is the lower-left corner of the board) will correspond
     * to board.tile(c, r).  Be careful! It works like (x, y) coordinates.
     */

    /** Largest piece value. */
    public static final int MAX_PIECE = 2048;

    /** A new 2048 game on a board of size SIZE with no pieces
     *  and score 0. */
    public Model(int size) {
        board = new Board(size);
        score = maxScore = 0;
        gameOver = false;
    }

    /** A new 2048 game where RAWVALUES contain the values of the tiles
     * (0 if null). VALUES is indexed by (row, col) with (0, 0) corresponding
     * to the bottom-left corner. Used for testing purposes. */
    public Model(int[][] rawValues, int score, int maxScore, boolean gameOver) {
        int size = rawValues.length;
        board = new Board(rawValues, score);
        this.score = score;
        this.maxScore = maxScore;
        this.gameOver = gameOver;
    }

    /** Return the current Tile at (COL, ROW), where 0 <= ROW < size(),
     *  0 <= COL < size(). Returns null if there is no tile there.
     *  Used for testing. Should be deprecated and removed.
     *  */
    public Tile tile(int col, int row) {
        return board.tile(col, row);
    }

    /** Return the number of squares on one side of the board.
     *  Used for testing. Should be deprecated and removed. */
    public int size() {
        return board.size();
    }

    /** Return true iff the game is over (there are no moves, or
     *  there is a tile with value 2048 on the board). */
    public boolean gameOver() {
        checkGameOver();
        if (gameOver) {
            maxScore = Math.max(score, maxScore);
        }
        return gameOver;
    }

    /** Return the current score. */
    public int score() {
        return score;
    }

    /** Return the current maximum game score (updated at end of game). */
    public int maxScore() {
        return maxScore;
    }

    /** Clear the board to empty and reset the score. */
    public void clear() {
        score = 0;
        gameOver = false;
        board.clear();
        setChanged();
    }

    /** Add TILE to the board. There must be no Tile currently at the
     *  same position. */
    public void addTile(Tile tile) {
        board.addTile(tile);
        checkGameOver();
        setChanged();
    }

    /** Tilt the board toward SIDE. Return true iff this changes the board.
     *
     * 1. If two Tile objects are adjacent in the direction of motion and have
     *    the same value, they are merged into one Tile of twice the original
     *    value and that new value is added to the score instance variable
     * 2. A tile that is the result of a merge will not merge again on that
     *    tilt. So each move, every tile will only ever be part of at most one
     *    merge (perhaps zero).
     * 3. When three adjacent tiles in the direction of motion have the same
     *    value, then the leading two tiles in the direction of motion merge,
     *    and the trailing tile does not.
     * */


    public boolean tilt(Side side) {
        boolean changed;
        changed = false;
        Board b = this.board;


        board.setViewingPerspective(side);
        for (int col=0; col < b.size(); col++) {
            // 跟踪变量，他会记录当前一列，最高能放在哪里
            int top = b.size()-1;
            for (int row=board.size()-2;row >=0;row--) {
                // 要保证移动的砖块不为空
                Tile moveboard = b.tile(col, row);
                // 如果我要进行值的判断，首先应该确认，该点不为null
                if (moveboard != null) {
                    // 一个砖块和top的如果值相同融合，然后原top位置不能再被访问
                    if (b.tile(col, top) != null) {
                        if (moveboard.value() == b.tile(col, top).value()) {
                            this.score += 2*b.tile(col, top).value();
                            b.move(col, top, moveboard);
                            // top的值如果小于0，就会超出限制？因为向下检索数量和场景砖块数量是一一对应的
                            top = top - 1;
                            changed = true;
                        }
                        else {
                            top -= 1;
                            b.move(col, top, moveboard);
                            changed = true;
                        }
                    }
                    else {
                        b.move(col, top, moveboard);
                        changed = true;
                    }
                }
            }
        }
        board.setViewingPerspective(Side.NORTH);
    // for the tilt to the Side SIDE. If the board changed, set the
        // changed local variable to true.

        checkGameOver();
        if (changed) {
            setChanged();
        }
        return changed;
    }

    /** Checks if the game is over and sets the gameOver variable
     *  appropriately.
     */
    private void checkGameOver() {
        gameOver = checkGameOver(board);
    }

    /** Determine whether game is over. */
    private static boolean checkGameOver(Board b) {
        return maxTileExists(b) || !atLeastOneMoveExists(b);
    }

    /** Returns true if at least one space on the Board is empty.
     *  Empty spaces are stored as null.
     * */
    /* 处理一个大型的项目,最重要的是做好抽象的屏障
     * 对于所有人来说(包括自己),并不一定要了解所有的细节
     * 但是只要抽象的屏障足够封装底层，就可以直接越过底层,在更高的层次上去编写功能                      */
    public static boolean emptySpaceExists(Board b) {
        int boardsize = b.size();
        for (int row = 0; row < boardsize; row++) {
            for (int col = 0; col < boardsize; col++) {
                    if (b.tile(col, row) == null) {
                        return true;
                    }
                }
            }
        return false;
    }

    /**
     * Returns true if any tile is equal to the maximum valid value.
     * Maximum valid value is given by MAX_PIECE. Note that
     * given a Tile object t, we get its value with t.value().
     */
    /* 对于一个软件来说,测试是非常重要的
        * 单元测试可以从基本的层面排查问题(查看测试报错,并且基于测试进行修改)
        * 断点测试,即debugging(你可以逐行的查看运行效果定位问题)
     */
    public static boolean maxTileExists(Board b) {
        int boardsize = b.size();
        for (int row = 0; row < boardsize; row++) {
            for (int col = 0; col < boardsize; col++) {
                // 两个if不能写成一行,因为两个if都会判断导致报错结束运行
                if (b.tile(col, row) != null) {
                    if (b.tile(col, row).value() == Model.MAX_PIECE) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns true if there are any valid moves on the board.
     * There are two ways that there can be valid moves:
     * 1. There is at least one empty space on the board.
     * 2. There are two adjacent tiles with the same value.
     */
    // 首先问题最好要分析清楚,要做好标注,做好抽象的屏障
    // *不要过早抽象*！！！
    public static boolean atLeastOneMoveExists(Board b) {
        int boardsize = b.size();
        if (Model.emptySpaceExists(b)) {
            return true;
        }
        else {
            // col,row最大输入是boardsize-1,场景最大最大索引boardsize-1
            // 先看测试,看测试是什么问题,可以通过debugging测具体的错位置
            for (int row = 0; row < boardsize; row++) {
                for (int col = 0; col < boardsize; col++) {
                        // 判断是否场景位于最下方,但不含(boardsize-1, boardsize-1)
                        if (col == boardsize - 1 & row != boardsize-1) {
                            if (b.tile(col, row).value() == b.tile(col, row + 1).value()) {
                                return true;
                            }
                        }
                        // 判断是否位于场景最左,但不含(boardsize-1, boardsize-1)
                        if (row == boardsize - 1 & col != boardsize-1) {
                            if (b.tile(col, row).value() == b.tile(col + 1, row).value()) {
                                return true;
                            }
                        }
                        // 对于内层的情况
                        else {
                            if (row != boardsize-1 & col != boardsize-1) {
                                if (b.tile(col, row).value() == b.tile(col+1, row).value() ||
                                    b.tile(col, row).value() == b.tile(col, row+1).value()) {
                                    return true;
                                }
                            }
                        }
                }
            }
        }
        return false;
    }

    @Override
     /** Returns the model as a string, used for debugging. */
    public String toString() {
        Formatter out = new Formatter();
        out.format("%n[%n");
        for (int row = size() - 1; row >= 0; row -= 1) {
            for (int col = 0; col < size(); col += 1) {
                if (tile(col, row) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(col, row).value());
                }
            }
            out.format("|%n");
        }
        String over = gameOver() ? "over" : "not over";
        out.format("] %d (max: %d) (game is %s) %n", score(), maxScore(), over);
        return out.toString();
    }

    @Override
    /** Returns whether two models are equal. */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (getClass() != o.getClass()) {
            return false;
        } else {
            return toString().equals(o.toString());
        }
    }

    @Override
    /** Returns hash code of Model’s string. */
    public int hashCode() {
        return toString().hashCode();
    }
}

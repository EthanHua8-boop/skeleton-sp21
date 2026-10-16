package game2048;

/** Represents the image of a numbered tile on a 2048 board.
 *  @author P. N. Hilfinger.
 */
public class Tile {

    /** A new tile with VALUE as its value at (ROW, COL).  This
     *  constructor is private, so all tiles are created by the
     *  factory methods create, move, and merge. */
    private Tile(int value, int col, int row) {
        // 一个砖块所附带的值(比如2,4,8,16...)
        this.value = value;
        this.row = row;
        this.col = col;
        this.next = null;
    }

    /** Return my current row. */
    public int row() {
        return row;
    }

    /** Return my current column. */
    public int col() {
        return col;
    }

    /** Return the value supplied to my constructor. */
    public int value() {
        return value;
    }

    /** Return my next state.  Before I am moved or merged, I am my
     *  own successor. */
    // 如果next == null,return this,否则 return next
    public Tile next() {
        return next == null ? this : next;
    }

    /** Return a new tile at (ROW, COL) with value VALUE. */
    // 为什么return的tile要用new
    public static Tile create(int value, int col, int row) {
        return new Tile(value, col, row);
    }

    /** Return the result of moving me to (COL, ROW). */
    public Tile move(int col, int row) {
        Tile result = new Tile(value, col, row);
        next = result;
        return result;
    }

    /** Return the result of merging OTHERTILE with me after moving to
     *  (COL, ROW). */
    public Tile merge(int col, int row, Tile otherTile) {
        // 为甚么要有这段砖块值存在声明？难道生成的砖块基础值不能绑定是2吗
        assert value == otherTile.value();
        // 假设已经两个值相同的砖块在某一次相邻，然后我们可以知道移动方向的边界?(可能是场地或者另一个砖块)，然后对一个砖块执行方法
        // 这个方法会首先吧otherTile的next属性，重新赋值为为要到达的最终位置(由函数输入)的一个砖块(吧值加倍，位置为最终位置)
        // 然后把这个位置绑定到当前对象的next
        // 但是如果这么理解的话可能意味着，有两个砖块会在执行时移到同一个位置，然后进行一个操作...
        next = otherTile.next = new Tile(2 * value, col, row);
        return next;
    }

    /** Return the distance in rows or columns between me and my successor
     *  tile (0 if I have no successor). */
    public int distToNext() {
        if (next == null) {
            return 0;
        } else {
            return Math.max(Math.abs(row - next.row()),
                            Math.abs(col - next.col()));
        }
    }

    @Override
    public String toString() {
        return String.format("%d@(%d, %d)", value(), col(), row());
    }

    /** My value. */
    private final int value;

    /** My last position on the board. */
    private final int row, col;

    /** Successor tile: one I am moved to or merged with. */
    private Tile next;
}

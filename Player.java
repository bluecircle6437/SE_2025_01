import java.util.ArrayList;
import java.util.List;


//Player는 name, Piece는 Num로 구별
public class Player {
    private int name;
    private int order; // 턴 순서
    private List<Piece> pieces;
    private int arrivedCount;

    public Player(int name) {
        this.name = name;
        this.order = order;
        this.pieces = new ArrayList<>();
        this.arrivedCount = 0;
    }

    public void addPiece() {
        int pieceNum = pieces.size();
        pieces.add(new Piece(pieceNum));
    }

    // Getter & Setter
    public int getname() { return name; }
    public int getOrder() { return order; }
    public void setOrder(int order) { this.order = order; }
    public List<Piece> getPieces() { return pieces; }
    public int getArrivedCount() { return arrivedCount; }
    public void setArrivedCount(int count) { this.arrivedCount = count; }

    public boolean isGoal() {
        return arrivedCount == pieces.size();
    }
}

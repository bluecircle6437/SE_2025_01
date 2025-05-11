package main;

import java.util.ArrayList;
import java.util.List;


//Player는 name, Piece는 Num로 구별
public class Player {
    private int name;
    private int order; // 턴 순서
    private int arrivedCount;
    List<Piece> pieces;

    public Player(int name, int order) {
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
    public int getName() { return name; }
    public int getOrder() { return order; }
    public void setOrder(int order) { this.order = order; }
    public List<Piece> getPieces() { return pieces; }
    public int getArrivedCount() { return arrivedCount; }
    public void setArrivedCount(int count) { this.arrivedCount = count; }

    public boolean isGoal() {
        return arrivedCount == pieces.size();
    }

    //업기/잡기 기능 구현 위해 추가로 구현했습니다
    //특정 칸에 있는 해당 플레이어의 말 리스트 리턴(없을 경우 null)
    public List<Piece> getPiecesList(int location) {
        int count = 0;
        List<Piece> piecesList = new ArrayList<>();
        for (int i = 0; i < pieces.size(); i++) {
            Piece p = pieces.get(i);
            if (p.getLocation() == location) {
                piecesList.add(p);
                count++;
            }
        }
        if (count == 0) return null;
        else return piecesList;
    }
}
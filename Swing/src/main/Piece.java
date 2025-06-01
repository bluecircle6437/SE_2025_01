package main;

import java.awt.Color;
import java.awt.Point;

public class Piece {
    public int pieceNum;      // 식별 번호 (외부에서 지정)
    private int location;     // 위치
    protected Player pPlayer; // 소속 플레이어
    private boolean isGoal;   // 골인 여부
    private Color color;      // 말 색깔
    private Point position;   // 보드상 좌표 위치

    public Piece(int pieceNum) {
        this.pieceNum = pieceNum;
        location = -1; // 배치되지 않은 상태
        isGoal = false;
        position = null;
    }

    // 말 배치
    public void isStarted() {
        location = 0;
        System.out.println("말 " + (pieceNum + 1) + "이 시작 위치에 배치되었습니다.");
    }

    // 말 이동
    public void move(int target) {
        location = target;
        System.out.println("말 " + (pieceNum + 1) + "이 " + target + "칸으로 이동했습니다.");
    }

    // 말 잡힘
    public void isCatched() {
        location = -1;
        System.out.println("말 " + (pieceNum + 1) + "이 잡혔습니다!");
    }

    // 말 완주
    public void finished() {
        location = -1;
        isGoal = true;
        System.out.println("말 " + (pieceNum + 1) + "이 골인했습니다!");
    }

    // 윷 던지기 (0 ~ 4)
    //Game.throwYut으로 대체됨
    public int throwYut() {
        int flat = 0;
        for (int i = 0; i < 4; i++) {
            boolean isFlat = Math.random() < 0.5;
            if (!isFlat) {
                flat++;
            }
        }
        return flat;
    }

    // 윷 결과로 이동
    //MapSpace.getDestination으로 대체됨
    public void moveByThrow() {
        int result = throwYut();

        int steps;
        String name;

        switch (result) {
            case 0:
                steps = 5;
                name = "모 (Mo)";
                break;
            case 1:
                steps = 1;
                name = "도 (Do)";
                break;
            case 2:
                steps = 2;
                name = "개 (Gae)";
                break;
            case 3:
                steps = 3;
                name = "걸 (Geol)";
                break;
            case 4:
                steps = 4;
                name = "윷 (Yut)";
                break;
            default:
                steps = 0;
                name = "오류";
        }

        if (location == -1) {
            System.out.println("말 " + (pieceNum + 1) + "은(는) 아직 배치되지 않았습니다.");
            return;
        }

        location += steps;
        System.out.println("말 " + (pieceNum + 1) + "이 " + name + "로 " + steps + "칸 이동했습니다. 현재 위치: " + location);

        if (location >= 20) {
            finished();
        }

        if (result == 0 || result == 4) {
            System.out.println(name + "이 나와서 말 " + (pieceNum + 1) + "이 한번 더 던집니다!");
            moveByThrow();
        }
    }

    // 변수 정보 반환
    public int getPieceNum() {
        return pieceNum;
    }

    public int getLocation() {
        return location;
    }

    public boolean getIsGoal() {
        return isGoal;
    }
    
    public void setPosition(Point p) { this.position = p; }

    public Point getPosition() { return position; }

    public void setColor(Color color) { this.color = color; }

    public Color getColor() { return color; }
}

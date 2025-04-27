public class Piece {
    public static int maxNum; //최대 식별 번호
    private int pieceNum; //식별 번호
    private int location; //위치
    protected Player pPlayer; //소속 플레이어
    private boolean isGoal; //골인 여부

    public Piece(Player p) {
        pPlayer = p;
        maxNum++;
        pieceNum = maxNum;
        location = -1; //배치되지 않은 상태
        isGoal = false;
    }

    //이하 게임 클래스에서 실행할 메소드들
    //말 배치
    public void isStarted() {
        location = 0;
    }

    //말 이동
    public void move(int target) {
        location = target;
    }

    //말 잡힘
    public void isCatched() {
        location = -1;
    }

    //말 완주
    public void finished() {
        location = -1;
        isGoal = true;
    }

    //이하 변수 정보 가져오는 메소드
    public int getPieceNum() {
        int num = pieceNum;
        return num;
    }

    public int getLocation() {
        int num = location;
        return num;
    }

    public boolean getIsGoal() {
        boolean bool = isGoal;
        return bool;
    }
}

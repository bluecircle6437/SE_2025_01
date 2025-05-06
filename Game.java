import java.util.ArrayList;
import java.util.List;

public class Game {
    //현재 이 클래스의 대부분의 함수는 선언만 되어 있습니다. 프로젝트 완료 후 이 주석을 지워주세요

    //게임 전 설정
    MapSpace map;//맵
    List<Player> players = new ArrayList<Player>();//플레이어 리스트
    List<Player> finishPlayers = new ArrayList<Player>();//순위 리스트(players와 동일 사이즈, 시작할 땐 비어 있음)

    //말 수 설정(2~5개)
    void setMaxPieceCount(int count) {

    }

    //플레이어 수 설정(2~4인)
    void setPlayerNumber(int number) {
        
if (number < 2 || number > 4) {
            throw new IllegalArgumentException("플레이어 수는 2~4명 사이여야 합니다.");
        }

        players.clear();

        for (int i = 0; i < number; i++) {
            Player player = new Player(i, i);  // id와 순서를 같게
            for (int j = 0; j < 4; j++) {
                player.addPiece();
            }
            players.add(player);
        }

        currentPlayerIndex = 0;
    }

    //판 선택(사각형, 오각형, 육각형)
    void setMapSpace(int type) {
        map = new MapSpace(type);
    }

    //게임 중

    //윷 던지기
    int throwYutRandom() {
        //1~100 랜덤값을 구해 각 구간별로 계산
        double randomValue = (Math.random() * 100);
        randomValue = Math.round(randomValue * 100) / 100.0;
        
        if (randomValue < 6.25) return -1; //빽도(6.25%)
        else if (randomValue >= 6.25 && randomValue < 25.0) return 1; //도(18.75%)
        else if (randomValue >= 25.0 && randomValue < 62.5) return 2; //개(37.5%)
        else if (randomValue >= 62.5 && randomValue < 87.5) return 3; //걸(25%)
        else if (randomValue >= 87.5 && randomValue < 93.75) return 4; //윷(6.25%)
        else return 5; //모(6.25%)
    }

    //윶 지정(테스트용)
    int throwYutSelect(int input) {
        return input;
    }

    //플레이어 행동권 지정
    void setPlayerTurn(Player p) {
        //말 선택 및 이동 등 구현
    }

    //말 잡기 구현
    void checkCatched() {
        //각 플레이어 턴 종료 시 마다 체크
        //겹치는 말이 있으면 현재 턴 진행중인 플레이어의 말을 남기고 추가 기회 부여
    }

    //승리 조건 체크
    void checkFinished() {
        //각 플레이어 턴 종료 시 마다 체크
        //모든 말이 골인 시 플레이어 완료 처리 후 순위 갱신
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).isGoal()) {
                finishPlayers.add(players.get(i)); //이 리스트에 들어온 순서대로 순위 결정
                //완료된 플레이어는 players에서 빠지는데, 이후 구현에 문제 있으면 아래 부분을 주석 처리해주세요
                players.remove(i);
            }
        }
    }
}

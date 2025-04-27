public class Game {
    //현재 이 클래스의 대부분의 함수는 선언만 되어 있습니다. 프로젝트 완료 후 이 주석을 지워주세요

    //게임 전 설정

    //말 수 설정(2~5개)
    void setMaxPieceCount(int count) {

    }

    //플레이어 수 설정(2~4인)
    void setPlayerNumber(int number) {

    }

    //판 선택(사각형, 오각형, 육각형)
    void setMapSpace() {
        //MapSpace 담당자분이 매개변수를 정해주세요
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
    }
}
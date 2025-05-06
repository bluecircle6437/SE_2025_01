import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Game {
    //현재 이 클래스의 대부분의 함수는 선언만 되어 있습니다. 프로젝트 완료 후 이 주석을 지워주세요

    //게임 전 설정
    MapSpace map;//맵
    List<Player> players = new ArrayList<Player>();//플레이어 리스트
    List<Player> finishPlayers = new ArrayList<Player>();//순위 리스트(players와 동일 사이즈, 시작할 땐 비어 있음)
    int maxPieceCount;
    int currentPlayerIndex;

    //말 수 설정(2~5개)
    void setMaxPieceCount(int count) {
        maxPieceCount = count;
    }

    //플레이어 수 설정(2~4인)
    void setPlayerNumber(int number) {
        
        if (number < 2 || number > 4) {
            throw new IllegalArgumentException("플레이어 수는 2~4명 사이여야 합니다.");
        }

        players.clear();

        for (int i = 0; i < number; i++) {
            Player player = new Player(i, i);  // id와 순서를 같게
            for (int j = 0; j < maxPieceCount; j++) { //말 수 설정만큼 말 넣기
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
    //게임 시작 시 이 함수 호출하시면 루프가 돌아갑니다
    void makeTurn() {
        while (!players.isEmpty()) {
            setPlayerTurn(players.get(currentPlayerIndex), throwYut());
            checkFinished();
        }
        //끝나면 finishPlayers에 전체 순위가 남습니다
    }

    //윷 던지기
    List<Integer> throwYut() {
        List<Integer> result = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        int num = 0;
        while(num == 0 || num >= 4) {
            if (num >= 4) {
                String name = num == 4 ? "윷" : "모";
                System.out.println(name + "이 나와서 플레이어 " + currentPlayerIndex + "번이 한번 더 던집니다!");
            }
            int next = scanner.nextInt();
            if (next == 0) {
                int select = scanner.nextInt();
                num = throwYutSelect(select);
            }
            else num = throwYutRandom();
            result.add(num);
        }
        scanner.close();
        return result;
    }

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
    void setPlayerTurn(Player player, List<Integer> resultQueue) {
        //대부분이 piece 클래스에 구현되어 있습니다
        //윷 결과 중 선택 함수
        Scanner scanner = new Scanner(System.in);

        while (!resultQueue.isEmpty()) {
            System.out.println("남은 이동 결과: " + resultQueue);

            // 이동시킬 말 선택
            System.out.print("이동할 말의 ID를 선택하세요: ");
            int pieceNum = scanner.nextInt();

            Piece piece = null;
            for (Piece p : player.getPieces()) {
                if (p.getPieceNum() == pieceNum && !p.getIsGoal()) {
                    piece = p;
                    break;
                }
            }

            //말이 골인한 말인지 확인 추가
            if (piece == null || piece.getIsGoal()) {
                System.out.println("유효하지 않은 말입니다.");
                continue;
            }

            // 이동값 선택
            System.out.println("사용할 이동 거리 선택:");
            for (int i = 0; i < resultQueue.size(); i++) {
                System.out.println(i + ": " + resultQueue.get(i) + "칸");
            }

            System.out.print("선택할 번호 입력: ");
            int idx = scanner.nextInt();

            if (idx < 0 || idx >= resultQueue.size()) {
                System.out.println("잘못된 선택입니다.");
                continue;
            }

            int move = resultQueue.remove(idx);
            int newPos;

            // 이동 처리
            if (piece.getLocation() == -1) {//배치 시 특수 처리
                piece.move(move);
                newPos = move;
            }
            else {
                newPos = map.getDestination(piece.getLocation(), move);
                List<Piece> piecesList = player.getPiecesList(piece.getLocation()); //같은 칸에 말이 있는지 확인
                if (piecesList != null) {
                    for (Piece p : piecesList) {//업기 추가 구현
                        if (newPos == -1) {
                            p.move(-1);
                            p.finished();
                            player.setArrivedCount(player.getArrivedCount() + 1);
                            System.out.println("말이 도착했습니다!");
                        } else {
                            p.move(newPos);
                            System.out.println("말이 " + newPos + "칸으로 이동했습니다.");
                        }
                    }
                }
                
            }
            // 이후에 잡기 체크 및 추가 결과 반영
            if (checkCatched(newPos)) {
                System.out.println("말을 잡아 플레이어 " + currentPlayerIndex + "번이 한번 더 던집니다!");
                resultQueue.addAll(throwYut());
            }
        }

        System.out.println("턴 종료");
        scanner.close();
    }

    //말 잡기 구현
    boolean checkCatched(int location) {
        for (int i = 0; i < players.size(); i++) {
            if (i == currentPlayerIndex) continue;
            Player player = players.get(i);
            List<Piece> pList = player.getPiecesList(location);
            if (pList != null) {
                for (Piece p : pList) {
                    p.isCatched();
                }
                return true;
            }
        }
        return false;
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

package main;

import java.util.ArrayList;
import java.util.List;

public class Game {
	// 게임 전 설정
	MapSpace map;// 맵
	List<Player> players = new ArrayList<Player>();// 플레이어 리스트
	List<Player> finishPlayers = new ArrayList<Player>();// 순위 리스트(players와 동일 사이즈, 시작할 땐 비어 있음)
	int maxPieceCount;
	int currentPlayerIndex;
	List<Integer> resultQueue = new ArrayList<>();
	int catchCount = 0;
	String type;

	public void initialize(GameConfig config) {
		setMaxPieceCount(config.pieceCount);
		setPlayerNumber(config.playerCount);
		setMapSpace(config.boardShape);
	}

	// 말 수 설정(2~5개)
	void setMaxPieceCount(int count) {
		maxPieceCount = count;
	}

	// 플레이어 수 설정(2~4인)
	void setPlayerNumber(int number) {

		if (number < 2 || number > 4) {
			throw new IllegalArgumentException("플레이어 수는 2~4명 사이여야 합니다.");
		}

		players.clear();

		for (int i = 0; i < number; i++) {
			Player player = new Player(i, i); // id와 순서를 같게
			for (int j = 0; j < maxPieceCount; j++) { // 말 수 설정만큼 말 넣기
				player.addPiece();
			}
			players.add(player);
		}

		currentPlayerIndex = 0;
	}

	// 판 선택(사각형, 오각형, 육각형)
	void setMapSpace(String type) {
		map = new MapSpace(type);
	}

	void throwYutRandom() {
		// 1~100 랜덤값을 구해 각 구간별로 계산
		double randomValue = (Math.random() * 100);
		randomValue = Math.round(randomValue * 100) / 100.0;
		int result;

		if (randomValue < 6.25)
			result = -1; // 빽도(6.25%)
		else if (randomValue < 25.0)
			result = 1; // 도
		else if (randomValue < 62.5)
			result = 2; // 개
		else if (randomValue < 87.5)
			result = 3; // 걸
		else if (randomValue < 93.75)
			result = 4; // 윷
		else
			result = 5; // 모

		resultQueue.add(result);

		if (result >= 4) {
			String name = result == 4 ? "윷" : "모";
			System.out.println("🎯 " + name + "이 나와서 한 번 더 던질 수 있습니다!");
		} else {
			System.out.println("🎲 윷 던지기 결과: " + result);
		}
	}

	// 윶 지정(테스트용)
	void throwYutSelect(int result) {
		// 무조건 결과를 추가
		resultQueue.add(result);

		// 윷이나 모면 추가 안내 메시지만 출력
		if (result >= 4) {
			String name = result == 4 ? "윷" : "모";
			System.out.println("🎯 " + name + "이 나와서 플레이어 " + (currentPlayerIndex + 1) + "번이 한번 더 던집니다!");
		} else {
			System.out.println("🎲 윷 던지기 결과: " + result);
		}
	}

	// 나간 말이 없는데 빽도면 턴 넘기기
	boolean checkZeroBack() {
		if (resultQueue.indexOf(-1) == -1 || resultQueue.size() > 1)
			return false;

		for (Piece p : players.get(currentPlayerIndex).getPieces()) {
			if (p.getLocation() != -1)
				return false;
		}
		System.out.println("⚠️ 말이 전부 맵 밖에 있습니다. 턴을 넘깁니다.");
		resultQueue.remove(0);
		changePlayer();

		return true; // ✅ 턴을 넘겼음을 알림
	}

	void changePlayer() {
		// 무한루프 방지용으로 루프 수를 제한했습니다. 실제로 다 돌아갈 일은 없습니다
		for (int i = 0; i < players.size(); i++) {
			if (currentPlayerIndex + 1 == players.size())
				currentPlayerIndex = 0;
			else
				currentPlayerIndex++;
			Player player = players.get(currentPlayerIndex);
			if (!player.isGoal())
				break;
		}
	}

	// makeTurn, setPlayerTurn 메소드는 말 이동마다 호출하는 걸 상정해 루프가 제거되었습니다
	// 이동시킬 말과 이동값 인덱스를 넣으면 됩니다
	void makeTurn(int pieceNum, int idx) {
		if (!players.isEmpty()) {
			setPlayerTurn(pieceNum, idx);
			checkFinished();

			// 이동값이 2개인 상황에서 하나 소모하고 빽도 남았을 시 오류 처리
			if (checkZeroBack())
				return;

			// 이동 결과가 더 남아있는 경우 턴 유지
			if (!resultQueue.isEmpty()) {
				System.out.println("🔁 추가 이동 기회가 남아 있습니다.");
				return;
			}

			if (catchCount >= 1) {
				System.out.println("말을 잡아 플레이어 " + (currentPlayerIndex + 1) + "번이 한번 더 던집니다!");
				catchCount--;
			} else {
				System.out.println("턴 종료");
				changePlayer();
			}
		}
	}
	// 게임이 끝나면 finishPlayers에 전체 순위가 남습니다

	// 플레이어 행동권 지정
	void setPlayerTurn(int pieceNum, int idx) {
		// 대부분이 piece 클래스에 구현되어 있습니다
		// 윷 결과 중 선택 함수
		Player player = players.get(currentPlayerIndex);

		System.out.println("남은 이동 결과: " + resultQueue);

		// 이동시킬 말 선택
		// System.out.print("이동할 말의 ID를 선택하세요: ");
		Piece piece = null;
		for (Piece p : player.getPieces()) {
			if (p.getPieceNum() == pieceNum && !p.getIsGoal()) {
				piece = p;
				break;
			}
		}

		// 말이 골인한 말인지 확인 추가
		if (piece == null || piece.getIsGoal()) {
			throw new IllegalArgumentException("유효하지 않은 말입니다."); // 오류 처리가 되어야 할 것 같습니다
			// continue;
		}

		// 이동값 선택
		System.out.println("사용할 이동 거리 선택:");
		for (int i = 0; i < resultQueue.size(); i++) {
			System.out.println(i + ": " + resultQueue.get(i) + "칸");
		}

		// System.out.print("선택할 번호 입력: ");
		if (idx < 0 || idx >= resultQueue.size()) {
			throw new IllegalArgumentException("잘못된 이동 인덱스입니다."); // 오류 처리가 되어야 할 것 같습니다
			// continue;
		}

		int move = resultQueue.remove(idx);
		int newPos;

		// 이동 처리
		if (piece.getLocation() == -1) {// 배치 시 특수 처리
			piece.move(move);
			newPos = move;
		} else {
			newPos = map.getDestination(piece.getLocation(), move);
			List<Piece> piecesList = player.getPiecesList(piece.getLocation()); // 같은 칸에 말이 있는지 확인
			if (piecesList != null) {
				for (Piece p : piecesList) {// 업기 추가 구현
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
		if (newPos != -1) {
			checkCatched(newPos);
		}
	}

	// 말 잡기 구현
	void checkCatched(int location) {
		for (int i = 0; i < players.size(); i++) {
			if (i == currentPlayerIndex)
				continue;
			Player player = players.get(i);
			List<Piece> pList = player.getPiecesList(location);
			if (pList != null) {
				for (Piece p : pList) {
					p.isCatched();
				}
				catchCount++;
			}
		}
	}

	// 🔍 최근 턴에 잡기가 있었는지 확인
	public boolean didCatchThisTurn() {
		return catchCount > 0;
	}

	// 다음 턴을 위해 잡기 여부 초기화
	public void clearCatchCount() {
		catchCount = 0;
	}

	// 승리 확인
	public boolean isFinished() {
		return players.isEmpty();
	}

	// 승리 조건 체크
	void checkFinished() {
		Player currentPlayer = players.get(currentPlayerIndex);
		// 각 플레이어 턴 종료 시 마다 체크
		// 모든 말이 골인 시 플레이어 완료 처리 후 순위 갱신
		if (currentPlayer.isGoal()) {
			finishPlayers.add(currentPlayer); // 이 리스트에 들어온 순서대로 순위 결정
			System.out.println("🎉 플레이어 " + (currentPlayer.getName() + 1) + "번이 모든 말을 골인시켰습니다!");
			// 완료된 플레이어는 players에서 빠지는데, 이후 구현에 문제 있으면 아래 부분을 주석 처리해주세요
			// players.remove(currentPlayerIndex);
			catchCount = 0;
		}
	}

	// UI 구현용 함수
	int getLastResult() {
		return resultQueue.get(resultQueue.size() - 1);
	}

	boolean getIsMoreThrow() {
		if (resultQueue.get(resultQueue.size() - 1) >= 4)
			return true;
		else
			return false;
	}

	// 이거 호출해서 리턴이 0이면 다음 턴으로 진행
	int getResultCount() {
		return resultQueue.size();
	}

	int getCurrentPlayerIndex() {
		return currentPlayerIndex;
	}
}

// JavaFX 버전의 GameUI 전체 변환 (GameUIFX.java)
package main;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.text.*;
import javafx.geometry.*;

import java.util.*;

public class GameUI extends Application {

	private Game game;
	private BorderPane root;
	private VBox pieceDisplayPanel;
	private Canvas boardCanvas;
	private Label turnLabel;
	private ComboBox<String> yutSelector;
	private ComboBox<String> pieceSelector;
	private Button randomThrowButton;
	private Button customThrowButton;
	private Button moveButton;
	private VBox rightPanel;
	private Piece selectedPiece;
	private GameConfig config;
	private int selectedPieceIndex = -1; // 말 선택용
	private int selectedPlayerIndex = -1;
	PieceCircle selectedPieceCircle = null;
	int selectedMoveIndex = -1;
	private List<Integer> movePreview = new ArrayList<>();
	private boolean isBackDo = false; // 빽도 상태 여부 저장

	private final Color[] playerColors = { Color.RED, Color.BLUE, Color.GREEN, Color.GOLD };
	private Map<Piece, PieceCircle> pieceCircleMap = new HashMap<>();
	private Map<Integer, Point2D> mapPoints = new HashMap<>();;
	private Map<Integer, Point2D> squareMapPoints;
	private Map<Integer, Point2D> pentagonMapPoints;
	private Map<Integer, Point2D> hexagonMapPoints;

	@Override
	public void start(Stage primaryStage) {
		config = showGameConfigDialog();

		if (config == null) {
			System.exit(0);
		}

		game = new Game();
		game.initialize(config);

		root = new BorderPane();
		Scene scene = new Scene(root, 1200, 900);
		primaryStage.setTitle("윷놀이 게임");
		primaryStage.setScene(scene);

		setupTopControls();
		setupBoard();
		setupRightPanel();
		setupBottomControls();

		primaryStage.show();
	}

	private void setupTopControls() {
		HBox topControls = new HBox(10);
		topControls.setPadding(new Insets(10));
		topControls.setAlignment(Pos.CENTER_LEFT);

		turnLabel = new Label("플레이어 1번의 턴입니다.");
		turnLabel.setFont(Font.font("맑은 고딕", 16));

		yutSelector = new ComboBox<>();
		yutSelector.getItems().addAll("빽도", "도", "개", "걸", "윷", "모");
		yutSelector.setValue("도");

		randomThrowButton = new Button("랜덤 윷 던지기");
		customThrowButton = new Button("지정 윷 던지기");
		moveButton = new Button("말 이동");

		topControls.getChildren().addAll(turnLabel, new Label("윷 선택:"), yutSelector, randomThrowButton,
				customThrowButton, moveButton);

		randomThrowButton.setOnAction(e -> handleRandomThrow());
		customThrowButton.setOnAction(e -> handleCustomThrow());
		moveButton.setOnAction(e -> handleMove());

		root.setTop(topControls);
	}

	private void setupBoard() {
		boardCanvas = new Canvas(800, 700);
		GraphicsContext gc = boardCanvas.getGraphicsContext2D();

		// 맵 포인트 초기화
		switch (config.boardShape) {
		case "사각형" -> {
			initializeSquareMapPoints();
			mapPoints = squareMapPoints;
		}
		case "오각형" -> {
			initializePentagonMapPoints();
			mapPoints = pentagonMapPoints;
		}
		case "육각형" -> {
			initializeHexagonMapPoints();
			mapPoints = hexagonMapPoints;
		}
		}

		drawBoard(gc);
		boardCanvas.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleBoardClick);

		StackPane boardPane = new StackPane(boardCanvas);
		root.setCenter(boardPane);
	}

	private void initializeSquareMapPoints() {
		squareMapPoints = new HashMap<>();
		int offsetX = 50;
		int offsetY = 50;
		int spacing = 30;

		// 바깥 경로
		squareMapPoints.put(0, new Point2D(offsetX + spacing * 15, offsetY + spacing * 15));
		squareMapPoints.put(1, new Point2D(offsetX + spacing * 15, offsetY + spacing * 12));
		squareMapPoints.put(2, new Point2D(offsetX + spacing * 15, offsetY + spacing * 9));
		squareMapPoints.put(3, new Point2D(offsetX + spacing * 15, offsetY + spacing * 6));
		squareMapPoints.put(4, new Point2D(offsetX + spacing * 15, offsetY + spacing * 3));
		squareMapPoints.put(5, new Point2D(offsetX + spacing * 15, offsetY + spacing * 0));
		squareMapPoints.put(6, new Point2D(offsetX + spacing * 12, offsetY + spacing * 0));
		squareMapPoints.put(7, new Point2D(offsetX + spacing * 9, offsetY + spacing * 0));
		squareMapPoints.put(8, new Point2D(offsetX + spacing * 6, offsetY + spacing * 0));
		squareMapPoints.put(9, new Point2D(offsetX + spacing * 3, offsetY + spacing * 0));
		squareMapPoints.put(10, new Point2D(offsetX + spacing * 0, offsetY + spacing * 0));
		squareMapPoints.put(11, new Point2D(offsetX + spacing * 0, offsetY + spacing * 3));
		squareMapPoints.put(12, new Point2D(offsetX + spacing * 0, offsetY + spacing * 6));
		squareMapPoints.put(13, new Point2D(offsetX + spacing * 0, offsetY + spacing * 9));
		squareMapPoints.put(14, new Point2D(offsetX + spacing * 0, offsetY + spacing * 12));
		squareMapPoints.put(15, new Point2D(offsetX + spacing * 0, offsetY + spacing * 15));
		squareMapPoints.put(16, new Point2D(offsetX + spacing * 3, offsetY + spacing * 15));
		squareMapPoints.put(17, new Point2D(offsetX + spacing * 6, offsetY + spacing * 15));
		squareMapPoints.put(18, new Point2D(offsetX + spacing * 9, offsetY + spacing * 15));
		squareMapPoints.put(19, new Point2D(offsetX + spacing * 12, offsetY + spacing * 15));

		// 중앙점
		squareMapPoints.put(24, new Point2D(offsetX + (int) (7.5 * spacing), offsetY + (int) (7.5 * spacing)));

		// 대각선 경로 (5→15)
		squareMapPoints.put(20, new Point2D(offsetX + (int) (12.5 * spacing), offsetY + (int) (2.5 * spacing)));
		squareMapPoints.put(21, new Point2D(offsetX + spacing * 10, offsetY + spacing * 5));
		squareMapPoints.put(25, new Point2D(offsetX + spacing * 5, offsetY + spacing * 10));
		squareMapPoints.put(26, new Point2D(offsetX + (int) (2.5 * spacing), offsetY + (int) (12.5 * spacing)));

		// 대각선 경로 (10→0)
		squareMapPoints.put(22, new Point2D(offsetX + (int) (2.5 * spacing), offsetY + (int) (2.5 * spacing)));
		squareMapPoints.put(23, new Point2D(offsetX + spacing * 5, offsetY + spacing * 5));
		squareMapPoints.put(27, new Point2D(offsetX + spacing * 10, offsetY + spacing * 10));
		squareMapPoints.put(28, new Point2D(offsetX + (int) (12.5 * spacing), offsetY + (int) (12.5 * spacing)));
	}

	private void initializePentagonMapPoints() {
		pentagonMapPoints = new HashMap<>();
		int offsetX = 0;
		int offsetY = 95;
		int spacing = 30;

		// 바깥 경로
		pentagonMapPoints.put(0, new Point2D(offsetX + spacing * 20, offsetY + spacing * 19));
		pentagonMapPoints.put(1, new Point2D(offsetX + (int) (20.6 * spacing), offsetY + (int) (16.3 * spacing)));
		pentagonMapPoints.put(2, new Point2D(offsetX + (int) (21.2 * spacing), offsetY + (int) (13.6 * spacing)));
		pentagonMapPoints.put(3, new Point2D(offsetX + (int) (21.8 * spacing), offsetY + (int) (10.9 * spacing)));
		pentagonMapPoints.put(4, new Point2D(offsetX + (int) (22.4 * spacing), offsetY + (int) (8.2 * spacing)));
		pentagonMapPoints.put(5, new Point2D(offsetX + spacing * 23, offsetY + (int) (5.5 * spacing)));
		pentagonMapPoints.put(6, new Point2D(offsetX + (int) (20.9 * spacing), offsetY + spacing * 4));
		pentagonMapPoints.put(7, new Point2D(offsetX + (int) (18.8 * spacing), offsetY + (int) (2.5 * spacing)));
		pentagonMapPoints.put(8, new Point2D(offsetX + (int) (16.7 * spacing), offsetY + spacing * 1));
		pentagonMapPoints.put(9, new Point2D(offsetX + (int) (14.6 * spacing), offsetY + (int) (-0.5 * spacing)));
		pentagonMapPoints.put(10, new Point2D(offsetX + (int) (12.5 * spacing), offsetY + spacing * -2));
		pentagonMapPoints.put(11, new Point2D(offsetX + (int) (10.4 * spacing), offsetY + (int) (-0.5 * spacing)));
		pentagonMapPoints.put(12, new Point2D(offsetX + (int) (8.3 * spacing), offsetY + spacing * 1));
		pentagonMapPoints.put(13, new Point2D(offsetX + (int) (6.2 * spacing), offsetY + (int) (2.5 * spacing)));
		pentagonMapPoints.put(14, new Point2D(offsetX + (int) (4.1 * spacing), offsetY + spacing * 4));
		pentagonMapPoints.put(15, new Point2D(offsetX + spacing * 2, offsetY + (int) (5.5 * spacing)));
		pentagonMapPoints.put(16, new Point2D(offsetX + (int) (2.6 * spacing), offsetY + (int) (8.2 * spacing)));
		pentagonMapPoints.put(17, new Point2D(offsetX + (int) (3.2 * spacing), offsetY + (int) (10.9 * spacing)));
		pentagonMapPoints.put(18, new Point2D(offsetX + (int) (3.8 * spacing), offsetY + (int) (13.6 * spacing)));
		pentagonMapPoints.put(19, new Point2D(offsetX + (int) (4.4 * spacing), offsetY + (int) (16.3 * spacing)));
		pentagonMapPoints.put(20, new Point2D(offsetX + spacing * 5, offsetY + spacing * 19));
		pentagonMapPoints.put(21, new Point2D(offsetX + spacing * 8, offsetY + spacing * 19));
		pentagonMapPoints.put(22, new Point2D(offsetX + spacing * 11, offsetY + spacing * 19));
		pentagonMapPoints.put(23, new Point2D(offsetX + spacing * 14, offsetY + spacing * 19));
		pentagonMapPoints.put(24, new Point2D(offsetX + spacing * 17, offsetY + spacing * 19));

		// 중앙점
		pentagonMapPoints.put(31, new Point2D(offsetX + (int) (12.5 * spacing), offsetY + spacing * 10));

		// 대각선 경로 (5→31)
		pentagonMapPoints.put(25, new Point2D(offsetX + (int) (19.5 * spacing), offsetY + spacing * 7));
		pentagonMapPoints.put(26, new Point2D(offsetX + spacing * 16, offsetY + (int) (8.5 * spacing)));

		// 대각선 경로 (10→31)
		pentagonMapPoints.put(27, new Point2D(offsetX + (int) (12.5 * spacing), offsetY + spacing * 2));
		pentagonMapPoints.put(28, new Point2D(offsetX + (int) (12.5 * spacing), offsetY + spacing * 6));

		// 대각선 경로 (15→31)
		pentagonMapPoints.put(29, new Point2D(offsetX + (int) (5.5 * spacing), offsetY + spacing * 7));
		pentagonMapPoints.put(30, new Point2D(offsetX + spacing * 9, offsetY + (int) (8.5 * spacing)));

		// 대각선 경로 (20→31)
		pentagonMapPoints.put(32, new Point2D(offsetX + spacing * 10, offsetY + spacing * 13));
		pentagonMapPoints.put(33, new Point2D(offsetX + (int) (7.5 * spacing), offsetY + spacing * 16));

		// 대각선 경로 (0→31)
		pentagonMapPoints.put(34, new Point2D(offsetX + spacing * 15, offsetY + spacing * 13));
		pentagonMapPoints.put(35, new Point2D(offsetX + (int) (17.5 * spacing), offsetY + spacing * 16));
	}

	private void initializeHexagonMapPoints() {
		hexagonMapPoints = new HashMap<>();
		int offsetX = 250;
		int offsetY = 60;
		int spacing = 20;

		// 바깥 경로
		hexagonMapPoints.put(0, new Point2D(offsetX + spacing * 15, offsetY + spacing * 30));
		hexagonMapPoints.put(1, new Point2D(offsetX + spacing * 17, offsetY + spacing * 27));
		hexagonMapPoints.put(2, new Point2D(offsetX + spacing * 19, offsetY + spacing * 24));
		hexagonMapPoints.put(3, new Point2D(offsetX + spacing * 21, offsetY + spacing * 21));
		hexagonMapPoints.put(4, new Point2D(offsetX + spacing * 23, offsetY + spacing * 18));
		hexagonMapPoints.put(5, new Point2D(offsetX + spacing * 25, offsetY + spacing * 15));
		hexagonMapPoints.put(6, new Point2D(offsetX + spacing * 23, offsetY + spacing * 12));
		hexagonMapPoints.put(7, new Point2D(offsetX + spacing * 21, offsetY + spacing * 9));
		hexagonMapPoints.put(8, new Point2D(offsetX + spacing * 19, offsetY + spacing * 6));
		hexagonMapPoints.put(9, new Point2D(offsetX + spacing * 17, offsetY + spacing * 3));
		hexagonMapPoints.put(10, new Point2D(offsetX + spacing * 15, offsetY + spacing * 0));
		hexagonMapPoints.put(11, new Point2D(offsetX + spacing * 12, offsetY + spacing * 0));
		hexagonMapPoints.put(12, new Point2D(offsetX + spacing * 9, offsetY + spacing * 0));
		hexagonMapPoints.put(13, new Point2D(offsetX + spacing * 6, offsetY + spacing * 0));
		hexagonMapPoints.put(14, new Point2D(offsetX + spacing * 3, offsetY + spacing * 0));
		hexagonMapPoints.put(15, new Point2D(offsetX + spacing * 0, offsetY + spacing * 0));
		hexagonMapPoints.put(16, new Point2D(offsetX + spacing * -2, offsetY + spacing * 3));
		hexagonMapPoints.put(17, new Point2D(offsetX + spacing * -4, offsetY + spacing * 6));
		hexagonMapPoints.put(18, new Point2D(offsetX + spacing * -6, offsetY + spacing * 9));
		hexagonMapPoints.put(19, new Point2D(offsetX + spacing * -8, offsetY + spacing * 12));
		hexagonMapPoints.put(20, new Point2D(offsetX + spacing * -10, offsetY + spacing * 15));
		hexagonMapPoints.put(21, new Point2D(offsetX + spacing * -8, offsetY + spacing * 18));
		hexagonMapPoints.put(22, new Point2D(offsetX + spacing * -6, offsetY + spacing * 21));
		hexagonMapPoints.put(23, new Point2D(offsetX + spacing * -4, offsetY + spacing * 24));
		hexagonMapPoints.put(24, new Point2D(offsetX + spacing * -2, offsetY + spacing * 27));
		hexagonMapPoints.put(25, new Point2D(offsetX + spacing * 0, offsetY + spacing * 30));
		hexagonMapPoints.put(26, new Point2D(offsetX + spacing * 3, offsetY + spacing * 30));
		hexagonMapPoints.put(27, new Point2D(offsetX + spacing * 6, offsetY + spacing * 30));
		hexagonMapPoints.put(28, new Point2D(offsetX + spacing * 9, offsetY + spacing * 30));
		hexagonMapPoints.put(29, new Point2D(offsetX + spacing * 12, offsetY + spacing * 30));

		// 중앙점
		hexagonMapPoints.put(38, new Point2D(offsetX + (int) (7.5 * spacing), offsetY + 15 * spacing));

		// 대각선 경로 (5→20)
		hexagonMapPoints.put(30, new Point2D(offsetX + (int) (19 * spacing), offsetY + 15 * spacing));
		hexagonMapPoints.put(31, new Point2D(offsetX + spacing * 13, offsetY + 15 * spacing));
		hexagonMapPoints.put(37, new Point2D(offsetX + spacing * 1, offsetY + 15 * spacing));
		hexagonMapPoints.put(36, new Point2D(offsetX + (int) (-5 * spacing), offsetY + 15 * spacing));

		// 대각선 경로 (10→25)
		hexagonMapPoints.put(32, new Point2D(offsetX + (int) (12.5 * spacing), offsetY + (int) (5 * spacing)));
		hexagonMapPoints.put(33, new Point2D(offsetX + (int) (10 * spacing), offsetY + spacing * 10));
		hexagonMapPoints.put(39, new Point2D(offsetX + spacing * 5, offsetY + spacing * 20));
		hexagonMapPoints.put(40, new Point2D(offsetX + (int) (2.5 * spacing), offsetY + (int) (25 * spacing)));

		// 대각선 경로 (15→0)
		hexagonMapPoints.put(34, new Point2D(offsetX + (int) (2.5 * spacing), offsetY + (int) (5 * spacing)));
		hexagonMapPoints.put(35, new Point2D(offsetX + spacing * 5, offsetY + spacing * 10));
		hexagonMapPoints.put(41, new Point2D(offsetX + spacing * 10, offsetY + spacing * 20));
		hexagonMapPoints.put(42, new Point2D(offsetX + (int) (12.5 * spacing), offsetY + (int) (25 * spacing)));
	}

	private void drawBoard(GraphicsContext gc) {
		// ✅ 기존 내용 전체 지우기
		gc.clearRect(0, 0, boardCanvas.getWidth(), boardCanvas.getHeight());
		
		gc.setLineWidth(2);
		gc.setStroke(Color.BLACK);
		gc.setFill(Color.WHITE);

		// 칸 그리기
		if (mapPoints == squareMapPoints) {
			// 테두리 선 연결
			int[] outerLine = { 0, 5, 10, 15, 0 }; // 사각형 꼭짓점 연결
			for (int i = 0; i < outerLine.length - 1; i++) {
				Point2D from = mapPoints.get(outerLine[i]);
				Point2D to = mapPoints.get(outerLine[i + 1]);
				if (from != null && to != null) {
					gc.strokeLine(from.getX(), from.getY(), to.getX(), to.getY());
				}
			}
			for (int i = 0; i < outerLine.length - 2; i++) {
				Point2D from = mapPoints.get(outerLine[i]);
				Point2D to = mapPoints.get(outerLine[i + 2]);
				if (from != null && to != null) {
					gc.strokeLine(from.getX(), from.getY(), to.getX(), to.getY());
				}
			}
			for (Map.Entry<Integer, Point2D> entry : mapPoints.entrySet()) {
				Point2D pt = entry.getValue();
				int index = entry.getKey();
				if (index == 24 || index % 5 == 0 && index <= 15) {
					gc.setStroke(Color.WHITE);
					gc.fillOval(pt.getX() - 30, pt.getY() - 30, 60, 60);
					gc.setStroke(Color.BLACK);
					gc.strokeOval(pt.getX() - 30, pt.getY() - 30, 60, 60);
				}
				gc.setStroke(Color.WHITE);
				gc.fillOval(pt.getX() - 25, pt.getY() - 25, 50, 50);
				gc.setStroke(Color.BLACK);
				gc.strokeOval(pt.getX() - 25, pt.getY() - 25, 50, 50);
			}
		}

		// 칸 그리기
		if (mapPoints == pentagonMapPoints) {
			// 테두리 선 연결
			int[] outerLine = { 0, 5, 10, 15, 20, 0 }; // 오각형 꼭짓점 연결
			for (int i = 0; i < outerLine.length - 1; i++) {
				Point2D from = mapPoints.get(outerLine[i]);
				Point2D to = mapPoints.get(outerLine[i + 1]);
				if (from != null && to != null) {
					gc.strokeLine(from.getX(), from.getY(), to.getX(), to.getY());
				}
			}
			for (int i = 0; i <= 4; i++) {
				gc.strokeLine(mapPoints.get(31).getX(), mapPoints.get(31).getY(), mapPoints.get(i * 5).getX(),
						mapPoints.get(i * 5).getY());
			}

			for (Map.Entry<Integer, Point2D> entry : mapPoints.entrySet()) {
				Point2D pt = entry.getValue();
				int index = entry.getKey();
				if (index == 31 || index % 5 == 0 && index <= 20) {
					gc.setStroke(Color.WHITE);
					gc.fillOval(pt.getX() - 30, pt.getY() - 30, 60, 60);
					gc.setStroke(Color.BLACK);
					gc.strokeOval(pt.getX() - 30, pt.getY() - 30, 60, 60);
				}
				gc.setStroke(Color.WHITE);
				gc.fillOval(pt.getX() - 25, pt.getY() - 25, 50, 50);
				gc.setStroke(Color.BLACK);
				gc.strokeOval(pt.getX() - 25, pt.getY() - 25, 50, 50);
			}
		}

		// 칸 그리기
		if (mapPoints == hexagonMapPoints) {
			// 테두리 선 연결
			int[] outerLine = { 0, 5, 10, 15, 20, 25, 0 }; // 육각형 꼭짓점 연결
			for (int i = 0; i < outerLine.length - 1; i++) {
				Point2D from = mapPoints.get(outerLine[i]);
				Point2D to = mapPoints.get(outerLine[i + 1]);
				if (from != null && to != null) {
					gc.strokeLine(from.getX(), from.getY(), to.getX(), to.getY());
				}
			}
			for (int i = 0; i <= 5; i++) {
				gc.strokeLine(mapPoints.get(38).getX(), mapPoints.get(38).getY(), mapPoints.get(i * 5).getX(),
						mapPoints.get(i * 5).getY());
			}

			for (Map.Entry<Integer, Point2D> entry : mapPoints.entrySet()) {
				Point2D pt = entry.getValue();
				int index = entry.getKey();
				if (index == 38 || index % 5 == 0 && index <= 25) {
					gc.setStroke(Color.WHITE);
					gc.fillOval(pt.getX() - 30, pt.getY() - 30, 60, 60);
					gc.setStroke(Color.BLACK);
					gc.strokeOval(pt.getX() - 30, pt.getY() - 30, 60, 60);
				}
				gc.setStroke(Color.WHITE);
				gc.fillOval(pt.getX() - 25, pt.getY() - 25, 50, 50);
				gc.setStroke(Color.BLACK);
				gc.strokeOval(pt.getX() - 25, pt.getY() - 25, 50, 50);
			}
		}

		// 출발 표시
		Point2D start = mapPoints.get(0);
		gc.setFont(new Font("맑은 고딕", 18));
		gc.setFill(Color.BLACK);
		gc.fillText("출발", start.getX() - 19, start.getY() + 7);

		// 말 그리기
		if (game != null && game.players != null) {

			Set<Integer> drawnStackPositions = new HashSet<>();

			for (int i = 0; i < game.players.size(); i++) {
				Player player = game.players.get(i);
				Color color = playerColors[i];

				// 말 위치별 개수 카운팅
				Map<Integer, Integer> countMap = new HashMap<>();
				for (Piece p : player.getPieces()) {
					if (!p.getIsGoal() && p.getLocation() >= 0)
						countMap.put(p.getLocation(), countMap.getOrDefault(p.getLocation(), 0) + 1);
				}

				// 말 그리기
				for (Piece piece : player.getPieces()) {
					int loc = piece.getLocation();
					if (loc >= 0 && mapPoints.containsKey(loc)) {
						Point2D p = mapPoints.get(loc);

						// 말 원
						gc.setFill(color);
						gc.fillOval(p.getX() - 25, p.getY() - 25, 50, 50);
						gc.setStroke(Color.BLACK);
						gc.strokeOval(p.getX() - 25, p.getY() - 25, 50, 50);

						// 업기 개수 표시
						int count = countMap.getOrDefault(loc, 0);
						if (count > 1) {
							gc.setFill(Color.WHITE);
							gc.fillOval(p.getX() + 10, p.getY() - 20, 16, 16);
							gc.setFill(Color.BLACK);
							gc.setFont(new Font("Arial", 12));
							gc.fillText(String.valueOf(count), p.getX() + 14, p.getY() - 8);
							drawnStackPositions.add(loc);
						}
					}
				}

				// ✔ 골인 가능 텍스트 표시
				if (movePreview.contains(-1)) {
					Player currentPlayer = game.players.get(game.getCurrentPlayerIndex());
					if (selectedPieceIndex >= 0 && selectedPieceIndex < currentPlayer.getPieces().size()) {
						Piece piece = currentPlayer.getPieces().get(selectedPieceIndex);
						int loc = piece.getLocation();
						if (loc >= 0 && mapPoints.containsKey(loc)) {
							Point2D pt = mapPoints.get(loc);
							gc.setFill(Color.BLACK);
							gc.setFont(new Font("맑은 고딕", 14));
							gc.fillText("골인", pt.getX() - 20, pt.getY() - 35); // 텍스트 위치 조정
						}
					}
				}
			}
		}

		// 🔸 목적지 미리보기
		gc.setFill(Color.rgb(100, 100, 100, 0.4)); // 반투명 회색
		for (Integer dest : movePreview) {
			if (mapPoints.containsKey(dest)) {
				Point2D pt = mapPoints.get(dest);
				gc.fillOval(pt.getX() - 25, pt.getY() - 25, 50, 50);
				// 미리보기 원
				if (dest == -1) {
					// 골인 위치를 예: 화면 우측 하단 등 고정 좌표에 그리기
					Point2D goalPoint = new Point2D(900, 900); // 원하는 위치로 조정
					gc.fillOval(goalPoint.getX() - 25, goalPoint.getY() - 25, 50, 50);
					gc.setStroke(Color.BLACK);
					;
					gc.strokeOval(goalPoint.getX() - 25, goalPoint.getY() - 25, 50, 50);
					gc.setFill(Color.BLACK);
					gc.fillText("골", goalPoint.getX() - 8, goalPoint.getY() + 4);
				}
			}
		}
	}

	private void setupRightPanel() {
		rightPanel = new VBox(10);
		rightPanel.setPadding(new Insets(10));
		rightPanel.setPrefWidth(200);

		root.setRight(rightPanel);
		pieceDisplayPanel = rightPanel;
		displayPieces(game.players.size(), game.maxPieceCount);
	}

	private void setupBottomControls() {
		HBox bottomPanel = new HBox(10);
		bottomPanel.setPadding(new Insets(10));
		bottomPanel.setAlignment(Pos.CENTER);

		Button restartButton = new Button("게임 재시작");
		Button exitButton = new Button("게임 종료");

		restartButton.setOnAction(e -> {
			Stage stage = (Stage) root.getScene().getWindow();
			stage.close();
			new GameUI().start(new Stage());
		});

		exitButton.setOnAction(e -> System.exit(0));

		bottomPanel.getChildren().addAll(restartButton, exitButton);
		root.setBottom(bottomPanel);
	}

	private void handleRandomThrow() {
		game.throwYutRandom();
		int result = game.getLastResult();
		isBackDo = (result == -1); // ✅ 빽도 여부 판단
		// ✅ checkZeroBack에서 턴을 넘긴 경우: UI 반영
		if (game.checkZeroBack()) {
			updateTurnLabel();
			drawBoard(boardCanvas.getGraphicsContext2D());
			setThrowButtonsEnabled(true);
			showAlert("윷 던지기 결과", "빽도\n판에 말이 없어 턴을 넘깁니다.");
			return;
		}

		// 🔸 윷 결과 값을 ComboBox에 반영 (예: yutSelector.setValue(...))
		if (yutSelector != null) {
			int comboIndex = switch (result) {
			case -1 -> 0;
			case 1 -> 1;
			case 2 -> 2;
			case 3 -> 3;
			case 4 -> 4;
			case 5 -> 5;
			default -> -1;
			};
			if (comboIndex >= 0 && comboIndex < yutSelector.getItems().size()) {
				yutSelector.getSelectionModel().select(comboIndex);
			}
		}

		// 윷/모가 나왔는지 확인
		if (result >= 4) {
			showAlert("윷 던지기 결과", convertResultToName(result) + "\n윷을 한번 더 던져주세요");
			setThrowButtonsEnabled(true);
		} else {
			showAlert("윷 던지기 결과", convertResultToName(result) + "\n말을 클릭해 이동하세요");
			setThrowButtonsEnabled(false); // ⛔ 던지기 버튼 잠금
		}

		movePreview.clear();
		selectedPieceIndex = -1;
		selectedPieceCircle = null;
		drawBoard(boardCanvas.getGraphicsContext2D());
	}

	private void handleCustomThrow() {
		int selectedIndex = yutSelector.getSelectionModel().getSelectedIndex();
		int[] yutValues = { -1, 1, 2, 3, 4, 5 };
		int value = yutValues[selectedIndex];

		game.throwYutSelect(value);
		yutSelector.getSelectionModel().select(convertResultToName(value));
		isBackDo = (value == -1); // ✅ 빽도 여부 판단

		// ✅ checkZeroBack에서 턴을 넘긴 경우: UI 반영
		if (game.checkZeroBack()) {
			updateTurnLabel(); // 턴 라벨 갱신
			drawBoard(boardCanvas.getGraphicsContext2D()); // 말 위치 갱신
			setThrowButtonsEnabled(true); // 버튼 다시 활성화
			showAlert("윷 던지기 결과", "빽도\n 판에 말이 없어 턴을 넘깁니다.");
			return;
		}
		movePreview.clear(); // ✅ 미리보기 제거
		selectedPieceIndex = -1; // ✅ 선택 초기화
		selectedPieceCircle = null; // ✅ 시각 선택 초기화
		drawBoard(boardCanvas.getGraphicsContext2D());
		if (value >= 4)
			showAlert("선택된 윷", yutSelector.getSelectionModel().getSelectedItem() + "\n한번 더 던지세요");
		else {
			setThrowButtonsEnabled(false); // 이동할 때까지 다시 못던지게
			showAlert("선택된 윷: ", yutSelector.getSelectionModel().getSelectedItem() + "\n말을 클릭해 이동하세요");
		}
	}

	private void handleMove() {
		if (selectedPieceIndex >= 0 && !game.resultQueue.isEmpty() && !movePreview.isEmpty()) {
			Player currentPlayer = game.players.get(game.getCurrentPlayerIndex());
			Piece selected = currentPlayer.getPieces().get(selectedPieceIndex);
			if (selectedPieceCircle != null)
				selectedPieceCircle.stopBlinking();

			int from = selected.getLocation();
			int move = game.resultQueue.get(0);
			int dest = (from == -1) ? move : game.map.getDestination(from, move);

			game.makeTurn(selectedPieceIndex, selectedMoveIndex); // 이동
			selectedMoveIndex = -1; // 초기화

			if (game.finishPlayers.size() == 1) {
				Player winner = game.finishPlayers.get(0); // 가장 먼저 골인한 플레이어
				showAlert("🎉 승리", "플레이어 " + (winner.getName() + 1) + "번이 모든 말을 내보내 승리했습니다!");

				moveButton.setDisable(true);
				randomThrowButton.setDisable(true);
				customThrowButton.setDisable(true);
				yutSelector.setDisable(true);
				pieceSelector.setDisable(true);
			}

			if (game.didCatchThisTurn()) {
				showAlert("잡기 성공", "말을 잡아 윷을 한 번 더 던질 수 있습니다!");
			}

			updatePieceColors();
			if (selected.getIsGoal()) {
				displayPieces(game.players.size(), game.maxPieceCount);
			}

			movePreview.clear();
			drawBoard(boardCanvas.getGraphicsContext2D());
			updateTurnLabel();

			System.out.println("🟢 이동 완료: " + from + " → " + dest);

			if (game.resultQueue.isEmpty() || game.didCatchThisTurn()) {
				setThrowButtonsEnabled(true);
				if (!game.players.isEmpty() && !game.didCatchThisTurn() && game.finishPlayers.size() != 1) {
					int nextPlayer = game.getCurrentPlayerIndex();
					showAlert("다음 턴", "플레이어 " + (nextPlayer + 1) + "번의 턴입니다.");
				}
				game.clearCatchCount();
			} else {
				setThrowButtonsEnabled(false);
			}

			game.clearCatchCount();

		} else {
			if (game.resultQueue.isEmpty()) {
				showAlert("오류", "먼저 윷을 던져주세요.");
			} else if (selectedPieceIndex < 0) {
				showAlert("오류", "이동시킬 말을 선택해주세요.");
			}
		}
	}

	private void handleBoardClick(MouseEvent e) {
		if (game.isFinished())
			return;

		double clickX = e.getX();
		double clickY = e.getY();

		movePreview.clear();

		Player current = game.players.get(game.getCurrentPlayerIndex());

		for (Piece piece : current.getPieces()) {
			int loc = piece.getLocation();
			if (loc >= 0 && mapPoints.containsKey(loc)) {
				Point2D pt = mapPoints.get(loc);

				// 클릭 지점이 말의 중심 반경 내인지 확인
				if (pt.distance(clickX, clickY) <= 25) {

					// 이전 깜빡임 정리
					if (selectedPieceCircle != null) {
						selectedPieceCircle.stopBlinking();
						selectedPieceCircle = null;
					}

					selectedPieceIndex = piece.getPieceNum();
					selectedPlayerIndex = game.getCurrentPlayerIndex();
					selectedPiece = piece;
					pieceSelector.getSelectionModel().select(selectedPieceIndex); // 콤보 동기화

					// 깜빡임 처리
					selectedPieceCircle = pieceCircleMap.get(piece);
					if (selectedPieceCircle != null) {
						selectedPieceCircle.startBlinking();
					}

					// 이동 미리보기 계산
					if (!game.resultQueue.isEmpty()) {
						if (game.resultQueue.size() == 1 || game.resultQueue.get(0) < 4) {
							int move = game.resultQueue.get(0);
							int from = piece.getLocation();
							int dest = (from == -1) ? move : game.map.getDestination(from, move);
							if (dest >= -1) {
								movePreview.add(dest);
								selectedMoveIndex = 0;
							}
						} else {
							showMoveSelectionDialog();
						}
					}

					// 다시 그리기
					drawBoard(boardCanvas.getGraphicsContext2D());

					return; // 클릭된 말 찾았으면 종료
				}
			}
		}
	}

	private void updateTurnLabel() {
		if (!game.players.isEmpty()) {
			int current = game.getCurrentPlayerIndex();
			turnLabel.setText("플레이어 " + (current + 1) + "번의 턴입니다.");
		} else {
			turnLabel.setText("게임 종료");
		}
	}

	private void setThrowButtonsEnabled(boolean enabled) {
		randomThrowButton.setDisable(!enabled);
		customThrowButton.setDisable(!enabled);
		yutSelector.setDisable(!enabled);
	}


	private void displayPieces(int playerCount, int pieceCount) {
		pieceDisplayPanel.getChildren().clear();
		pieceSelector = new ComboBox<>();
		pieceSelector.setPromptText("말 선택");
		pieceSelector.setDisable(true);
		pieceDisplayPanel.getChildren().add(0, pieceSelector);
		pieceCircleMap.clear();

		for (int p = 0; p < playerCount; p++) {
			VBox playerBox = new VBox(10);
			playerBox.setPadding(new Insets(10));
			playerBox.setAlignment(Pos.CENTER);
			playerBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-border-radius: 5;");

			Label title = new Label("플레이어 " + (p + 1));
			title.setStyle("-fx-font-weight: bold;");
			playerBox.getChildren().add(title);

			GridPane grid = new GridPane();
			grid.setHgap(15);
			grid.setVgap(15);
			grid.setAlignment(Pos.CENTER);

			// 🔧 열 3개를 균등하게 분배
			for (int col = 0; col < 3; col++) {
				ColumnConstraints cc = new ColumnConstraints();
				cc.setHalignment(HPos.CENTER); // 수평 가운데 정렬
				cc.setHgrow(Priority.ALWAYS); // 너비 자동 확장
				grid.getColumnConstraints().add(cc);
			}

			Player player = game.players.get(p);

			for (int i = 0; i < pieceCount; i++) {
				VBox pieceWithLabel = new VBox(4);
				pieceWithLabel.setAlignment(Pos.TOP_CENTER);

				Piece pieceModel = player.getPieces().get(i);
				PieceCircle piece = new PieceCircle(playerColors[p]);
				piece.setPrefSize(40, 40);
				pieceCircleMap.put(pieceModel, piece);

				Label label = new Label("말 " + (i + 1));
				label.setTextAlignment(TextAlignment.CENTER);
				label.setAlignment(Pos.CENTER);
				label.setPadding(Insets.EMPTY);

				if (pieceModel.getIsGoal()) {
					piece.setColor(Color.BLACK);
					label.setText("말 " + (i + 1) + " ✔");
				}

				pieceWithLabel.getChildren().addAll(piece, label);
				grid.add(pieceWithLabel, i % 3, i / 3);

				final int selectedPlayer = p;
				final int selectedPiece = i;

				piece.setOnMouseClicked(e -> {
					if (game.isFinished())
						return;

					int current = game.getCurrentPlayerIndex();

					if (selectedPlayer != current || pieceModel.getIsGoal())
						return;

					if (game.resultQueue.size() == 1 && isBackDo && pieceModel.getLocation() == -1) {
						showAlert("알림", "다른 말을 선택하세요.");
						return;
					}

					selectedPieceIndex = selectedPiece;
					pieceSelector.getSelectionModel().select(selectedPiece);

					if (selectedPieceCircle != null) {
						selectedPieceCircle.stopBlinking();
					}

					if (!game.resultQueue.isEmpty()) {
						piece.startBlinking();
						selectedPieceCircle = piece;
					}

					if (!game.resultQueue.isEmpty()) {
						List<Integer> results = game.resultQueue;
						List<String> resultNames = new ArrayList<>();
						for (int r : results) {
							if (r == -1 && pieceModel.getLocation() == -1)
								continue;
							resultNames.add(convertResultToName(r) + " (" + r + "칸)");
						}

						if (results.size() == 1 || results.get(0) < 4) {
							int move = results.get(0);
							int from = pieceModel.getLocation();
							int dest = (from == -1) ? move : game.map.getDestination(from, move);
							if (dest >= 0) {
								movePreview.clear();
								movePreview.add(dest);
								selectedMoveIndex = 0;
							}
						} else {
							ChoiceDialog<String> dialog = new ChoiceDialog<>(resultNames.get(0), resultNames);
							dialog.setTitle("이동 선택");
							dialog.setHeaderText("사용할 이동 값을 선택하세요:");
							Optional<String> selected = dialog.showAndWait();

							if (selected.isPresent()) {
								int selectedIndex = resultNames.indexOf(selected.get());
								int from = pieceModel.getLocation();
								int move = game.resultQueue.get(selectedIndex);
								int dest = (from == -1) ? move : game.map.getDestination(from, move);

								if (dest >= 0 || dest == -1) {
									movePreview.clear();
									movePreview.add(dest);
									selectedPieceIndex = pieceModel.getPieceNum();
									selectedPieceCircle = piece;
									piece.startBlinking();
									selectedMoveIndex = selectedIndex;
								}
							}
						}

						movePreview.clear();
						if (!game.resultQueue.isEmpty() && selectedMoveIndex >= 0) {
							Piece selectedP = game.players.get(game.getCurrentPlayerIndex()).getPieces()
									.get(selectedPiece);
							int from = selectedP.getLocation();
							int move = game.resultQueue.get(selectedMoveIndex);
							int dest = (from == -1) ? move : game.map.getDestination(from, move);
							if (dest >= 0) {
								movePreview.add(dest);
							}
						}

						drawBoard(boardCanvas.getGraphicsContext2D());
					}
				});
			}

			playerBox.getChildren().add(grid);
			pieceDisplayPanel.getChildren().add(playerBox);
		}
	}

	public void updatePieceColors() {
		for (Map.Entry<Piece, PieceCircle> entry : pieceCircleMap.entrySet()) {
			Piece piece = entry.getKey();
			PieceCircle circle = entry.getValue();

			if (piece.getLocation() >= 0) {
				circle.setColor(Color.GRAY); // 예: 맵에 올라간 말은 회색
			} else if (piece.getIsGoal()) {
				circle.setColor(Color.BLACK); // 골인한 말은 검정색
			} else {
				circle.resetColor(); // 초기 색상 복원 메서드 구현 필요
			}
			circle.update();
		}
	}

	private void showMoveSelectionDialog() {
		List<Integer> results = game.resultQueue;
		List<String> resultNames = new ArrayList<>();

		for (int i = 0; i < results.size(); i++) {
			int r = results.get(i);
			String name = convertResultToName(r) + " (" + r + "칸)";
			resultNames.add(name);
		}

		// JavaFX ChoiceDialog 사용
		ChoiceDialog<String> dialog = new ChoiceDialog<>(resultNames.get(0), resultNames);
		dialog.setTitle("이동 선택");
		dialog.setHeaderText("사용할 이동 값을 선택하세요");
		dialog.setContentText("이동:");

		Optional<String> result = dialog.showAndWait();
		result.ifPresent(selected -> {
			int idx = resultNames.indexOf(selected);
			selectedMoveIndex = idx;

			// ✅ 미리보기 목적지 계산
			Piece selectedPiece = game.players.get(game.getCurrentPlayerIndex()).getPieces().get(selectedPieceIndex);
			int from = selectedPiece.getLocation();
			int move = game.resultQueue.get(idx);
			int dest = (from == -1) ? move : game.map.getDestination(from, move);

			movePreview.clear();
			if (dest >= -1) {
				movePreview.add(dest);
			}

			drawBoard(boardCanvas.getGraphicsContext2D()); // JavaFX에 맞게 다시 그리기
		});
	}

	private GameConfig showGameConfigDialog() {
		Dialog<GameConfig> dialog = new Dialog<>();
		dialog.setTitle("게임 설정");

		ButtonType startButtonType = new ButtonType("시작", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(startButtonType, ButtonType.CANCEL);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		ComboBox<String> playerBox = new ComboBox<>();
		playerBox.getItems().addAll("2", "3", "4");
		playerBox.setValue("2");

		ComboBox<String> pieceBox = new ComboBox<>();
		pieceBox.getItems().addAll("2", "3", "4", "5");
		pieceBox.setValue("4");

		ComboBox<String> boardBox = new ComboBox<>();
		boardBox.getItems().addAll("사각형", "오각형", "육각형");
		boardBox.setValue("사각형");

		grid.add(new Label("참여자 수"), 0, 0);
		grid.add(playerBox, 1, 0);
		grid.add(new Label("말 개수"), 0, 1);
		grid.add(pieceBox, 1, 1);
		grid.add(new Label("판 형태"), 0, 2);
		grid.add(boardBox, 1, 2);

		dialog.getDialogPane().setContent(grid);

		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == startButtonType) {
				int players = Integer.parseInt(playerBox.getValue());
				int pieces = Integer.parseInt(pieceBox.getValue());
				String board = boardBox.getValue();
				return new GameConfig(players, pieces, board);
			}
			return null;
		});

		return dialog.showAndWait().orElse(null);
	}

	private String convertResultToName(int r) {
		return switch (r) {
		case -1 -> "빽도";
		case 1 -> "도";
		case 2 -> "개";
		case 3 -> "걸";
		case 4 -> "윷";
		case 5 -> "모";
		default -> r + "칸";
		};
	}

	private void showAlert(String title, String content) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
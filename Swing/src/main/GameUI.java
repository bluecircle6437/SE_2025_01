package main;

import javax.swing.JFrame;
//GameUI.java - 자바 스윙 기반 윷놀이판 UI 구성 (출발점, 원형 말판 포함)
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import java.awt.Color;

public class GameUI extends JFrame {
	private Game game;
	private JPanel boardPanel;
	private JPanel pieceDisplayPanel;
	private JButton randomThrowButton;
	private JButton customThrowButton;
	private JButton moveButton;
	private JComboBox<String> yutSelector;
	private JComboBox<String> pieceSelector;
	private JLabel turnLabel;
	private int selectedPieceIndex = -1; // 말 선택용
	private int selectedPlayerIndex = -1;
	PieceCircle selectedPieceCircle = null;
	int selectedMoveIndex = -1;
	private Map<Integer, Point> MapPoints;
	private Map<Integer, Point> squareMapPoints;
	private Map<Integer, Point> pentagonMapPoints;
	private Map<Integer, Point> hexagonMapPoints;
	private List<Integer> movePreview = new ArrayList<>();
	private Map<Piece, PieceCircle> pieceCircleMap = new HashMap<>();
	private boolean isBackDo = false; // 빽도 상태 여부 저장
	Color[] playerColors = { Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW };

	private String convertResultToName(int r) {
		switch (r) {
		case -1:
			return "빽도";
		case 1:
			return "도";
		case 2:
			return "개";
		case 3:
			return "걸";
		case 4:
			return "윷";
		case 5:
			return "모";
		default:
			return r + "칸";
		}
	}

	public GameUI() {
		GameConfig config = showGameConfigDialog();
		if (config == null) {
			System.exit(0); // 사용자가 취소하면 게임 종료
		}

		// 설정 결과 출력 (또는 저장해서 활용)
		System.out.println("참여자 수: " + config.playerCount);
		System.out.println("말 개수: " + config.pieceCount);
		System.out.println("판 형태: " + config.boardShape);

		setTitle("윷놀이 게임");
		setSize(1000, 1000);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		this.game = new Game();
		this.game.initialize(config);

		pieceDisplayPanel = new JPanel();
		add(pieceDisplayPanel, BorderLayout.EAST);
		displayPieces(config.playerCount, config.pieceCount);

		// 상단 컨트롤 패널
		JPanel controlPanel = new JPanel();
		randomThrowButton = new JButton("랜덤 윷 던지기");
		customThrowButton = new JButton("지정 윷 던지기");
		moveButton = new JButton("말 이동");
		yutSelector = new JComboBox<>(new String[] { "빽도", "도", "개", "걸", "윷", "모" });
		String[] pieces = new String[config.pieceCount];
		for (int i = 0; i < config.pieceCount; i++) {
			pieces[i] = "말 " + (i + 1);
		}
		pieceSelector = new JComboBox<>(pieces);

		turnLabel = new JLabel("플레이어 1번의 턴입니다.");
		turnLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
		controlPanel.add(turnLabel);

		controlPanel.add(new JLabel("윷 선택:"));
		controlPanel.add(yutSelector);
		controlPanel.add(randomThrowButton);
		controlPanel.add(customThrowButton);
		controlPanel.add(moveButton);

		add(controlPanel, BorderLayout.NORTH);

		// 윷놀이판 패널
		boardPanel = new JPanel() {
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				switch (config.boardShape) {
				case "사각형":
					initializeSquareMapPoints();
					MapPoints = squareMapPoints;
					drawBoard(g);
					break;
				case "오각형":
					initializePentagonMapPoints();
					MapPoints = pentagonMapPoints;
					drawBoard(g);
					break;
				case "육각형":
					initializeHexagonMapPoints();
					MapPoints = hexagonMapPoints;
					drawBoard(g);
					break;
				}

			}
		};

		boardPanel.setBackground(Color.WHITE);
		add(boardPanel, BorderLayout.CENTER);

		boardPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// ✅ 게임 종료 후 클릭 막기
				if (game.isFinished())
					return;

				Point clicked = e.getPoint();

				// 말 클릭 판정
				for (int p = 0; p < game.players.size(); p++) {
					Player player = game.players.get(p);
					for (Piece piece : player.getPieces()) {
						int loc = piece.getLocation();
						if (loc >= 0 && MapPoints.containsKey(loc)) {
							Point pos = MapPoints.get(loc);
							if (clicked.distance(pos) <= 25) { // 말의 반지름 기준

								// ✅ 현재 플레이어만 반응
								if (p != game.getCurrentPlayerIndex()) {
									return; // 다른 플레이어면 무시
								}

								// ✅ 맵 클릭 시 기존 깜빡이던 말 중지
								if (selectedPieceCircle != null) {
									selectedPieceCircle.stopBlinking();
									selectedPieceCircle = null;
								}
								selectedPieceIndex = -1;
								pieceSelector.setSelectedIndex(-1); // 콤보박스도 초기화

								// 선택된 말 정보 저장
								selectedPieceIndex = piece.getPieceNum();
								selectedPlayerIndex = p;

								// 미리보기 계산
								movePreview.clear();
								if (!game.resultQueue.isEmpty()) {
									if (game.resultQueue.size() == 1) {
										int move = game.resultQueue.get(0);
										int from = piece.getLocation();
										int dest = (from == -1) ? move : game.map.getDestination(from, move);
										if (dest >= -1)
											movePreview.add(dest);
										selectedMoveIndex = 0;
									} else {
										showMoveSelectionDialog();
									}

									// 다시 그리기
									boardPanel.repaint();
								}

								return;
							}
						}
					}
				}
			}
		});

		// 하단 버튼
		JPanel bottomPanel = new JPanel();
		JButton restartButton = new JButton("게임 재시작");
		JButton exitButton = new JButton("게임 종료");
		bottomPanel.add(restartButton);
		bottomPanel.add(exitButton);
		add(bottomPanel, BorderLayout.SOUTH);

		// 예시 이벤트 연결
		randomThrowButton.addActionListener(new ActionListener() {
			// 윷 던지기 로직 실행
			@Override
			public void actionPerformed(ActionEvent e) {
				game.throwYutRandom();
				int lastResult = game.getLastResult();
				isBackDo = (lastResult == -1); // ✅ 빽도 여부 판단
				// ✅ checkZeroBack에서 턴을 넘긴 경우: UI 반영
				if (game.checkZeroBack()) {
					updateTurnLabel(); // 턴 라벨 갱신
					boardPanel.repaint(); // 말 위치 갱신
					setThrowButtonsEnabled(true); // 버튼 다시 활성화
					JOptionPane.showMessageDialog(null, "윷 던지기 결과: 빽도\n 판에 말이 없어 턴을 넘깁니다.");
					return;
				}

				// 🔸 윷 결과 값을 yutSelector에 반영
				int result = game.getLastResult();
				int comboIndex = switch (result) {
				case -1 -> 0; // 빽도
				case 1 -> 1; // 도
				case 2 -> 2; // 개
				case 3 -> 3; // 걸
				case 4 -> 4; // 윷
				case 5 -> 5; // 모
				default -> -1;
				};

				if (comboIndex >= 0) {
					yutSelector.setSelectedIndex(comboIndex);
				}

				// 윷/모가 나왔는지 확인
				if (game.getLastResult() >= 4) {
					JOptionPane.showMessageDialog(null,
							"윷 던지기 결과: " + convertResultToName(game.getLastResult()) + "\n윷을 한번 더 던져주세요");
					setThrowButtonsEnabled(true);
				} else {
					JOptionPane.showMessageDialog(null,
							"윷 던지기 결과: " + convertResultToName(game.getLastResult()) + "\n말을 클릭해 이동하세요");
					setThrowButtonsEnabled(false); // ⛔ 던지기 버튼 잠금
				}

				movePreview.clear(); // ✅ 미리보기 제거
				selectedPieceIndex = -1; // ✅ 선택 초기화
				selectedPieceCircle = null; // ✅ 시각 선택 초기화
				boardPanel.repaint();
			}
		});

		customThrowButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedIndex = yutSelector.getSelectedIndex();
				int[] yutValues = { -1, 1, 2, 3, 4, 5 };
				int value = yutValues[selectedIndex];

				game.throwYutSelect(value);
				yutSelector.setSelectedItem(convertResultToName(value));
				isBackDo = (value == -1); // ✅ 빽도 여부 판단

				// ✅ checkZeroBack에서 턴을 넘긴 경우: UI 반영
				if (game.checkZeroBack()) {
					updateTurnLabel(); // 턴 라벨 갱신
					boardPanel.repaint(); // 말 위치 갱신
					setThrowButtonsEnabled(true); // 버튼 다시 활성화
					JOptionPane.showMessageDialog(null, "윷 던지기 결과: 빽도\n 판에 말이 없어 턴을 넘깁니다.");
					return;
				}
				movePreview.clear(); // ✅ 미리보기 제거
				selectedPieceIndex = -1; // ✅ 선택 초기화
				selectedPieceCircle = null; // ✅ 시각 선택 초기화
				boardPanel.repaint();
				if (value >= 4)
					JOptionPane.showMessageDialog(null, "선택된 윷: " + yutSelector.getSelectedItem() + "\n한번 더 던지세요");
				else {
					setThrowButtonsEnabled(false); // 이동할 때까지 다시 못던지게
					JOptionPane.showMessageDialog(null, "선택된 윷: " + yutSelector.getSelectedItem() + "\n말을 클릭해 이동하세요");
				}
			}
		});

		moveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
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
						JOptionPane.showMessageDialog(null,
								"🎉 플레이어 " + (winner.getName() + 1) + "번이 모든 말을 내보내 승리했습니다!");

						moveButton.setEnabled(false);
						randomThrowButton.setEnabled(false);
						customThrowButton.setEnabled(false);
						yutSelector.setEnabled(false);
						pieceSelector.setEnabled(false);
					}

					// ✅ 잡기 메시지 처리
					if (game.didCatchThisTurn()) {
						JOptionPane.showMessageDialog(null, "말을 잡아 윷을 한 번 더 던질 수 있습니다!");
					}
					updatePieceColors();
					// 골인 상태 반영
					if (selected.getIsGoal()) {
						displayPieces(game.players.size(), game.maxPieceCount);
					}
					movePreview.clear();
					boardPanel.repaint();
					updateTurnLabel();

					System.out.println("🟢 이동 완료: " + from + " → " + dest);

					// ✅ 이동 후 남은 결과 큐에 따라 처리 분기
					if (game.resultQueue.isEmpty() || game.didCatchThisTurn()) {
						setThrowButtonsEnabled(true); // 윷 던지기 버튼 활성화

						if (!game.players.isEmpty() && !game.didCatchThisTurn() && game.finishPlayers.size() != 1) {
							int nextPlayer = game.getCurrentPlayerIndex();
							JOptionPane.showMessageDialog(null, "플레이어 " + (nextPlayer + 1) + "번의 턴입니다.");
						}

						//game.clearCatchCount();
					} else {
						setThrowButtonsEnabled(false); // 결과 큐 남아있으면 윷 못던지게
					}

					// ✅ 여기서 catchCount 감소 (1회 잡기만 인정)
					//game.clearCatchCount();

				} else {
					if (game.resultQueue.isEmpty()) {
						JOptionPane.showMessageDialog(null, "먼저 윷을 던져주세요.");
					} else if (selectedPieceIndex < 0) {
						JOptionPane.showMessageDialog(null, "이동시킬 말을 선택해주세요.");
					}
				}
			}
		});

		restartButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				SwingUtilities.invokeLater(() -> {
					GameUI newGame = new GameUI();
					newGame.setVisible(true);
				});
			}
		});

		exitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

	}

	private GameConfig showGameConfigDialog() {
		String[] playerOptions = { "2", "3", "4" };
		String[] pieceOptions = { "2", "3", "4", "5" };
		String[] boardShapes = { "사각형", "오각형", "육각형" };

		JComboBox<String> playerBox = new JComboBox<>(playerOptions);
		JComboBox<String> pieceBox = new JComboBox<>(pieceOptions);
		JComboBox<String> boardBox = new JComboBox<>(boardShapes);

		JPanel panel = new JPanel(new GridLayout(0, 2));
		panel.add(new JLabel("참여자의 명수"));
		panel.add(playerBox);
		panel.add(new JLabel("말 갯수"));
		panel.add(pieceBox);
		panel.add(new JLabel("윷놀이 판"));
		panel.add(boardBox);

		int result = JOptionPane.showConfirmDialog(null, panel, "게임 설정", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {
			int players = Integer.parseInt((String) playerBox.getSelectedItem());
			int pieces = Integer.parseInt((String) pieceBox.getSelectedItem());
			String boardShape = (String) boardBox.getSelectedItem();
			return new GameConfig(players, pieces, boardShape);
		} else {
			return null;
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
		randomThrowButton.setEnabled(enabled);
		customThrowButton.setEnabled(enabled);
		yutSelector.setEnabled(enabled);
	}

	private void displayPieces(int playerCount, int pieceCount) {
		pieceDisplayPanel.removeAll();
		pieceDisplayPanel.setLayout(new GridLayout(playerCount, 1, 10, 10));

		pieceCircleMap.clear(); // 매번 새로 갱신

		for (int p = 0; p < playerCount; p++) {
			JPanel playerPanel = new JPanel();
			playerPanel.setLayout(new BorderLayout());
			playerPanel.setBorder(BorderFactory.createTitledBorder("플레이어 " + (p + 1)));

			Player player = game.players.get(p); // 이게 있어야 함

			JPanel pieceGrid = new JPanel(new GridLayout(0, 2, 10, 10)); // 2열 그리드로 변경
			for (int i = 0; i < pieceCount; i++) {
				JPanel pieceWithLabel = new JPanel();
				pieceWithLabel.setLayout(new BoxLayout(pieceWithLabel, BoxLayout.Y_AXIS));
				pieceWithLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

				Piece pieceModel = player.getPieces().get(i); // 말 모델
				PieceCircle piece = new PieceCircle(playerColors[p]);
				pieceCircleMap.put(player.getPieces().get(i), piece); // 연결

				// ✅ 클릭 이벤트 추가 위치 (PieceCircle 생성 후)
				final int selectedPlayer = p;
				final int selectedPiece = i;

				pieceCircleMap.put(pieceModel, piece); // 연결

				JLabel label = new JLabel("말 " + (i + 1), SwingConstants.CENTER);
				label.setAlignmentX(Component.CENTER_ALIGNMENT);

				// ✅ 골인한 말 처리
				if (pieceModel.getIsGoal()) {
					System.out.println("✅ 골인 말 감지됨: " + pieceModel.getPieceNum());
					piece.setColor(Color.BLACK); // 골인한 말은 회색 처리
					label.setText("말 " + (i + 1) + " ✔");
				}

				pieceWithLabel.add(piece);
				pieceWithLabel.add(Box.createVerticalStrut(4)); // 말과 텍스트 간 간격 조절
				pieceGrid.add(pieceWithLabel);
				pieceGrid.add(label);

				piece.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						// ✅ 게임 종료 후 클릭 막기
						if (game.isFinished())
							return;

						Piece actualPiece = player.getPieces().get(selectedPiece);
						// ✅ 현재 플레이어 확인
						int current = game.getCurrentPlayerIndex();
						if (selectedPlayer != current || pieceModel.getIsGoal()) {
							// ❌ 다른 플레이어의 말은 클릭 무시
							return;
						}

						if (game.resultQueue.size() == 1 && isBackDo && actualPiece.getLocation() == -1) {
							JOptionPane.showMessageDialog(null, "다른 말을 선택하세요.");
							return;
						}

						selectedPieceIndex = selectedPiece;
						pieceSelector.setSelectedIndex(selectedPiece); // 콤보박스와 동기화

						// 이전 말 깜빡임 종료
						if (selectedPieceCircle != null) {
							selectedPieceCircle.stopBlinking();
						}

						// 현재 말 깜빡임 시작
						if (!game.resultQueue.isEmpty()) {
							piece.startBlinking();
							selectedPieceCircle = piece;
						}

						// ✅ 이동 순서 선택 팝업
						if (!game.resultQueue.isEmpty()) {
							List<Integer> results = game.resultQueue;
							List<String> resultNames = new ArrayList<>();
							for (int i = 0; i < game.resultQueue.size(); i++) {
								int r = game.resultQueue.get(i);
								if (r == -1 && actualPiece.getLocation() == -1)
									continue;
								String name = convertResultToName(r) + " (" + r + "칸)";
								resultNames.add(i + ": " + name);
							}

							if (results.size() == 1 || results.get(0) < 4) {
								// 선택 없이 첫 값 자동 적용
								int move = results.get(0);
								int from = actualPiece.getLocation();
								int dest = (from == -1) ? move : game.map.getDestination(from, move);
								if (dest >= 0) {
									movePreview.add(dest);
									selectedMoveIndex = 0;
								}
							}

							else {
								Object selected = JOptionPane.showInputDialog(null, "사용할 이동 값을 선택하세요:", "이동 선택",
										JOptionPane.QUESTION_MESSAGE, null, resultNames.toArray(), resultNames.get(0));

								if (selected != null) {
									int selectedIndex = resultNames.indexOf(selected.toString());

									int from = pieceModel.getLocation();
									int move = game.resultQueue.get(selectedIndex);
									int dest = (from == -1) ? move : game.map.getDestination(from, move);

									if (dest >= 0 || dest == -1) {
										movePreview.clear();
										movePreview.add(dest);
										boardPanel.repaint();
										selectedPieceIndex = pieceModel.getPieceNum();
										selectedPieceCircle = piece;
										piece.startBlinking();

										// ✅ 선택된 인덱스를 저장해서 이동 버튼 누를 때 사용
										selectedMoveIndex = selectedIndex;
									}
								}
							}

							// ✅ 미리보기 위치 설정
							movePreview.clear();
							if (!game.resultQueue.isEmpty() && selectedMoveIndex >= 0) {
								Player currentPlayer = game.players.get(game.getCurrentPlayerIndex());
								Piece selected = currentPlayer.getPieces().get(selectedPiece);
								int from = selected.getLocation();
								int move = game.resultQueue.get(selectedMoveIndex); // 가장 앞 이동 값만 사용

								int dest = (from == -1) ? move : game.map.getDestination(from, move);
								if (dest >= 0) {
									movePreview.add(dest);
								}
							}
						}

						boardPanel.repaint(); // ⬅️ 이동 미리보기 표시
					}
				});
			}
			playerPanel.add(pieceGrid, BorderLayout.CENTER);
			pieceDisplayPanel.add(playerPanel);
		}

		pieceDisplayPanel.revalidate();
		pieceDisplayPanel.repaint();
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
			circle.repaint();
		}
	}

	private void showMoveSelectionDialog() {
		List<Integer> results = game.resultQueue;
		List<String> resultNames = new ArrayList<>();
		for (int r : results)
			resultNames.add(convertResultToName(r));

		Object selected = JOptionPane.showInputDialog(null, "사용할 이동 값을 선택하세요:", "이동 선택", JOptionPane.QUESTION_MESSAGE,
				null, resultNames.toArray(), resultNames.get(0));

		if (selected != null) {
			int idx = resultNames.indexOf(selected.toString());
			selectedMoveIndex = idx;

			// 미리보기 목적지 계산
			Piece selectedPiece = game.players.get(game.getCurrentPlayerIndex()).getPieces().get(selectedPieceIndex);
			int from = selectedPiece.getLocation();
			int move = game.resultQueue.get(idx);
			int dest = (from == -1) ? move : game.map.getDestination(from, move);

			movePreview.clear();
			if (dest >= -1)
				movePreview.add(dest);
			boardPanel.repaint();
		}
	}

	private void initializeSquareMapPoints() {
		squareMapPoints = new HashMap<>();
		int offsetX = 200;
		int offsetY = 200;
		int spacing = 30;

		// 바깥 경로
		squareMapPoints.put(0, new Point(offsetX + spacing * 15, offsetY + spacing * 15));
		squareMapPoints.put(1, new Point(offsetX + spacing * 15, offsetY + spacing * 12));
		squareMapPoints.put(2, new Point(offsetX + spacing * 15, offsetY + spacing * 9));
		squareMapPoints.put(3, new Point(offsetX + spacing * 15, offsetY + spacing * 6));
		squareMapPoints.put(4, new Point(offsetX + spacing * 15, offsetY + spacing * 3));
		squareMapPoints.put(5, new Point(offsetX + spacing * 15, offsetY + spacing * 0));
		squareMapPoints.put(6, new Point(offsetX + spacing * 12, offsetY + spacing * 0));
		squareMapPoints.put(7, new Point(offsetX + spacing * 9, offsetY + spacing * 0));
		squareMapPoints.put(8, new Point(offsetX + spacing * 6, offsetY + spacing * 0));
		squareMapPoints.put(9, new Point(offsetX + spacing * 3, offsetY + spacing * 0));
		squareMapPoints.put(10, new Point(offsetX + spacing * 0, offsetY + spacing * 0));
		squareMapPoints.put(11, new Point(offsetX + spacing * 0, offsetY + spacing * 3));
		squareMapPoints.put(12, new Point(offsetX + spacing * 0, offsetY + spacing * 6));
		squareMapPoints.put(13, new Point(offsetX + spacing * 0, offsetY + spacing * 9));
		squareMapPoints.put(14, new Point(offsetX + spacing * 0, offsetY + spacing * 12));
		squareMapPoints.put(15, new Point(offsetX + spacing * 0, offsetY + spacing * 15));
		squareMapPoints.put(16, new Point(offsetX + spacing * 3, offsetY + spacing * 15));
		squareMapPoints.put(17, new Point(offsetX + spacing * 6, offsetY + spacing * 15));
		squareMapPoints.put(18, new Point(offsetX + spacing * 9, offsetY + spacing * 15));
		squareMapPoints.put(19, new Point(offsetX + spacing * 12, offsetY + spacing * 15));

		// 중앙점
		squareMapPoints.put(24, new Point(offsetX + (int) (7.5 * spacing), offsetY + (int) (7.5 * spacing)));

		// 대각선 경로 (5→15)
		squareMapPoints.put(20, new Point(offsetX + (int) (12.5 * spacing), offsetY + (int) (2.5 * spacing)));
		squareMapPoints.put(21, new Point(offsetX + spacing * 10, offsetY + spacing * 5));
		squareMapPoints.put(25, new Point(offsetX + spacing * 5, offsetY + spacing * 10));
		squareMapPoints.put(26, new Point(offsetX + (int) (2.5 * spacing), offsetY + (int) (12.5 * spacing)));

		// 대각선 경로 (10→0)
		squareMapPoints.put(22, new Point(offsetX + (int) (2.5 * spacing), offsetY + (int) (2.5 * spacing)));
		squareMapPoints.put(23, new Point(offsetX + spacing * 5, offsetY + spacing * 5));
		squareMapPoints.put(27, new Point(offsetX + spacing * 10, offsetY + spacing * 10));
		squareMapPoints.put(28, new Point(offsetX + (int) (12.5 * spacing), offsetY + (int) (12.5 * spacing)));
	}

	private void initializePentagonMapPoints() {
		pentagonMapPoints = new HashMap<>();
		int offsetX = 60;
		int offsetY = 150;
		int spacing = 30;

		// 바깥 경로
		pentagonMapPoints.put(0, new Point(offsetX + spacing * 20, offsetY + spacing * 19));
		pentagonMapPoints.put(1, new Point(offsetX + (int) (20.6 * spacing), offsetY + (int) (16.3 * spacing)));
		pentagonMapPoints.put(2, new Point(offsetX + (int) (21.2 * spacing), offsetY + (int) (13.6 * spacing)));
		pentagonMapPoints.put(3, new Point(offsetX + (int) (21.8 * spacing), offsetY + (int) (10.9 * spacing)));
		pentagonMapPoints.put(4, new Point(offsetX + (int) (22.4 * spacing), offsetY + (int) (8.2 * spacing)));
		pentagonMapPoints.put(5, new Point(offsetX + spacing * 23, offsetY + (int) (5.5 * spacing)));
		pentagonMapPoints.put(6, new Point(offsetX + (int) (20.9 * spacing), offsetY + spacing * 4));
		pentagonMapPoints.put(7, new Point(offsetX + (int) (18.8 * spacing), offsetY + (int) (2.5 * spacing)));
		pentagonMapPoints.put(8, new Point(offsetX + (int) (16.7 * spacing), offsetY + spacing * 1));
		pentagonMapPoints.put(9, new Point(offsetX + (int) (14.6 * spacing), offsetY + (int) (-0.5 * spacing)));
		pentagonMapPoints.put(10, new Point(offsetX + (int) (12.5 * spacing), offsetY + spacing * -2));
		pentagonMapPoints.put(11, new Point(offsetX + (int) (10.4 * spacing), offsetY + (int) (-0.5 * spacing)));
		pentagonMapPoints.put(12, new Point(offsetX + (int) (8.3 * spacing), offsetY + spacing * 1));
		pentagonMapPoints.put(13, new Point(offsetX + (int) (6.2 * spacing), offsetY + (int) (2.5 * spacing)));
		pentagonMapPoints.put(14, new Point(offsetX + (int) (4.1 * spacing), offsetY + spacing * 4));
		pentagonMapPoints.put(15, new Point(offsetX + spacing * 2, offsetY + (int) (5.5 * spacing)));
		pentagonMapPoints.put(16, new Point(offsetX + (int) (2.6 * spacing), offsetY + (int) (8.2 * spacing)));
		pentagonMapPoints.put(17, new Point(offsetX + (int) (3.2 * spacing), offsetY + (int) (10.9 * spacing)));
		pentagonMapPoints.put(18, new Point(offsetX + (int) (3.8 * spacing), offsetY + (int) (13.6 * spacing)));
		pentagonMapPoints.put(19, new Point(offsetX + (int) (4.4 * spacing), offsetY + (int) (16.3 * spacing)));
		pentagonMapPoints.put(20, new Point(offsetX + spacing * 5, offsetY + spacing * 19));
		pentagonMapPoints.put(21, new Point(offsetX + spacing * 8, offsetY + spacing * 19));
		pentagonMapPoints.put(22, new Point(offsetX + spacing * 11, offsetY + spacing * 19));
		pentagonMapPoints.put(23, new Point(offsetX + spacing * 14, offsetY + spacing * 19));
		pentagonMapPoints.put(24, new Point(offsetX + spacing * 17, offsetY + spacing * 19));

		// 중앙점
		pentagonMapPoints.put(31, new Point(offsetX + (int) (12.5 * spacing), offsetY + spacing * 10));

		// 대각선 경로 (5→31)
		pentagonMapPoints.put(25, new Point(offsetX + (int) (19.5 * spacing), offsetY + spacing * 7));
		pentagonMapPoints.put(26, new Point(offsetX + spacing * 16, offsetY + (int) (8.5 * spacing)));

		// 대각선 경로 (10→31)
		pentagonMapPoints.put(27, new Point(offsetX + (int) (12.5 * spacing), offsetY + spacing * 2));
		pentagonMapPoints.put(28, new Point(offsetX + (int) (12.5 * spacing), offsetY + spacing * 6));

		// 대각선 경로 (15→31)
		pentagonMapPoints.put(29, new Point(offsetX + (int) (5.5 * spacing), offsetY + spacing * 7));
		pentagonMapPoints.put(30, new Point(offsetX + spacing * 9, offsetY + (int) (8.5 * spacing)));

		// 대각선 경로 (20→31)
		pentagonMapPoints.put(32, new Point(offsetX + spacing * 10, offsetY + spacing * 13));
		pentagonMapPoints.put(33, new Point(offsetX + (int) (7.5 * spacing), offsetY + spacing * 16));

		// 대각선 경로 (0→31)
		pentagonMapPoints.put(34, new Point(offsetX + spacing * 15, offsetY + spacing * 13));
		pentagonMapPoints.put(35, new Point(offsetX + (int) (17.5 * spacing), offsetY + spacing * 16));
	}

	private void initializeHexagonMapPoints() {
		hexagonMapPoints = new HashMap<>();
		int offsetX = 300;
		int offsetY = 60;
		int spacing = 20;

		// 바깥 경로
		hexagonMapPoints.put(0, new Point(offsetX + spacing * 15, offsetY + spacing * 30));
		hexagonMapPoints.put(1, new Point(offsetX + spacing * 17, offsetY + spacing * 27));
		hexagonMapPoints.put(2, new Point(offsetX + spacing * 19, offsetY + spacing * 24));
		hexagonMapPoints.put(3, new Point(offsetX + spacing * 21, offsetY + spacing * 21));
		hexagonMapPoints.put(4, new Point(offsetX + spacing * 23, offsetY + spacing * 18));
		hexagonMapPoints.put(5, new Point(offsetX + spacing * 25, offsetY + spacing * 15));
		hexagonMapPoints.put(6, new Point(offsetX + spacing * 23, offsetY + spacing * 12));
		hexagonMapPoints.put(7, new Point(offsetX + spacing * 21, offsetY + spacing * 9));
		hexagonMapPoints.put(8, new Point(offsetX + spacing * 19, offsetY + spacing * 6));
		hexagonMapPoints.put(9, new Point(offsetX + spacing * 17, offsetY + spacing * 3));
		hexagonMapPoints.put(10, new Point(offsetX + spacing * 15, offsetY + spacing * 0));
		hexagonMapPoints.put(11, new Point(offsetX + spacing * 12, offsetY + spacing * 0));
		hexagonMapPoints.put(12, new Point(offsetX + spacing * 9, offsetY + spacing * 0));
		hexagonMapPoints.put(13, new Point(offsetX + spacing * 6, offsetY + spacing * 0));
		hexagonMapPoints.put(14, new Point(offsetX + spacing * 3, offsetY + spacing * 0));
		hexagonMapPoints.put(15, new Point(offsetX + spacing * 0, offsetY + spacing * 0));
		hexagonMapPoints.put(16, new Point(offsetX + spacing * -2, offsetY + spacing * 3));
		hexagonMapPoints.put(17, new Point(offsetX + spacing * -4, offsetY + spacing * 6));
		hexagonMapPoints.put(18, new Point(offsetX + spacing * -6, offsetY + spacing * 9));
		hexagonMapPoints.put(19, new Point(offsetX + spacing * -8, offsetY + spacing * 12));
		hexagonMapPoints.put(20, new Point(offsetX + spacing * -10, offsetY + spacing * 15));
		hexagonMapPoints.put(21, new Point(offsetX + spacing * -8, offsetY + spacing * 18));
		hexagonMapPoints.put(22, new Point(offsetX + spacing * -6, offsetY + spacing * 21));
		hexagonMapPoints.put(23, new Point(offsetX + spacing * -4, offsetY + spacing * 24));
		hexagonMapPoints.put(24, new Point(offsetX + spacing * -2, offsetY + spacing * 27));
		hexagonMapPoints.put(25, new Point(offsetX + spacing * 0, offsetY + spacing * 30));
		hexagonMapPoints.put(26, new Point(offsetX + spacing * 3, offsetY + spacing * 30));
		hexagonMapPoints.put(27, new Point(offsetX + spacing * 6, offsetY + spacing * 30));
		hexagonMapPoints.put(28, new Point(offsetX + spacing * 9, offsetY + spacing * 30));
		hexagonMapPoints.put(29, new Point(offsetX + spacing * 12, offsetY + spacing * 30));

		// 중앙점
		hexagonMapPoints.put(38, new Point(offsetX + (int) (7.5 * spacing), offsetY + 15 * spacing));

		// 대각선 경로 (5→20)
		hexagonMapPoints.put(30, new Point(offsetX + (int) (19 * spacing), offsetY + 15 * spacing));
		hexagonMapPoints.put(31, new Point(offsetX + spacing * 13, offsetY + 15 * spacing));
		hexagonMapPoints.put(37, new Point(offsetX + spacing * 1, offsetY + 15 * spacing));
		hexagonMapPoints.put(36, new Point(offsetX + (int) (-5 * spacing), offsetY + 15 * spacing));

		// 대각선 경로 (10→25)
		hexagonMapPoints.put(32, new Point(offsetX + (int) (12.5 * spacing), offsetY + (int) (5 * spacing)));
		hexagonMapPoints.put(33, new Point(offsetX + (int) (10 * spacing), offsetY + spacing * 10));
		hexagonMapPoints.put(39, new Point(offsetX + spacing * 5, offsetY + spacing * 20));
		hexagonMapPoints.put(40, new Point(offsetX + (int) (2.5 * spacing), offsetY + (int) (25 * spacing)));

		// 대각선 경로 (15→0)
		hexagonMapPoints.put(34, new Point(offsetX + (int) (2.5 * spacing), offsetY + (int) (5 * spacing)));
		hexagonMapPoints.put(35, new Point(offsetX + spacing * 5, offsetY + spacing * 10));
		hexagonMapPoints.put(41, new Point(offsetX + spacing * 10, offsetY + spacing * 20));
		hexagonMapPoints.put(42, new Point(offsetX + (int) (12.5 * spacing), offsetY + (int) (25 * spacing)));
	}

	private void drawBoard(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setStroke(new BasicStroke(2));

		// 칸 그리기
		if (MapPoints == squareMapPoints) {
			// 테두리 선 연결
			int[] outerLine = { 0, 5, 10, 15, 0 }; // 사각형 꼭짓점 연결
			for (int i = 0; i < outerLine.length - 1; i++) {
				Point from = MapPoints.get(outerLine[i]);
				Point to = MapPoints.get(outerLine[i + 1]);
				if (from != null && to != null) {
					g2d.drawLine(from.x, from.y, to.x, to.y);
				}
			}
			for (int i = 0; i < outerLine.length - 2; i++) {
				Point from = MapPoints.get(outerLine[i]);
				Point to = MapPoints.get(outerLine[i + 2]);
				if (from != null && to != null) {
					g2d.drawLine(from.x, from.y, to.x, to.y);
				}
			}
			for (Map.Entry<Integer, Point> entry : MapPoints.entrySet()) {
				Point pt = entry.getValue();
				int index = entry.getKey();
				if (index == 24 || index % 5 == 0 && index <= 15) {
					g2d.setColor(Color.WHITE);
					g2d.fillOval(pt.x - 30, pt.y - 30, 60, 60);
					g2d.setColor(Color.BLACK);
					g2d.drawOval(pt.x - 30, pt.y - 30, 60, 60);
				}
				g2d.setColor(Color.WHITE);
				g2d.fillOval(pt.x - 25, pt.y - 25, 50, 50);
				g2d.setColor(Color.BLACK);
				g2d.drawOval(pt.x - 25, pt.y - 25, 50, 50);
			}
		}

		// 칸 그리기
		if (MapPoints == pentagonMapPoints) {
			// 테두리 선 연결
			int[] outerLine = { 0, 5, 10, 15, 20, 0 }; // 오각형 꼭짓점 연결
			for (int i = 0; i < outerLine.length - 1; i++) {
				Point from = MapPoints.get(outerLine[i]);
				Point to = MapPoints.get(outerLine[i + 1]);
				if (from != null && to != null) {
					g2d.drawLine(from.x, from.y, to.x, to.y);
				}
			}
			for (int i = 0; i <= 4; i++) {
				g2d.drawLine(MapPoints.get(31).x, MapPoints.get(31).y, MapPoints.get(i * 5).x, MapPoints.get(i * 5).y);
			}

			for (Map.Entry<Integer, Point> entry : MapPoints.entrySet()) {
				Point pt = entry.getValue();
				int index = entry.getKey();
				if (index == 31 || index % 5 == 0 && index <= 20) {
					g2d.setColor(Color.WHITE);
					g2d.fillOval(pt.x - 30, pt.y - 30, 60, 60);
					g2d.setColor(Color.BLACK);
					g2d.drawOval(pt.x - 30, pt.y - 30, 60, 60);
				}
				g2d.setColor(Color.WHITE);
				g2d.fillOval(pt.x - 25, pt.y - 25, 50, 50);
				g2d.setColor(Color.BLACK);
				g2d.drawOval(pt.x - 25, pt.y - 25, 50, 50);
			}
		}

		// 칸 그리기
		if (MapPoints == hexagonMapPoints) {
			// 테두리 선 연결
			int[] outerLine = { 0, 5, 10, 15, 20, 25, 0 }; // 육각형 꼭짓점 연결
			for (int i = 0; i < outerLine.length - 1; i++) {
				Point from = MapPoints.get(outerLine[i]);
				Point to = MapPoints.get(outerLine[i + 1]);
				if (from != null && to != null) {
					g2d.drawLine(from.x, from.y, to.x, to.y);
				}
			}
			for (int i = 0; i <= 5; i++) {
				g2d.drawLine(MapPoints.get(38).x, MapPoints.get(38).y, MapPoints.get(i * 5).x, MapPoints.get(i * 5).y);
			}
			
			for (Map.Entry<Integer, Point> entry : MapPoints.entrySet()) {
				Point pt = entry.getValue();
				int index = entry.getKey();
				if (index == 38 || index % 5 == 0 && index <= 25) {
					g2d.setColor(Color.WHITE);
					g2d.fillOval(pt.x - 30, pt.y - 30, 60, 60);
					g2d.setColor(Color.BLACK);
					g2d.drawOval(pt.x - 30, pt.y - 30, 60, 60);
				}
				g2d.setColor(Color.WHITE);
				g2d.fillOval(pt.x - 25, pt.y - 25, 50, 50);
				g2d.setColor(Color.BLACK);
				g2d.drawOval(pt.x - 25, pt.y - 25, 50, 50);
			}
		}

		// 출발 표시
		Point start = MapPoints.get(0);
		g2d.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		g2d.drawString("출발", start.x - 20, start.y + 10);

		// 말 그리기
		if (game != null && game.players != null) {

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
					if (loc >= 0 && MapPoints.containsKey(loc)) {
						Point p = MapPoints.get(loc);

						// 말 원
						g2d.setColor(color);
						g2d.fillOval(p.x - 25, p.y - 25, 50, 50);
						g2d.setColor(Color.BLACK);
						g2d.drawOval(p.x - 25, p.y - 25, 50, 50);

						// 업기 개수 표시
						int count = countMap.getOrDefault(loc, 0);
						if (count > 1) {
							g2d.setColor(Color.WHITE);
							g2d.fillOval(p.x + 10, p.y - 20, 16, 16);
							g2d.setColor(Color.BLACK);
							g2d.setFont(new Font("Arial", Font.BOLD, 12));
							g2d.drawString(String.valueOf(count), p.x + 14, p.y - 8);
						}
					}
				}

				// ✔ 골인 가능 텍스트 표시
				if (movePreview.contains(-1)) {
					Player currentPlayer = game.players.get(game.getCurrentPlayerIndex());
					if (selectedPieceIndex >= 0 && selectedPieceIndex < currentPlayer.getPieces().size()) {
						Piece piece = currentPlayer.getPieces().get(selectedPieceIndex);
						int loc = piece.getLocation();
						if (loc >= 0 && MapPoints.containsKey(loc)) {
							Point pt = MapPoints.get(loc);
							g2d.setColor(Color.BLACK);
							g2d.setFont(new Font("맑은 고딕", Font.BOLD, 14));
							g2d.drawString("골인", pt.x - 20, pt.y - 35); // 텍스트 위치 조정
						}
					}
				}
			}
		}

		// 🔸 목적지 미리보기
		g2d.setColor(new Color(100, 100, 100, 100)); // 반투명 회색
		for (Integer dest : movePreview) {
			if (MapPoints.containsKey(dest)) {
				Point pt = MapPoints.get(dest);
				g2d.fillOval(pt.x - 25, pt.y - 25, 50, 50); // 미리보기 원
				if (dest == -1) {
					// 골인 위치를 예: 화면 우측 하단 등 고정 좌표에 그리기
					Point goalPoint = new Point(900, 900); // 원하는 위치로 조정
					g2d.fillOval(goalPoint.x - 25, goalPoint.y - 25, 50, 50);
					g2d.drawOval(goalPoint.x - 25, goalPoint.y - 25, 50, 50);
					g2d.drawString("골", goalPoint.x - 8, goalPoint.y + 4);
				}
			}
		}
	}
}

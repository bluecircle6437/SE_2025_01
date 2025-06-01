package ui;

import javax.swing.JFrame;
//GameUI.java - 자바 스윙 기반 윷놀이판 UI 구성 (출발점, 원형 말판 포함)
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameUI extends JFrame {
	private JPanel boardPanel;
	private JButton randomThrowButton;
	private JButton customThrowButton;
	private JComboBox<String> yutSelector;
	private JComboBox<String> pieceSelector;

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

		// 상단 컨트롤 패널
		JPanel controlPanel = new JPanel();
		randomThrowButton = new JButton("랜덤 윷 던지기");
		customThrowButton = new JButton("지정 윷 던지기");
		yutSelector = new JComboBox<>(new String[] { "빽도", "도", "개", "걸", "윷", "모" });
		String[] pieces = new String[config.pieceCount];
		for (int i = 0; i < config.pieceCount; i++) {
			pieces[i] = "말 " + (i + 1);
		}
		pieceSelector = new JComboBox<>(pieces);

		controlPanel.add(new JLabel("말 선택:"));
		controlPanel.add(pieceSelector);
		controlPanel.add(new JLabel("윷 선택:"));
		controlPanel.add(yutSelector);
		controlPanel.add(randomThrowButton);
		controlPanel.add(customThrowButton);

		add(controlPanel, BorderLayout.NORTH);

		// 윷놀이판 패널
		boardPanel = new JPanel() {
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				switch (config.boardShape) {
				case "사각형":
					drawSquareBoard(g);
					break;
				case "오각형":
					drawPentagonBoard(g);
					break;
				case "육각형":
					drawHexagonBoard(g);
					break;
				}
			}
		};
		boardPanel.setBackground(Color.WHITE);
		add(boardPanel, BorderLayout.CENTER);

		// 하단 버튼
		JPanel bottomPanel = new JPanel();
		JButton restartButton = new JButton("게임 재시작");
		JButton exitButton = new JButton("게임 종료");
		bottomPanel.add(restartButton);
		bottomPanel.add(exitButton);
		add(bottomPanel, BorderLayout.SOUTH);

		// 예시 이벤트 연결
		randomThrowButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "윷 던지기!");
			}
		});

		customThrowButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String result = (String) yutSelector.getSelectedItem();
				JOptionPane.showMessageDialog(null, result + " 결과로 이동합니다.");
			}
		});
		
		restartButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				SwingUtilities.invokeLater(() -> {
					new GameUI();
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

	private void drawSquareBoard(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setStroke(new BasicStroke(2));

		int size = 15; // 11x11 격자
		int spacing = 55;
		int offset = 55;

		// 격자 점 위치 배열로 그리기
		Point[][] points = new Point[size][size];

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				points[i][j] = new Point(offset + j * spacing, offset + i * spacing);
			}
		}

		// 십자 선

		// 외곽 사각형
		g2d.drawRect(offset, offset, spacing * (size - 1), spacing * (size - 1));

		// 대각선
		g2d.drawLine(points[0][0].x, points[0][0].y, points[14][14].x, points[14][14].y);
		g2d.drawLine(points[0][14].x, points[0][14].y, points[14][0].x, points[14][0].y);

		// 모서리 원
		for (int i = 0; i < 15; i += 14) {
			g2d.setColor(Color.WHITE);
			g2d.fillOval(points[i][i].x - 50, points[i][i].y - 50, 100, 100);
			g2d.fillOval(points[i][14 - i].x - 50, points[i][14 - i].y - 50, 100, 100);
			g2d.setColor(Color.BLACK);
			g2d.drawOval(points[i][i].x - 50, points[i][i].y - 50, 100, 100);
			g2d.drawOval(points[i][i].x - 40, points[i][i].y - 40, 80, 80);
			g2d.drawOval(points[i][14 - i].x - 50, points[i][14 - i].y - 50, 100, 100);
			g2d.drawOval(points[i][14 - i].x - 40, points[i][14 - i].y - 40, 80, 80);

		}

		// 외곽 원
		for (int i = 2; i < 14; i += 3) {
			g2d.setColor(Color.WHITE);
			g2d.fillOval(points[0][i].x - 15, points[0][i].y - 40, 80, 80);
			g2d.setColor(Color.BLACK);
			g2d.drawOval(points[0][i].x - 15, points[0][i].y - 40, 80, 80);

			// 하단
			g2d.setColor(Color.WHITE);
			g2d.fillOval(points[14][i].x - 15, points[14][i].y - 40, 80, 80);
			g2d.setColor(Color.BLACK);
			g2d.drawOval(points[14][i].x - 15, points[14][i].y - 40, 80, 80);

			// 좌측
			g2d.setColor(Color.WHITE);
			g2d.fillOval(points[i][0].x - 40, points[i][0].y - 15, 80, 80);
			g2d.setColor(Color.BLACK);
			g2d.drawOval(points[i][0].x - 40, points[i][0].y - 15, 80, 80);

			// 우측
			g2d.setColor(Color.WHITE);
			g2d.fillOval(points[i][14].x - 40, points[i][14].y - 15, 80, 80);
			g2d.setColor(Color.BLACK);
			g2d.drawOval(points[i][14].x - 40, points[i][14].y - 15, 80, 80);
		}

		// 대각선 원
		for (int i = 2; i < 6; i += 2) {
			g2d.setColor(Color.WHITE);
			g2d.fillOval(points[i][i].x - 10, points[i][i].y - 10, 80, 80);
			g2d.fillOval(points[i][13 - i].x - 10, points[i][13 - i].y - 10, 80, 80);
			g2d.fillOval(points[13 - i][i].x - 10, points[13 - i][i].y - 10, 80, 80);
			g2d.fillOval(points[13 - i][13 - i].x - 10, points[13 - i][13 - i].y - 10, 80, 80);
			g2d.setColor(Color.BLACK);
			g2d.drawOval(points[i][i].x - 10, points[i][i].y - 10, 80, 80);
			g2d.drawOval(points[i][13 - i].x - 10, points[i][13 - i].y - 10, 80, 80);
			g2d.drawOval(points[13 - i][i].x - 10, points[13 - i][i].y - 10, 80, 80);
			g2d.drawOval(points[13 - i][13 - i].x - 10, points[13 - i][13 - i].y - 10, 80, 80);

		}

		// 중앙 원
		g2d.setColor(Color.WHITE);
		g2d.fillOval(points[7][7].x - 60, points[7][7].y - 60, 120, 120);
		g2d.setColor(Color.BLACK);
		g2d.drawOval(points[7][7].x - 60, points[7][7].y - 60, 120, 120);
		g2d.drawOval(points[7][7].x - 50, points[7][7].y - 50, 100, 100);

		// 출발 표시
		g2d.setFont(new Font("맑은 고딕", Font.BOLD, 40)); // 글꼴명, 스타일, 크기
		g2d.drawString("출발", points[14][14].x - 40, points[14][14].y + 15);
	}

	private void drawPentagonBoard(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setStroke(new BasicStroke(2));

		int size = 15; // 11x11 격자
		int spacing = 55;
		int offset = 55;

		// 격자 점 위치 배열로 그리기
		Point[][] points = new Point[size][size];

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				points[i][j] = new Point(offset + j * spacing, offset + i * spacing);
			}
		}

		// 십자 선

		// 외곽 사각형
		g2d.drawRect(offset, offset, spacing * (size - 1), spacing * (size - 1));

		// 대각선
		g2d.drawLine(points[0][0].x, points[0][0].y, points[14][14].x, points[14][14].y);
		g2d.drawLine(points[0][14].x, points[0][14].y, points[14][0].x, points[14][0].y);

		// 모서리 원
		for (int i = 0; i < 15; i += 14) {
			g2d.setColor(Color.WHITE);
			g2d.fillOval(points[i][i].x - 50, points[i][i].y - 50, 100, 100);
			g2d.fillOval(points[i][14 - i].x - 50, points[i][14 - i].y - 50, 100, 100);
			g2d.setColor(Color.BLACK);
			g2d.drawOval(points[i][i].x - 50, points[i][i].y - 50, 100, 100);
			g2d.drawOval(points[i][i].x - 40, points[i][i].y - 40, 80, 80);
			g2d.drawOval(points[i][14 - i].x - 50, points[i][14 - i].y - 50, 100, 100);
			g2d.drawOval(points[i][14 - i].x - 40, points[i][14 - i].y - 40, 80, 80);

		}

		// 외곽 원
		for (int i = 2; i < 14; i += 3) {
			g2d.setColor(Color.WHITE);
			g2d.fillOval(points[0][i].x - 15, points[0][i].y - 40, 80, 80);
			g2d.setColor(Color.BLACK);
			g2d.drawOval(points[0][i].x - 15, points[0][i].y - 40, 80, 80);

			// 하단
			g2d.setColor(Color.WHITE);
			g2d.fillOval(points[14][i].x - 15, points[14][i].y - 40, 80, 80);
			g2d.setColor(Color.BLACK);
			g2d.drawOval(points[14][i].x - 15, points[14][i].y - 40, 80, 80);

			// 좌측
			g2d.setColor(Color.WHITE);
			g2d.fillOval(points[i][0].x - 40, points[i][0].y - 15, 80, 80);
			g2d.setColor(Color.BLACK);
			g2d.drawOval(points[i][0].x - 40, points[i][0].y - 15, 80, 80);

			// 우측
			g2d.setColor(Color.WHITE);
			g2d.fillOval(points[i][14].x - 40, points[i][14].y - 15, 80, 80);
			g2d.setColor(Color.BLACK);
			g2d.drawOval(points[i][14].x - 40, points[i][14].y - 15, 80, 80);
		}

		// 대각선 원
		for (int i = 2; i < 6; i += 2) {
			g2d.setColor(Color.WHITE);
			g2d.fillOval(points[i][i].x - 10, points[i][i].y - 10, 80, 80);
			g2d.fillOval(points[i][13 - i].x - 10, points[i][13 - i].y - 10, 80, 80);
			g2d.fillOval(points[13 - i][i].x - 10, points[13 - i][i].y - 10, 80, 80);
			g2d.fillOval(points[13 - i][13 - i].x - 10, points[13 - i][13 - i].y - 10, 80, 80);
			g2d.setColor(Color.BLACK);
			g2d.drawOval(points[i][i].x - 10, points[i][i].y - 10, 80, 80);
			g2d.drawOval(points[i][13 - i].x - 10, points[i][13 - i].y - 10, 80, 80);
			g2d.drawOval(points[13 - i][i].x - 10, points[13 - i][i].y - 10, 80, 80);
			g2d.drawOval(points[13 - i][13 - i].x - 10, points[13 - i][13 - i].y - 10, 80, 80);

		}

		// 중앙 원
		g2d.setColor(Color.WHITE);
		g2d.fillOval(points[7][7].x - 60, points[7][7].y - 60, 120, 120);
		g2d.setColor(Color.BLACK);
		g2d.drawOval(points[7][7].x - 60, points[7][7].y - 60, 120, 120);
		g2d.drawOval(points[7][7].x - 50, points[7][7].y - 50, 100, 100);

		// 출발 표시
		g2d.setFont(new Font("맑은 고딕", Font.BOLD, 40)); // 글꼴명, 스타일, 크기
		g2d.drawString("출발", points[14][14].x - 40, points[14][14].y + 15);
	}

	private void drawHexagonBoard(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setStroke(new BasicStroke(2));

		int size = 15; // 11x11 격자
		int spacing = 55;
		int offset = 55;

		// 격자 점 위치 배열로 그리기
		Point[][] points = new Point[size][size];

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				points[i][j] = new Point(offset + j * spacing, offset + i * spacing);
			}
		}

		// 십자 선

		// 외곽 사각형
		g2d.drawRect(offset, offset, spacing * (size - 1), spacing * (size - 1));

		// 대각선
		g2d.drawLine(points[0][0].x, points[0][0].y, points[14][14].x, points[14][14].y);
		g2d.drawLine(points[0][14].x, points[0][14].y, points[14][0].x, points[14][0].y);

		// 모서리 원
		for (int i = 0; i < 15; i += 14) {
			g2d.setColor(Color.WHITE);
			g2d.fillOval(points[i][i].x - 50, points[i][i].y - 50, 100, 100);
			g2d.fillOval(points[i][14 - i].x - 50, points[i][14 - i].y - 50, 100, 100);
			g2d.setColor(Color.BLACK);
			g2d.drawOval(points[i][i].x - 50, points[i][i].y - 50, 100, 100);
			g2d.drawOval(points[i][i].x - 40, points[i][i].y - 40, 80, 80);
			g2d.drawOval(points[i][14 - i].x - 50, points[i][14 - i].y - 50, 100, 100);
			g2d.drawOval(points[i][14 - i].x - 40, points[i][14 - i].y - 40, 80, 80);

		}

		// 외곽 원
		for (int i = 2; i < 14; i += 3) {
			g2d.setColor(Color.WHITE);
			g2d.fillOval(points[0][i].x - 15, points[0][i].y - 40, 80, 80);
			g2d.setColor(Color.BLACK);
			g2d.drawOval(points[0][i].x - 15, points[0][i].y - 40, 80, 80);

			// 하단
			g2d.setColor(Color.WHITE);
			g2d.fillOval(points[14][i].x - 15, points[14][i].y - 40, 80, 80);
			g2d.setColor(Color.BLACK);
			g2d.drawOval(points[14][i].x - 15, points[14][i].y - 40, 80, 80);

			// 좌측
			g2d.setColor(Color.WHITE);
			g2d.fillOval(points[i][0].x - 40, points[i][0].y - 15, 80, 80);
			g2d.setColor(Color.BLACK);
			g2d.drawOval(points[i][0].x - 40, points[i][0].y - 15, 80, 80);

			// 우측
			g2d.setColor(Color.WHITE);
			g2d.fillOval(points[i][14].x - 40, points[i][14].y - 15, 80, 80);
			g2d.setColor(Color.BLACK);
			g2d.drawOval(points[i][14].x - 40, points[i][14].y - 15, 80, 80);
		}

		// 대각선 원
		for (int i = 2; i < 6; i += 2) {
			g2d.setColor(Color.WHITE);
			g2d.fillOval(points[i][i].x - 10, points[i][i].y - 10, 80, 80);
			g2d.fillOval(points[i][13 - i].x - 10, points[i][13 - i].y - 10, 80, 80);
			g2d.fillOval(points[13 - i][i].x - 10, points[13 - i][i].y - 10, 80, 80);
			g2d.fillOval(points[13 - i][13 - i].x - 10, points[13 - i][13 - i].y - 10, 80, 80);
			g2d.setColor(Color.BLACK);
			g2d.drawOval(points[i][i].x - 10, points[i][i].y - 10, 80, 80);
			g2d.drawOval(points[i][13 - i].x - 10, points[i][13 - i].y - 10, 80, 80);
			g2d.drawOval(points[13 - i][i].x - 10, points[13 - i][i].y - 10, 80, 80);
			g2d.drawOval(points[13 - i][13 - i].x - 10, points[13 - i][13 - i].y - 10, 80, 80);

		}

		// 중앙 원
		g2d.setColor(Color.WHITE);
		g2d.fillOval(points[7][7].x - 60, points[7][7].y - 60, 120, 120);
		g2d.setColor(Color.BLACK);
		g2d.drawOval(points[7][7].x - 60, points[7][7].y - 60, 120, 120);
		g2d.drawOval(points[7][7].x - 50, points[7][7].y - 50, 100, 100);

		// 출발 표시
		g2d.setFont(new Font("맑은 고딕", Font.BOLD, 40)); // 글꼴명, 스타일, 크기
		g2d.drawString("출발", points[14][14].x - 40, points[14][14].y + 15);
	}
}

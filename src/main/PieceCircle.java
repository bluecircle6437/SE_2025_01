package main;

import javax.swing.*;
import java.awt.*;


class PieceCircle extends JComponent {
	private boolean blinking = false;
	private Timer blinkTimer;
	private boolean isVisible = true;
	private Color originalColor;
	private Color currentColor;

	public PieceCircle(Color color) {
		this.originalColor = color;
		this.currentColor = color;
		setPreferredSize(new Dimension(40, 40)); // 기본 말 크기
		setOpaque(false); // 배경 투명
	}
	
	public void setColor(Color color) {
		this.currentColor = color;
		repaint();
	}

	public void resetColor() {
		this.currentColor = originalColor;
		repaint();
	}


	public void startBlinking() {
		if (blinkTimer != null && blinkTimer.isRunning()) return;

		blinkTimer = new Timer(300, e -> {
			isVisible = !isVisible;
			repaint();
		});
		blinkTimer.start();
	}

	public void stopBlinking() {
		if (blinkTimer != null) {
			blinkTimer.stop();
			isVisible = true;
			repaint();
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (!isVisible) return;

		Graphics2D g2d = (Graphics2D) g.create();

		// 원형 유지: 정사각형 영역 만들기
		int size = Math.min(getWidth(), getHeight());
		int x = (getWidth() - size) / 2;
		int y = (getHeight() - size) / 2;

		// 깜빡이면 테두리 밝게, 아니면 기본 색
		g2d.setColor(currentColor);
		g2d.fillOval(x, y, size, size);

		g2d.setColor(Color.BLACK);
		g2d.drawOval(x, y, size, size);

		g2d.dispose();
	}
}
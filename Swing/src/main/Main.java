package main;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // UI는 이벤트 디스패치 스레드에서 실행해야 합니다.
        SwingUtilities.invokeLater(() -> {
            GameUI gameUI = new GameUI();
            gameUI.setVisible(true);
        });
    }
}

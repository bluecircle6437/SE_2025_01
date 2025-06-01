package main;

import junit.framework.*;
import org.junit.Test;

public class GameTest extends TestCase {

    @Test public void test1() {
        //플레이어 1,2, 말 2개, 맵 사각형 구성
        Game game = new Game();
        GameConfig config = new GameConfig(2, 2, "사각형");
        game.initialize(config);
        //구성 체크
        assertEquals(2, game.players.size());
        assertEquals(2, game.maxPieceCount);
        assertEquals(29, game.map.length);

        //1번 모 개
        game.throwYutSelect(5);
        game.throwYutSelect(2);
        game.checkZeroBack();
        game.makeTurn(0, 0);
        game.makeTurn(1, 0);
        //위치 체크
        assertEquals(5, game.players.get(0).pieces.get(0).getLocation());
        assertEquals(2, game.players.get(0).pieces.get(1).getLocation());

        //2번 개 잡고 개
        game.throwYutSelect(2);
        game.checkZeroBack();
        game.makeTurn(0, 0);
        game.throwYutSelect(2);
        game.checkZeroBack();
        game.makeTurn(0, 0);
        //1번 2번 위치 체크
        assertEquals(-1, game.players.get(0).pieces.get(1).getLocation());
        assertEquals(4, game.players.get(1).pieces.get(0).getLocation());

        //1번 모 걸 업고 중앙으로
        game.throwYutSelect(5);
        game.throwYutSelect(3);
        game.checkZeroBack();
        game.makeTurn(1, 0);
        game.makeTurn(0, 0);
        //1번 업기 체크
        assertEquals(24, game.players.get(0).pieces.get(0).getLocation());
        assertEquals(24, game.players.get(0).pieces.get(1).getLocation());

        //2번 윷 개 왼쪽 위로
        game.throwYutSelect(4);
        game.throwYutSelect(2);
        game.checkZeroBack();
        game.makeTurn(0, 0);
        game.makeTurn(0, 0);
        //2번 위치 체크
        assertEquals(10, game.players.get(1).pieces.get(0).getLocation());

        //1번 도
        game.throwYutSelect(1);
        game.checkZeroBack();
        game.makeTurn(0, 0);
        //2번 걸 중앙으로
        game.throwYutSelect(3);
        game.checkZeroBack();
        game.makeTurn(0, 0);
        //1번 빽도 잡고 걸
        game.throwYutSelect(-1);
        game.checkZeroBack();
        game.makeTurn(0, 0);
        game.throwYutSelect(3);
        game.checkZeroBack();
        game.makeTurn(0, 0);
        //1번 2번 위치 체크
        assertEquals(0, game.players.get(0).pieces.get(0).getLocation());
        assertEquals(0, game.players.get(0).pieces.get(1).getLocation());
        assertEquals(-1, game.players.get(1).pieces.get(0).getLocation());

        //2번 빽도 움직임 봉쇄
        game.throwYutSelect(-1);
        game.checkZeroBack();
        //2번 봉쇄되었는지 체크
        assertEquals(-1, game.players.get(1).pieces.get(0).getLocation());

        //1번 골인
        game.throwYutSelect(1);
        game.checkZeroBack();
        game.makeTurn(0, 0);
        //1번 골인 체크
        assertEquals(1, game.finishPlayers.size());
    }
}

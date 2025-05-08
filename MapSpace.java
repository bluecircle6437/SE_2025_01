import java.util.Arrays;

public class MapSpace {
    int length; //맵 전체 길이
    int fartest; //가장 멀리 돌아가는 루트의 마지막 번호
    int[] centerRoute; //맵 중앙점 착지 시 루트
    int[] secondRoute; //맵 중앙점 지나칠 시 이후 루트
    int[][] innerRoute; //내부를 통하는 루트

    public MapSpace(int type) {
        switch (type) {
            case 4:
                setSquareMap();
                break;
            case 5:
                setPentagonMap();
                break;
            case 6:
                setHexagonMap();
                break;
            default:
                //error
                break;
        }
    }

    //판 생성 내부 함수
    //번호 순서는 바깥 루트-중앙점에서 1, 2번째로 가까운 루트를 제외한 나머지 루트-중앙점-중앙점에서 2번째로 가까운 루트-가장 가까운 루트
    private void setSquareMap() {
        length = 29;
        fartest = 19;
        centerRoute = new int[]{24, 27, 28, 0};
        secondRoute = new int[]{24, 25, 26, 15};
        innerRoute = new int[][]{{5, 20, 21, 24, 25, 26, 15}, {10, 22, 23, 24, 27, 28, 0}};
    }

    private void setPentagonMap() {
        length = 36;
        fartest = 24;
        centerRoute = new int[]{31, 34, 35, 0};
        secondRoute = new int[]{31, 32, 33, 20};
        innerRoute = new int[][]{{5, 25, 26, 31, 32, 33, 20}, {10, 27, 28, 31, 32, 33, 20}, {15, 29, 30, 31, 34, 35, 0}};
        //15~31~20 루트의 경우 중점을 안 거치는 것보다 느림
    }

    private void setHexagonMap() {
        length = 43;
        fartest = 29;
        centerRoute = new int[]{38, 41, 42, 0};
        secondRoute = new int[]{38, 39, 40, 25};
        innerRoute = new int[][]{{5, 30, 31, 38, 39, 40, 25}, {10, 32, 33, 38, 39, 40, 25}, {15, 34, 35, 38, 39, 40, 25}, {20, 36, 37, 38, 41, 42, 0}};
        //20~38~25 루트의 경우 중점을 안 거치는 것보다 느림
    }

    //시작점+전진 칸 입력 시 도착점을 반환하는 함수(골인은 -1)
    //새로 놓는 말의 경우 이 메소드를 호출하지 마시고 location에 칸 수를 바로 넣어주세요
    @SuppressWarnings("unlikely-arg-type")
    public int getDestination(int start, int toGo) {
        //0번 칸 특수 처리
        if (start == 0) {
            if (toGo == -1) return fartest;
            else return -1;
        }
        //1. 최장 루트를 벗어나지 않았는지 검사
        if (start < fartest) {
            //2. 안쪽 루트로 이을 수 있는지 검사
            for(int i = 0; i < innerRoute.length && toGo != -1; i++) {
                if (start == innerRoute[i][0]) return innerRoute[i][toGo];
            }
            //3. 최장 루트에서 골인했는지 검사
            int dest = start + toGo - fartest;
            if (dest >= 2) return -1;
            else if (dest == 1) return 0;
            else return start + toGo;
        }
        else { //일단 최장 루트 벗어남
            //4. 현 위치가 중앙점인지 검사
            if (start == centerRoute[0]) {
                if (toGo >= centerRoute.length) return -1;
                else return centerRoute[toGo];
            }
            //5. center, second 이외 지점인지 검사
            for(int i = 0; i < innerRoute.length && toGo != -1; i++) {
                if (start >= innerRoute[i][1] && start < innerRoute[i+1][1]) {
                    int index = Arrays.asList(innerRoute[i]).indexOf(start);
                    if (toGo + index >= innerRoute[i].length) {
                        if (innerRoute[i][innerRoute[i].length - 1] == 0) return -1;
                        else return innerRoute[i][innerRoute[i].length - 1] + toGo;
                    }
                    else return innerRoute[i][toGo + index];
                }
            }
            //6. center, second 검사
            if (start >= secondRoute[1] && start < centerRoute[1]) {
                int index = Arrays.asList(secondRoute).indexOf(start);
                if (toGo + index >= secondRoute.length) return secondRoute[secondRoute.length - 1] + toGo;
                else return secondRoute[toGo + index];
            }
            else {
                int index = Arrays.asList(centerRoute).indexOf(start);
                if (toGo + index >= centerRoute.length) return -1;
                else return centerRoute[toGo + index];
            }
        }
    }

    //이하 변수 정보 가져오는 메소드
    public int getLength() {
        int i = length;
        return i;
    }

    public int getFartest() {
        int i = fartest;
        return i;
    }
}

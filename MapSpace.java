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
        /*
         * 8   7   6   5   4
         * 9   17  x   16  3
         * 10  x   18  x   2
         * 11  19  x   20  1
         * 12  13  14  15  0
        */
        length = 20;
        fartest = 15;
        centerRoute = new int[]{18, 20, 0};
        secondRoute = new int[]{18, 19, 12};
        innerRoute = new int[][]{{4, 16, 18, 19, 12}, {8, 17, 18, 20, 0}};
    }

    private void setPentagonMap() {
        length = 25;
        fartest = 14;
        centerRoute = new int[]{21, 24, 25, 0};
        secondRoute = new int[]{21, 22, 23, 12};
        innerRoute = new int[][]{{3, 15, 16, 21, 22, 23, 12}, {6, 17, 18, 21, 22, 23, 12}, {9, 19, 20, 21, 24, 25, 0}};
        //9~21~12 루트의 경우 중점을 안 거치는 것보다 느림
    }

    private void setHexagonMap() {
        length = 30;
        fartest = 17;
        centerRoute = new int[]{26, 29, 30, 0};
        secondRoute = new int[]{26, 27, 28, 15};
        innerRoute = new int[][]{{3, 18, 19, 26, 27, 28, 15}, {6, 20, 21, 26, 27, 28, 15}, {9, 22, 23, 26, 27, 28, 15}, {12, 24, 25, 26, 29, 30, 0}};
        //12~26~15 루트의 경우 중점을 안 거치는 것보다 느림
    }

    //시작점+전진 칸 입력 시 도착점을 반환하는 함수(골인은 -1)
    //새로 놓는 말의 경우 이 메소드를 호출하지 마시고 location에 칸 수를 바로 넣어주세요
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
                if (start == innerRoute[i][0]) {
                    if (toGo >= innerRoute[i].length) return -1;
                    else return innerRoute[i][toGo];
                }
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
                    int index = start - innerRoute[i][1] + 1;
                    if (toGo + index >= innerRoute[i].length) {
                        if (innerRoute[i][innerRoute[i].length - 1] == 0) return -1;
                        else return innerRoute[i][innerRoute[i].length - 1] + toGo;
                    }
                    else return innerRoute[i][toGo + index];
                }
            }
            //6. center, second 검사
            if (start >= secondRoute[1] && start < centerRoute[1]) {
                int index = start - secondRoute[1] + 1;
                if (toGo + index >= secondRoute.length) return secondRoute[secondRoute.length - 1] + toGo;
                else return secondRoute[toGo + index];
            }
            else {
                int index = start - centerRoute[1] + 1;
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

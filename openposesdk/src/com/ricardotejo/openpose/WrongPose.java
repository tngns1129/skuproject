package com.ricardotejo.openpose;

public class WrongPose {

    private int neckx = 0;
    private int necky = 0;
    private int earRx = 0;
    private int earRy = 0;
    private int earLx = 0;
    private int earLy = 0;
    private int hipRx = 0;
    private int hipRy = 0;
    private int hipLx = 0;
    private int hipLy = 0;
    private int nosex = 0;
    private int nosey = 0;

    public int getBornWrong(int index){
        int i;
        i = 6;

        if(index == 12) {
            if(earRx == 0 && earLx == 0){
                if(Math.abs(nosex - neckx) > 20)
                    i = 0; //잘못된 자세
            }
            else if(earRx==0 || earLx==0){
                if(earRx < earLx)
                    earRx = earLx;
                else
                    earLx = earRx;
                if(Math.abs((earRx + earLx)/2 - neckx) > 20)
                    i = 0; //잘못된 자세
            }
            else{
                if(Math.abs((earRx + earLx)/2 - neckx) > 20)
                    i = 0; //잘못된 자세
            }

        }

        if(index == 6 || index==9) {
            if(hipRx==0 || hipLx==0){
                if(hipRx < hipLx)
                    hipRx = hipLx;
                else
                    hipLx = hipRx;
            }
            if(Math.abs((hipRx + hipLx)/2 - neckx) > 20)
                i = 0; //잘못된 자세
        }

        return i;
    }

    public void setBorn(int index, int x, int y) {
        //목 좌표값
        if(index == 1){
            neckx = x;
            necky = y;
        }
        //왼쪽 귀
        if(index == 17){
            earLx = x;
            earLy = y;
        }
        //오른쪽 귀
        if(index == 16){
            earRx = x;
            earRy = y;
        }
        //왼쪽 엉덩이
        if(index == 11){
            hipLx = x;
            hipLy = y;
        }
        //오른쪽 엉덩이
        if(index == 8){
            hipRx = x;
            hipRy = y;
        }
    }

    public void poseReset(){
        neckx = 0;
        necky = 0;
        earRx = 0;
        earRy = 0;
        earLx = 0;
        earLy = 0;
        hipRx = 0;
        hipRy = 0;
        hipLx = 0;
        hipLy = 0;
    }
}

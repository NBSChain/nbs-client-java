package io.nbs.client.ui;

public enum ScreenSize {

    Low,Mid,Big;

    public static ScreenSize convertSize(int w,int h){
        int compareSize = w > h ? h : w;

        if(compareSize > 900 ){
            return ScreenSize.Mid;
        }else {
            return ScreenSize.Low;
        }
    }
}

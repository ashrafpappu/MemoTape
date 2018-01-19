package com.example.pappu.memotape.Frame;

import com.example.pappu.memotape.R;

import java.util.ArrayList;





public class FrameDtoCollection {

    private ArrayList<FrameItem> frameItemArrayList;

    public static int[] frameIconArrayList = {
            R.drawable.frame001,
            R.drawable.frame001,
            R.drawable.frame001,
            R.drawable.frame001,
            R.drawable.frame001,
            R.drawable.frame001
    };
    private  String[] framexmls = {
            "flower",
            "flowerfall",
            "genaricframe",
            "colum",
            "blueflower",
            "flowerframe"
    };

    public  void loadFrametem() {

        frameItemArrayList = new ArrayList<>();
        for(int i = 0; i < frameIconArrayList.length; i++) {
            frameItemArrayList.add(new FrameItem( frameIconArrayList[i],framexmls[i],i));
        }
    }

    public ArrayList<FrameItem> getMaskItemArrayList() {
        return frameItemArrayList;
    }

}

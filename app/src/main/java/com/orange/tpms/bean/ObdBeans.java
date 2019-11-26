package com.orange.tpms.bean;

import java.util.ArrayList;

public class ObdBeans {
    public int rowcount=6;
    public int idcount=8;
    public static int PROGRAM_WAIT=2;
    public static int PROGRAM_SUCCESS=0;
    public static int PROGRAM_FALSE=1;
    public ArrayList<String> OldSemsor=new ArrayList<>();
    public ArrayList<String> NewSensor=new ArrayList<>();
    public ArrayList<Integer> state=new ArrayList<>();
    public boolean CanEdit=true;
    public void add(String OldSemsor,String NewSensor,int state){
        this.OldSemsor.add(OldSemsor);
        this.NewSensor.add(NewSensor);
        this.state.add(state);
    }
    public void clear(){
        OldSemsor.clear();
        NewSensor.clear();
        state.clear();
    }
}

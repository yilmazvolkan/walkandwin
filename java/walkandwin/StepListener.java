package com.boun.volkanyilmaz.walkandwin;


// This interface will listen to alerts about steps being detected.
public interface StepListener {

    public void step(long timeNs);

}

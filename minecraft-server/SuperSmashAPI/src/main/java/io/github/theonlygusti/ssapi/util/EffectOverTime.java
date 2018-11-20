package io.github.theonlygusti.ssapi.util;

import io.github.theonlygusti.ssapi.SuperSmashKit;

public class EffectOverTime {
    public String name;
    public int timer;
    public Boolean removeOnTouchingGround;
    
    public int integerValue;
    public Double doubleValue;
    public Boolean booleanValue;
    public String stringValue;
    
    public SuperSmashKit appliedBy;


    public EffectOverTime() {
        name = "";
        timer = 0;
        removeOnTouchingGround = false;
        
        integerValue = 0;
        doubleValue = 0.0;
        booleanValue = false;
        stringValue = "";
        
        appliedBy = null;
    }
}
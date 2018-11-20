package io.github.theonlygusti.ssapi.util;

public class IntegerBoolean {
    public int IntegerValue;
    public Boolean BooleanValue;
	
	public IntegerBoolean(int InitIntValue, Boolean InitBoolValue) {
		IntegerValue = InitIntValue;
		BooleanValue = InitBoolValue;
	}

	public IntegerBoolean() {
		IntegerValue = 0;
		BooleanValue = true;
	}
}
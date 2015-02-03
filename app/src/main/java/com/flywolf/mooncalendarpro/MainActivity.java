package com.flywolf.mooncalendarpro;

import android.os.Bundle;

public class MainActivity extends com.flywolf.mooncalendar.MainActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		extra = true;
		super.onCreate(savedInstanceState);
	}
}

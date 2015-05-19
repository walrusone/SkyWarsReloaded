package com.walrusone.skywars.commands;

public class Cmd extends BaseCmd { 
	
	/**
	 * @author zRA1Nz
	 * Copy And Paste this class to make a new Blank Command
	 * Make sure to load the new command in the cmd manager
	 */
	
	
	
	public Cmd() {
		forcePlayer = true;
		cmdName = "";
		argLength = 2; //counting cmdName
		usage = "<>";
		desc = ":: ";

	}

	@Override
	public boolean run() {
		return true;
	}

}

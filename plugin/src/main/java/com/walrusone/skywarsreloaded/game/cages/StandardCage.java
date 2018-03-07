package com.walrusone.skywarsreloaded.game.cages;

import com.walrusone.skywarsreloaded.menus.gameoptions.objects.CoordLoc;

public class StandardCage extends Cage {

	public StandardCage() {
		cageType = CageType.STANDARD;
		coordOffsets.add(new CoordLoc(0, 0, 0));
		coordOffsets.add(new CoordLoc(0, 1, 1));
		coordOffsets.add(new CoordLoc(1, 1, 0));
		coordOffsets.add(new CoordLoc(-1, 1, 0));
		coordOffsets.add(new CoordLoc(0, 2, 1));
		coordOffsets.add(new CoordLoc(0, 2, -1));
		coordOffsets.add(new CoordLoc(1, 2, 0));
		coordOffsets.add(new CoordLoc(-1, 2, 0));
		coordOffsets.add(new CoordLoc(0, 3, 1));
		coordOffsets.add(new CoordLoc(0, 3, -1));
		coordOffsets.add(new CoordLoc(1, 3, 0));
		coordOffsets.add(new CoordLoc(-1, 3, 0));
		coordOffsets.add(new CoordLoc(0, 4, 0));
	}

}

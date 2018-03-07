package com.walrusone.skywarsreloaded.game.cages;

import com.walrusone.skywarsreloaded.menus.gameoptions.objects.CoordLoc;

public class CubeCage extends Cage {

	public CubeCage() {
		cageType = CageType.CUBE;
		coordOffsets.add(new CoordLoc(0, 0, 0));
		coordOffsets.add(new CoordLoc(0, 0, 1));
		coordOffsets.add(new CoordLoc(0, 0, -1));
		coordOffsets.add(new CoordLoc(1, 0, 0));
		coordOffsets.add(new CoordLoc(1, 0, 1));
		coordOffsets.add(new CoordLoc(1, 0, -1));
		coordOffsets.add(new CoordLoc(-1, 0, 0));
		coordOffsets.add(new CoordLoc(-1, 0, 1));
		coordOffsets.add(new CoordLoc(-1, 0, -1));	
		for (int i = 1; i < 4; i++) {
			coordOffsets.add(new CoordLoc(2, i, -1));
			coordOffsets.add(new CoordLoc(2, i, 0));
			coordOffsets.add(new CoordLoc(2, i, 1));
			coordOffsets.add(new CoordLoc(-2, i, -1));
			coordOffsets.add(new CoordLoc(-2, i, 0));
			coordOffsets.add(new CoordLoc(-2, i, 1));
			coordOffsets.add(new CoordLoc(-1, i, 2));
			coordOffsets.add(new CoordLoc(0, i, 2));
			coordOffsets.add(new CoordLoc(1, i, 2));
			coordOffsets.add(new CoordLoc(-1, i, -2));
			coordOffsets.add(new CoordLoc(0, i, -2));
			coordOffsets.add(new CoordLoc(1, i, -2));
		}
		coordOffsets.add(new CoordLoc(0, 4, 0));
		coordOffsets.add(new CoordLoc(0, 4, 1));
		coordOffsets.add(new CoordLoc(0, 4, -1));
		coordOffsets.add(new CoordLoc(1, 4, 0));
		coordOffsets.add(new CoordLoc(1, 4, 1));
		coordOffsets.add(new CoordLoc(1, 4, -1));
		coordOffsets.add(new CoordLoc(-1, 4, 0));
		coordOffsets.add(new CoordLoc(-1, 4, 1));
		coordOffsets.add(new CoordLoc(-1, 4, -1));
	}

}

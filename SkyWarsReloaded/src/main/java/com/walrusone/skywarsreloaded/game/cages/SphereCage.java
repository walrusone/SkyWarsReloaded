package com.walrusone.skywarsreloaded.game.cages;

import com.walrusone.skywarsreloaded.menus.gameoptions.objects.CoordLoc;

public class SphereCage extends Cage {

	public SphereCage() {
		cageType = CageType.SPHERE;
		coordOffsets.add(new CoordLoc(0, 0, 0));
		coordOffsets.add(new CoordLoc(1, 0, 0));
		coordOffsets.add(new CoordLoc(1, 0, 1));
		coordOffsets.add(new CoordLoc(0, 0, 1));
		
		coordOffsets.add(new CoordLoc(2, 1, 0));
		coordOffsets.add(new CoordLoc(2, 1, 1));
		coordOffsets.add(new CoordLoc(1, 1, 2));
		coordOffsets.add(new CoordLoc(0, 1, 2));
		coordOffsets.add(new CoordLoc(-1, 1, 0));
		coordOffsets.add(new CoordLoc(-1, 1, 1));
		coordOffsets.add(new CoordLoc(0, 1, -1));
		coordOffsets.add(new CoordLoc(1, 1, -1));
		
		coordOffsets.add(new CoordLoc(3, 2, 0));
		coordOffsets.add(new CoordLoc(3, 2, 1));
		coordOffsets.add(new CoordLoc(2, 2, 2));
		coordOffsets.add(new CoordLoc(1, 2, 3));
		coordOffsets.add(new CoordLoc(0, 2, 3));
		coordOffsets.add(new CoordLoc(-1, 2, 2));
		coordOffsets.add(new CoordLoc(-2, 2, 1));
		coordOffsets.add(new CoordLoc(-2, 2, 0));
		coordOffsets.add(new CoordLoc(2, 2, -1));
		coordOffsets.add(new CoordLoc(1, 2, -2));
		coordOffsets.add(new CoordLoc(0, 2, -2));
		coordOffsets.add(new CoordLoc(-1, 2, -1));
		
		coordOffsets.add(new CoordLoc(4, 3, 0));
		coordOffsets.add(new CoordLoc(4, 3, 1));
		coordOffsets.add(new CoordLoc(3, 3, 2));
		coordOffsets.add(new CoordLoc(2, 3, 3));
		coordOffsets.add(new CoordLoc(1, 3, 4));
		coordOffsets.add(new CoordLoc(0, 3, 4));
		coordOffsets.add(new CoordLoc(-1, 3, 3));
		coordOffsets.add(new CoordLoc(-2, 3, 2));
		coordOffsets.add(new CoordLoc(-3, 3, 1));
		coordOffsets.add(new CoordLoc(-3, 3, 0));
		coordOffsets.add(new CoordLoc(3, 3, -1));
		coordOffsets.add(new CoordLoc(2, 3, -2));
		coordOffsets.add(new CoordLoc(1, 3, -3));
		coordOffsets.add(new CoordLoc(0, 3, -3));
		coordOffsets.add(new CoordLoc(-1, 3, -2));
		coordOffsets.add(new CoordLoc(-2, 3, -1));
		
		coordOffsets.add(new CoordLoc(3, 4, 0));
		coordOffsets.add(new CoordLoc(3, 4, 1));
		coordOffsets.add(new CoordLoc(2, 4, 2));
		coordOffsets.add(new CoordLoc(1, 4, 3));
		coordOffsets.add(new CoordLoc(0, 4, 3));
		coordOffsets.add(new CoordLoc(-1, 4, 2));
		coordOffsets.add(new CoordLoc(-2, 4, 1));
		coordOffsets.add(new CoordLoc(-2, 4, 0));
		coordOffsets.add(new CoordLoc(2, 4, -1));
		coordOffsets.add(new CoordLoc(1, 4, -2));
		coordOffsets.add(new CoordLoc(0, 4, -2));
		coordOffsets.add(new CoordLoc(-1, 4, -1));
		
		coordOffsets.add(new CoordLoc(2, 5, 0));
		coordOffsets.add(new CoordLoc(2, 5, 1));
		coordOffsets.add(new CoordLoc(1, 5, 2));
		coordOffsets.add(new CoordLoc(0, 5, 2));
		coordOffsets.add(new CoordLoc(-1, 5, 0));
		coordOffsets.add(new CoordLoc(-1, 5, 1));
		coordOffsets.add(new CoordLoc(0, 5, -1));
		coordOffsets.add(new CoordLoc(1, 5, -1));
		
		coordOffsets.add(new CoordLoc(0, 6, 0));
		coordOffsets.add(new CoordLoc(1, 6, 0));
		coordOffsets.add(new CoordLoc(1, 6, 1));
		coordOffsets.add(new CoordLoc(0, 6, 1));
	}

}

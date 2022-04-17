package edu.ufl.cise.cs1.controllers;

import game.controllers.AttackerController;
import game.models.*;
import java.awt.*;
import java.util.List;

public final class StudentAttackerController implements AttackerController
{
	public void init(Game game) { } //Left empty purposefully


	public void shutdown(Game game) { } //Left empty purposefully


	public int update(Game game,long timeDue) {
		//Returns gator attacker
		Attacker msGator = game.getAttacker();

		//POWER NODE SECTION
		List<Node> pnodes = game.getPowerPillList();
		int farthestPowerDistance = Integer.MIN_VALUE;
		int closestPDistance = Integer.MAX_VALUE;
		Node farthestPPill = null;
		Node closestPPill = null;

		if (pnodes.size() > 0) { //to obtain farthest Power Pill Distance
			for (Node n : pnodes) {
				if (msGator.getPathTo(n).size() > farthestPowerDistance) {
					farthestPPill = msGator.getTargetNode(pnodes, false);
					farthestPowerDistance = msGator.getPathTo(farthestPPill).size();
				}
			}
		}

		if(pnodes.size() > 0) //to obtain closest Power Pill Distance
		{
			for (Node n : pnodes) {
				if(msGator.getPathTo(n).size() < closestPDistance)
				{
					closestPPill = msGator.getTargetNode(pnodes, true);
					closestPDistance = msGator.getPathTo(closestPPill).size();
				}
			}
		}


		//NORMAL PILL SECTION
		List<Node> normalPills = game.getPillList();
		int closestNormalPillDist = Integer.MAX_VALUE;
		Node closestNormalPill = null;

//		if (normalPills.size() > 0) {
//
//		}

		//DEFENDER SECTION
		List<Defender> deflist = game.getDefenders();
		List<Defender> defendersList = game.getDefenders();
		int totalVTime = 0;
		int totalLairTime = 0;
		int closestdefDistance = Integer.MAX_VALUE;  //for closest defender (requires max value of git)
		int closestVulnerableDist = Integer.MAX_VALUE;
		Defender closestDefender = null;

		for (Defender defs : defendersList) //finds closest defender
		{

			if (msGator.getLocation().getPathDistance(defs.getLocation()) < closestdefDistance) {
				if (msGator.getLocation().getPathDistance(defs.getLocation()) != -1)
					closestdefDistance = msGator.getLocation().getPathDistance(defs.getLocation());
			}
		}

		boolean vulnerableD = false;
		int vulnDefIndex = 0;
		int vulnerableDAmount = 0;

		for (int i = 0; i <= 3; i++) //to find if a ghost is vulnerable
		{
			if (game.getDefender(i).isVulnerable()) {
				vulnerableD = true;
				vulnerableDAmount++;
				vulnDefIndex = i;  //defender index that is vulnerable
			}
		}

		//For total vulnerable time of all defenders
		for (Defender defender : defendersList) {
			totalVTime += defender.getVulnerableTime();
		}

		//For total lair time of all lair defenders
		for (Defender defender : defendersList) {
			totalLairTime += defender.getLairTime();
		}


		//UPDATE LOOP
		int action = 1;

		if (vulnerableDAmount > 0){ // First and most important option is to go to ghost if there is 1 or more ghosts only

			action = msGator.getNextDir(game.getDefender(vulnDefIndex).getLocation(), true);

		}
//		if (totalVTime < 15){ 
//
//			action = msGator.getNextDir(game.getDefender(vulnDefIndex).getLocation(), true);
//
//		}
		else if(pnodes.size() > 0 && closestPDistance > 3) { //if power pills remain, go to the next closest one

			action = msGator.getNextDir(closestPPill, true);

		}
//		else if (pnodes.size() != 0) {
//			action = msGator.getNextDir(farthestPPill, true);
//		}
		else if(closestPDistance < 4 && closestdefDistance > 4) { //stall next to Power Pill to bait

			action = msGator.getReverse();

		} else if (pnodes.size() > 0 && closestdefDistance < 6) { //Start moving towards pill once defender is too close

			action = msGator.getNextDir(closestPPill, true);

		} else if(closestdefDistance < 6) { //if defender is too close, reverse direction

			action = msGator.getReverse();

		} else {

			action = msGator.getNextDir(goforNode(game.getPillList(), game),true); //just go for closest normal pills

		}

		return action; //returns the action depending on the situation

	}

	public static Node goforNode(List<Node> node, Game game) { //For use of telling gator to go to nearest node, suggested by TA
		int mindist = Integer.MAX_VALUE;
		int minIndex = 0;

		if (node.size() == 0) {
			return null;
		}
		for (int i = 0; i < node.size(); i++) {
			int currentdist = node.get(i).getPathDistance(game.getAttacker().getLocation());
			if (currentdist < mindist) {
				mindist = node.get(i).getPathDistance(game.getAttacker().getLocation());
				minIndex = i;
			}
		}
		return node.get(minIndex);
	}
}
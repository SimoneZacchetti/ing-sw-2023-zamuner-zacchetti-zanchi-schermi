package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.Random;

public class PersonalGoalCard {
    private ArrayList<PersonalGoal> goals;
    private int targetsReached;

    //serve mettere dei parametri nel costruttore?
    public PersonalGoalCard(ArrayList<PersonalGoal> goals) {
       this.targetsReached = 0;
       this.goals = goals;
    }

    public int calculatePoints(Player p) {
        return 0;
    }

}
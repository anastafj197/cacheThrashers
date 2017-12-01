
import java.util.*;
import java.io.*;

  
public class Environment {

  // ==== Private Fields ====
  private int HP;                  // our HP percentage
  private int oHP;                 // opponent's HP percentage
  
  private String HPS;              // our HP string
  private String oHPS;             // opponent's HP string
   
  private String name;             // species of the mon we are currently using 
  private String oName;            // species of the mon the opponent is using 
  
  private String status;           // status of our mon 
  private String oStatus;          // status of opponent mon 
  
  private String[] team;           // holds the names of out team 
  
  private int[] teamPP;            // holds the pp of our entire teams movelist 24 values

  private int currIndex;           // integer representation of the current mon in team 
  private int oCurrIndex;          // integer index of the mon 
  
  private int prevIndex;           // index of the pokemon we played last turn 
  private int oPrevIndex;          // index of the pokemon they played last turn 
  
  private int[] teamHP;            // array of the HP values of our team 
  private int[] oTeamHP;           // array of the HP values of thier team 
  
  private String[] teamStatus;     // array of all the status of our team 
  private String[] oTeamStatus;    // array of opponents' status
    
  public int totalReward;          // total reward this game
  

  private String[] state;          // has info needed to make an action
  private boolean[] validActions;  // which actions are valid
  private Automator a;
//   private NotAutomator a;
  
  
  // ==== Constructor ====
  public Environment(Automator a) throws FileNotFoundException, InterruptedException { 
    this.a = a;
  }
  
  
  // ==== Public Methods ====
  public void newGame() throws FileNotFoundException, InterruptedException {
    totalReward = 0;
    update();
    teamHP = new int[] {100, 100, 100, 100, 100, 100};
    oTeamHP = new int[] {100, 100, 100, 100, 100, 100};
    teamStatus = new String[] {"none", "none", "none", "none", "none", "none"};
    oTeamStatus = new String[] {"none", "none", "none", "none", "none", "none"};
    prevIndex = currIndex;
    oPrevIndex = oCurrIndex;
    teamPP = new int[] {24, 8, 16, 24, 
                        8, 24, 16, 8, 
                        16, 16, 32, 24, 
                        24, 32, 48, 32, 
                        16, 16, 8, 24, 
                        24, 16, 32, 16};
  }
  
  // Returns: array of valid actions 
  public boolean[] getValidActions() {
    
    validActions = new boolean[10]; // all false by default
    
    // validate swapping to all alive pokemon
    for (int i = 0; i < 6; i++) {
      if (!(teamHP[i] == 0)) {
        validActions[4 + i] = true;
      }
    }
    
    // validate all available moves (moves with PP left)
    for (int i = 0; i < 4; i++) {
      if (!(teamPP[4 * currIndex + i] == 0)) {
        validActions[i] = true;
      }
    }
    
    // invalidate using moves if we have fainted. just chose which poke to swap to
    if (HP == 0) {
      for (int i = 0; i < 4; i++)
        validActions[i] = false;
    }
    
    // invalidate swapping to current pokemon
    validActions[4 + currIndex] = false;
    
    return validActions;
  }
  
  // Returns: reward
  public int act(int action) throws FileNotFoundException, InterruptedException {
    
    if (action < 4) {
      a.move(action);
      teamPP[4 * currIndex + action]--;
    } else
      a.swap(action - 4);
    
    
    update();
    
    int damageDealt = oTeamHP[oCurrIndex] - oHP;
    System.out.println("e: damageDealt: " + damageDealt); 
    int damageTaken = teamHP[currIndex] - HP;
    System.out.println("e: damageTaken: " + damageTaken);
    
    
    String oStatus = "none";
    
    if (!oTeamStatus[oCurrIndex].equals(oStatus)) {
      status = oStatus;
    }
    
    String status = "none";
    
    if (!teamStatus[currIndex].equals(status)) {
      status = status;
    }
    
    int reward = damageDealt - damageTaken;
    
    System.out.println("e: oStatus: " + oStatus);
    // convert string statuses into reward values
    switch (oStatus) {
      case "TOX": reward += 30;
      case "BRN": reward += 20;
      case "FRZ": reward += 50;
      case "PAR": reward += 40;
      case "PSN": reward += 20;
      case "SLP": reward += 45;
    }
    
    System.out.println("e: status: " + status);
    switch (status) {
      case "TOX": reward -= 30;
      case "BRN": reward -= 20;
      case "FRZ": reward -= 50;
      case "PAR": reward -= 40;
      case "PSN": reward -= 20;
      case "SLP": reward -= 45;
    }
    
    updateTeam();
    // +/- 50 reward for faints
    if (oTeamHP[oPrevIndex] == 0)
      reward += 50;
    if (HP == 0)
      reward -= 50;
  

    System.out.println("e: reward = " + reward);
    totalReward += reward;
    return reward;
  }
  
  // return this.state
  public String[] getState() {
    return this.state;
  }
  
  // after game over return win/loss final team hps and total reward.
  public String[] getEndState() {
    String win = a.win;
    String teamHPS = "[" + teamHP[0];
    for (int i = 1; i < 6; i++) {
      teamHPS += ", " + teamHP[i];
    }
    teamHPS += "]";

    String oTeamHPS = "[" + oTeamHP[0];
    for (int i = 1; i < 6; i++) {
      oTeamHPS += ", " + oTeamHP[i];
    }
    oTeamHPS += "]";
    
    return new String[] {win, teamHPS, oTeamHPS, "" + totalReward};
  }
  
  // return this.teamHP
  public int[] getTeamHP() {
    return this.teamHP;
  } 
  
  // return this.oTeamHp
  public int[] getOTeamHP() {
    return this.oTeamHP;
  }  
  
  // ==== Private Methods ====
  // update non team values
  private void update() throws FileNotFoundException, InterruptedException {
    HP = a.getHP();
    oHP = a.getOHP();
    name = a.getName();
    oName = a.getOName();
    status = a.getStatus();
    oStatus = a.getOStatus();
    currIndex = a.getCurrIndex();
    oCurrIndex = a.getOCurrIndex();
    if (a.getOFaint())
      oTeamHP[prevIndex] = 0; 
    if (HP < 33)
      HPS = "Low";
    else if (HP <= 66)
      HPS = "Mid";
    else
      HPS = "High";
    if (oHP < 33)
      oHPS = "Low";
    else if (oHP <= 66)
      oHPS = "Mid";
    else 
      oHPS = "High";
    state = new String[] {name, oName, HPS, oHPS, status, oStatus};
  }
 
 // update built for NotAutomator
  private void updateN() throws FileNotFoundException, InterruptedException {
    HP = a.getHP();
    oHP = a.getOHP();
    currIndex = a.getCurrIndex();
    oCurrIndex = a.getOCurrIndex();
    if (currIndex != prevIndex)
      name = a.getName();
    if (oCurrIndex != oPrevIndex)
      oName = a.getOName();
    String statusTemp = a.getStatus();
    if (!statusTemp.equals("s"))
      status = statusTemp;
    statusTemp = a.getOStatus();
    if (!statusTemp.equals("s"))
      oStatus = statusTemp;
    boolean oFaint = a.getOFaint();

    if (HP < 33)
      HPS = "Low";
    if (33 <= HP && HP <= 66)
      HPS = "Mid";
    if (66 < HP) 
      HPS = "High";
    if (HP < 33)
      oHPS = "Low";
    if (33 <= oHP && oHP <= 66)
      oHPS = "Mid";
    if (66 < HP) 
      oHPS = "High";
    state = new String[] {name, oName, HPS, oHPS, status, oStatus};
  }
  
  // update team values and prevIndex
  private void updateTeam() {
    // if the pokemon I had out took damage update the hp 
    teamHP[currIndex] = this.HP;
    oTeamHP[oCurrIndex] = this.oHP;
    
    // allows us to see the index of pokemon they had out last turn
    prevIndex = currIndex;
    oPrevIndex = oCurrIndex;
  }
}

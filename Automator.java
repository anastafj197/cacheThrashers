
import java.util.*;
import java.io.*;


public class Automator {
  
  public final int WAIT_TIME = 1000;
  public final File ACTION_FILE = new File("action.txt");
  public final File STATE_FILE = new File("state.txt");
  public final String[] TEAM_NAMES = new String[] {"Exeggutor", "Tauros", "Chansey", 
                                                   "Jolteon", "Golem", "Slowbro"};
                                         
  
  public int HP;
  public int oHP;
  public String name;
  public String oName;
  public String status;
  public String oStatus;
  
  public String[] oTeamNames;
  public int currIndex;
  public int oCurrIndex;
  
  public boolean faint;
  public boolean oFaint;
  
  public boolean done;
  public String win;
  public String flags; // w/l, 
    
  public boolean dirty;
  
  public boolean playerOne;
  public boolean ourPokeFirst;
  
 
  // === Constructor ===
  public Automator() {
		STATE_FILE.delete();
		ACTION_FILE.delete();
    dirty = true;
    faint = false;
    oFaint = false;
    
    oTeamNames = new String[6];
  }
  
  public Automator(String unimportant) throws FileNotFoundException, InterruptedException {
		STATE_FILE.delete();
		ACTION_FILE.delete();
		Random rand = new Random();
		System.out.println("Au: Checking For action.txt");
		while (true) {
			PrintStream ps = new PrintStream(ACTION_FILE);
			int nondescript = rand.nextInt(10);
			if (nondescript < 4)
				ps.print(nondescript);
			else
				ps.print(TEAM_NAMES[nondescript - 4]);
				Thread.sleep(3000);
			ACTION_FILE.delete();
			
		}
    
  }
  
  // Returns when STATE_FILE exists
  public void waitForInput(File f) throws FileNotFoundException, InterruptedException {
		int counter = 0;
    while(!f.exists()) {
      Thread.sleep(WAIT_TIME);
      if (counter % 4 == 0);
				System.out.println("Au: Checking For action.txt");
      counter++;
    }
    Thread.sleep(WAIT_TIME);
  }
  
  // Waits for STATE_FILE to be created and then parses it (state data is now clean)
  public void getState() throws FileNotFoundException, InterruptedException {
    waitForInput(STATE_FILE);
    System.out.println("Au: Getting state");
    Scanner sc = new Scanner(STATE_FILE);
    while (sc.hasNextLine()) {
			System.out.println(sc.nextLine());
    }
    sc = new Scanner(STATE_FILE);
    String firstLine = sc.nextLine();
    
    // on first turn BA sends which player we are as first line before normal output
    if (firstLine.equals("p2a")) {
      playerOne = false;
      firstLine = sc.nextLine();
      ourPokeFirst = false;
    }
    if (firstLine.equals("p1a")) {
      playerOne = true;
      firstLine = sc.nextLine();
      ourPokeFirst = true;
    }

    
    oFaint = false;
    System.out.println("case = " + firstLine);
    switch (firstLine) {
      case "0": // game end
                done = true;
                win = sc.next();
                break;
                
      case "1": // someone fainted
                boolean playerOneFaint = sc.nextLine().charAt(8) == '1';
                if (playerOneFaint) {
                  if (playerOne) {
                    faint = true;
                  } else {
                    faint = false;
                  }
                } else {
                  if (playerOne) {
                    faint = false;
                  } else {
                    faint = true;
                  }
                }
                    
                if (sc.hasNextLine()) { // both fainted
                  faint = true;
                  oFaint = true;
                  oName = "faint";
                  oHP = 0;
                  oStatus = "none";
                }
                getState();
                break;
                
      case "2": // they forfeit
                done = true;
                win = "W";
                break;
                
      case "3": // normal turn
                if (faint) {
                  name = "faint";
                  HP = 0;
                  status = "none";
                  faint = false;
                  
                  // getShit for them only
                  if (oFaint) {
                    if (ourPokeFirst) {
                      sc.nextLine();
                    
                      String them = sc.nextLine(); // them
                      readShit(them, 'o');
                    } else {
                    
                      String them = sc.nextLine(); // them
                      readShit(them, 'o');
                    }
                  }
                  
                } else {
                  if (ourPokeFirst) {
                    String us = sc.nextLine(); // us
                    readShit(us, 'u');
                    
                    String them = sc.nextLine(); // them
                    readShit(them, 'o');
                  } else {
                    
                    String them = sc.nextLine(); // them
                    readShit(them, 'o');
                    
                    String us = sc.nextLine(); // us
                    readShit(us, 'u');
                  }
                  
                }
                break;
                
      case "4": // someone switched
                int lastSwitch = Integer.parseInt(sc.nextLine());
                
                if (playerOne && lastSwitch == 1) {
                  ourPokeFirst = false;
                } else if (lastSwitch == 21) {
                  if (playerOne) {
                    ourPokeFirst = true;
                  } else {
                    ourPokeFirst = false;
                  }
                } else {
                  ourPokeFirst = true;
                }
                getState();
                
//                 if (faint) {
//                   name = "faint";
//                   HP = 0;
//                   status = "none";
//                   faint = false;
//                   
//                   // getShit for them only
//                   if (ourPokeFirst) {
//                     sc.nextLine();
//                     
//                     String them = sc.nextLine(); // them
//                     readShit(them, 'o');
//                   } else {
//                     
//                     String them = sc.nextLine(); // them
//                     readShit(them, 'o');
//                   }
//                   
//                 } else {
//                   if (ourPokeFirst) {
//                     String us = sc.nextLine(); // us
//                     readShit(us, 'u');
//                     
//                     String them = sc.nextLine(); // them
//                     readShit(them, 'o');
//                   } else {
//                     
//                     String them = sc.nextLine(); // them
//                     readShit(them, 'o');
//                     
//                     String us = sc.nextLine(); // us
//                     readShit(us, 'u');
//                   }
//                   
//                 }
                break;
                
    }
    
    if (!name.equals("faint")) {
			currIndex = indexOf(name, TEAM_NAMES);
		}
		
    if (!oName.equals("faint")) {
      oCurrIndex = indexOf(oName, oTeamNames);
      if (oCurrIndex == -1)
        oCurrIndex = firstEmpty();
      oTeamNames[oCurrIndex] = oName;
    }
    
    if(!isValidStatus(status))
			status = "none";
		if(!isValidStatus(oStatus))
			oStatus = "none";
    
    dirty = false;
    System.out.println(name + " " + oName + " " + HP + " " + oHP + " " + status + " " + oStatus + " " + currIndex + " " + oCurrIndex);
    STATE_FILE.delete();
  }
  
  //
  public boolean isValidStatus(String st) {
		String[] valid = new String[] {"SLP", "PSN", "FRZ", "TOX", "PAR", "BRN"};
		for (int i = 0; i < valid.length; i++)
			if (st.equals(valid[i]))
				return true;
		return false;
  }
  
  //
  public void readShit(String shit, char who) {
		if (who == 'o') {
			String them = shit;
			int firstSpace = them.indexOf(' ');
      int i = 0;
      while (them.charAt(i) < '0' || them.charAt(i) > '9') { // find first int
        i++;
      }
			if (firstSpace != -1 && firstSpace >= i) // if there is a space after the first int
				them = them.substring(0, firstSpace);
      oName = them.substring(0, i);
      oHP = Integer.parseInt(them.substring(i, them.indexOf('%')));
      oStatus = them.substring(them.indexOf('%') + 1);
		} else {
			// us
			String us = shit;
      int i = 0;
      while (us.charAt(i) < '0' || us.charAt(i) > '9') { // find first int
        i++;
      }
			int firstSpace = us.indexOf(' ');
			if (firstSpace != -1 && firstSpace >= i) // if there is a space after the first int
				us = us.substring(0, firstSpace);
      name = us.substring(0, i);
      HP = Integer.parseInt(us.substring(i,us.indexOf('%')));
      status = us.substring(us.indexOf('%') + 1);
		}
  }
  
  // Returns the index of the first instance of s in a
  public int indexOf(String s, String[] a) {
    for (int i  = 0; i < a.length; i++) {
      if (a[i] != null && a[i].equals(s)) {
        return i;
      }
    }
    return -1;
  }
  
  // Returns the first index in oTeamNames containing null
  public int firstEmpty() {
		System.out.println("firstEmpty()");
    for (int i = 0; i < 6; i++) {
      if (oTeamNames[i] == null) {
				System.out.println("fE: i: " + i);
        return i;
      }
    }
    return -1;
  }
  
  //
  public String getFlags() {
    return this.flags;
  }
  
  // Writes action to ACTION_FILE (state data is now dirty)
  public void sendAction(String a) throws FileNotFoundException {
    PrintStream ps = new PrintStream(ACTION_FILE);
    ps.print(a);
    dirty = true;
  }
  
  // Refreshes state if dirty. Returns this.done
  public boolean done() throws FileNotFoundException, InterruptedException {
    if (dirty)
      getState();
    return done;
  }
  
  // Refreshes state if dirty. Returns this.HP
  public int getHP() throws FileNotFoundException, InterruptedException {
    if (dirty)
      getState();
    return HP;
  }
  
  // Refreshes state if dirty. Returns this.oHP
  public int getOHP() throws FileNotFoundException, InterruptedException {
    if (dirty)
      getState();
    return oHP;
  }
  
  // Refreshes state if dirty. Returns this.name
  public String getName() throws FileNotFoundException, InterruptedException {
    if (dirty)
      getState();
    return name;
  }
  
  // Refreshes state if dirty. Returns this.oName
  public String getOName() throws FileNotFoundException, InterruptedException {
    if (dirty)
      getState();
    return oName;
  }
  
  // Refreshes state if dirty. Returns this.status
  public String getStatus() throws FileNotFoundException, InterruptedException {
    if (dirty)
      getState();
    return status;
  }
  
  // Refreshes state if dirty. Returns this.oStatus
  public String getOStatus() throws FileNotFoundException, InterruptedException {
    if (dirty)
      getState();
    return oStatus;
  }
  
  // Refreshes state if dirty. Returns this.currIndex
  public int getCurrIndex() throws FileNotFoundException, InterruptedException {
    if (dirty)
      getState();
    return currIndex;
  }
  
  // Refreshes state if dirty. Returns this.oCurrIndex
  public int getOCurrIndex() throws FileNotFoundException, InterruptedException {
    if (dirty)
      getState();
    return oCurrIndex;
  }
  
  // Returns this.oFaint. Resets oFaint to true
  public boolean getOFaint() throws FileNotFoundException, InterruptedException {
    if (oFaint) {
      oFaint = false;
      return true;
    }
    return false;
  }
  
  // Sends action to ACTION_FILE
  public void move(int n) throws FileNotFoundException {
    sendAction("" + n);
  }
  
  // Sends action to ACTION_FILE
  public void swap(int n) throws FileNotFoundException {
    sendAction(TEAM_NAMES[n]);
    currIndex = n;
  }
}


import java.applet.Applet;
import netscape.javascript.*;
import java.util.*;
import java.io.*;

public class RedClient {
  
  // ==== Class Constants ====
  public static final String PATHFINDER = "phantomjs/phantomjs battleAutomator.js";
  public static final String Q_TABLE_FILE = "QTable.txt";
  public static final String LOG_FILE = "logs.txt";
  public static final String USERNAME = "xX420_SwAgXx";
  public static final String PASSWORD = "12345";
  public static final boolean NOT_AUTOMATOR = true;
  
  // ==== Methods ====
  public static void main(String[] args) throws FileNotFoundException, InterruptedException, IOException {
    Scanner sc = new Scanner(System.in);
    PrintStream ps = new PrintStream(new File(LOG_FILE));
    String a1 = "";
    if (args.length > 0)
      a1 = args[0];
    ArrayList<State> QTable = initQTable();
    Agent red;
    if (a1.equals("-g"))
      red = new Agent(QTable, "-g");
    else 
      red = new Agent(QTable);
    Automator a;
    if (a1.equals("-r")) {
			Runtime rt = Runtime.getRuntime();
      Process pr = rt.exec(PATHFINDER);
			a = new Automator("-r");
			
		}
		else
			a = new Automator();
		

    
//     NotAutomator nA = new NotAutomator();
    
    Environment e = new Environment(a);
//     for (int i = 0; i < 10; i++) {
      Runtime rt = Runtime.getRuntime();
      Process pr = rt.exec("/home/student/auslanem196/bin/phantomjs/phantomjs "
             + "/battleAutomator.js");
      ArrayList<State> states = new ArrayList<State>();
      ArrayList<Integer> actions = new ArrayList<Integer>();
      e.newGame();
      while (!a.done()) {
        int action = red.actionSelection(e.getState(), e.getValidActions());
        int reward;
        if (!a1.equals("-c")) {
					reward = e.act(action);
				} else {
					System.out.println("Choose action (0-9) at your own risk. ");
					System.out.print("0-3 = moves, 4-9 = swaps: ");
					reward = e.act(Integer.parseInt(sc.next()));
				}
        
        if (!a.done()) {
          red.updateQ(reward);
          states.add(red.getCurrState());
          actions.add(red.getCurrAction());
        }
      }
      red.updateWinningQ(states, actions);
      gameLog(ps, e); // w/l ending teamStates totalReward ending teamState 
      pr.destroy();
//     }
    logQTable(red.getQTable());
    
  }
  
  public static void gameLog(PrintStream ps, Environment e) {
    String[] endState = e.getEndState();
    ps.print(endState[0]);
    System.out.print(endState[0]);
    for (int i = 1; i < endState.length; i++) {
      ps.print(" " + endState[i]);
      System.out.print(endState[i]);
    }
    ps.println();
    System.out.println();
  }
  
  
  // Parses Q_TABLE_FILE returns QTable
  public static ArrayList<State> initQTable() throws FileNotFoundException {
    Scanner sc = new Scanner(new File(Q_TABLE_FILE));
    int n = Integer.parseInt(sc.next());
    ArrayList<State> QTable = new ArrayList<State>();
    for (int i = 0; i < n; i++) {
      String[] state = new String[6];
      for (int j = 0; j < 6; j++) {
        state[j] = sc.next();
      }
      double[] qs = new double[10];
      for (int j = 0; j < qs.length; j++) {
        qs[j] = new Double(sc.next());
      }
      int[] counts = new int[10];
      for (int j = 0; j < counts.length; j++) {
        counts[j] = Integer.parseInt(sc.next());
      }
      QTable.add(new State(state, qs, counts));
    }
    return QTable;
  }
  
  // Prints QTable to Q_TABLE_FILE
  public static void logQTable(ArrayList<State> QTable) throws FileNotFoundException{
    PrintStream ps = new PrintStream(new File(Q_TABLE_FILE));
    ps.println(QTable.size());
    for (int i = 0; i < QTable.size(); i++) {
      State curr = QTable.get(i);
      String[] state = curr.getState();
      for (int j = 0; j < 6; j++) {
        ps.print(state[j] + " ");
      }
      double[] qs = curr.getQs();
      for (int j = 0; j < 10; j++) {
        ps.print(qs[j] + " ");
      }
      int[] counts = curr.getCounts();
      for (int j = 0; j < 10; j++) {
        ps.print(counts[j] + " ");
      }
      ps.println();
    }
  }
}

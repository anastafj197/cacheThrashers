import java.util.*;
import java.io.*;

public class CompileQTables {
  
  public static void main(String[] args) throws FileNotFoundException {
    ArrayList<ArrayList<State>> tables = new ArrayList<ArrayList<State>>();
    ArrayList<State> QTable = new ArrayList<State>();
    for (int i = 1; i < args.length; i++) {
      tables.add(initQTable(args[i]));
    }
    for (int i = 0; i < tables.size(); i++) {
      for (int j = 0; j < tables.get(i).size(); j++) {
        int a = contains(QTable, tables.get(i).get(j));
        if (a >= 0)
          QTable.set(a, addStates(QTable.get(a), tables.get(i).get(j)));
        else
          QTable.add(tables.get(i).get(j));
      }
    }
    logQTable(QTable, args[0]);
  }
  
  // Takes fileName, returns QTable parsed from file contents
  public static ArrayList<State> initQTable(String fileName) throws FileNotFoundException {
    Scanner sc = new Scanner(new File(fileName));
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
  
  // Takes QTable and fileName, logs QTable in file
  public static void logQTable(ArrayList<State> QTable, String fileName) throws FileNotFoundException{
    PrintStream ps = new PrintStream(new File(fileName));
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
  
  // Returns true if QTable contains state equivalent to s
  public static int contains(ArrayList<State> QTable, State s) {
    for (int i = 0; i < QTable.size(); i++)
      if (equalState(QTable.get(i), s))
        return i;
    return -1;
  }
  
  // Returns true if states are equivilent 
  public static boolean equalState(State sO1, State sO2) {
    String[] sA1 = sO1.getState();
    String[] sA2 = sO2.getState();
    for (int i = 0; i < sA1.length; i++)
      if (!sA1[i].equals(sA2[i]))
        return false;
    return true;
  }
  
  public static State addStates(State s1, State s2) {
    double[] q1 = s1.getQs();
    double[] q2 = s2.getQs();
    int[] c1 = s1.getCounts();
    int[] c2 = s2.getCounts();
    
    double[] qs = new double[q1.length];
    int[] counts = new int[q1.length];
    
    for (int i = 0; i < q1.length; i++) {
      counts[i] = c1[i] + c2[i];
      qs[i] = (c1[i] * q1[i] + c2[i] * q2[i]) / counts[i];
    }
    
    return new State(s1.getState(), qs, counts);
  }
}

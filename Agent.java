import java.util.*;
public class Agent {
  
  // ==== Private Fields ====
  private ArrayList<State> qTable; // 
  private State currState; // most recent state
  private int currAction; // most recent action
  
  private State prevState; // second most recent state
  private int prevAction; // second most recent action
  private boolean greedy; // greedy action selection
  
  // ==== Constructors ====
  public Agent(ArrayList<State> qTable) {
    this.qTable = qTable;
    prevState = new State(new String[] {"D", "u", "m", "m", "y", "!"});
    currState = new State(new String[] {"D", "u", "m", "m", "y", "!"});
  }
  
  public Agent(ArrayList<State> qTable, String arg) {
    this.qTable = qTable;
    prevState = new State(new String[] {"D", "u", "m", "m", "y", "!"});
    currState = new State(new String[] {"D", "u", "m", "m", "y", "!"});
    if (arg.equals("-g"))
      this.greedy = true;
  }
  
  // ==== Public Methods ====
  public void updateQ(int reward) {
    double q = currState.getQs()[currAction];
    int count = currState.getCounts()[currAction];
    System.out.println("a: currState = " + currState);
    currState.getQs()[currAction] = (count * q + reward) / (count + 1);
    currState.getCounts()[currAction]++;
    System.out.println("a: currState = " + currState);
    
    
    
    q = prevState.getQs()[prevAction];
    count = prevState.getCounts()[prevAction];
    System.out.println("a: prevState = " + prevState);
    prevState.getQs()[prevAction] = (count * q + (q + reward / 4)) / (count + 1);
    prevState.getCounts()[prevAction]++;
    System.out.println("a: prevState = " + prevState);
  }
  
  // ---- 
  public int actionSelection(String[] state, boolean[] valid) {
    prevState = currState;
    prevAction = currAction;
    currState = getStateObject(state);
    double[] q = currState.getQs();
    if (greedy) {
      double maxQ = q[0];
      int maxI = 0;
      for (int i = 0; i < q.length; i++) {
        if (maxQ < q[i]) {
          maxQ = q[i];
          maxI = i;
        }
      }
      return maxI;
    }
      
    double totalQ = 0.0;
    for (int i = 0; i < 9; i++) {
      if (valid[i]) {
        
        totalQ += Math.exp(q[i]);
      }
    }
    Random rand = new Random();
    double r = rand.nextDouble() * totalQ;
    double sum = 0;
    for (int i = 0; i < 9; i++) {
      if (valid[i]) {
        sum += Math.exp(q[i]);
      }
      if (r < sum) {
        currAction = i;
        System.out.println("a: currAction = " + currAction);
        return i;
      }
    }
    currAction = -1;
    return -1;
  }
  
  // boosts the Q values of each given state-action pair
  public void updateWinningQ(ArrayList<State> states, ArrayList<Integer> actions) {
    for (int i = 0; i < states.size(); i++) {
      double q = states.get(i).getQs()[actions.get(i)];
      int count = states.get(i).getCounts()[actions.get(i)];
      System.out.println("a: currState = " + currState);
      states.get(i).getQs()[actions.get(i)] = (count * q + (q + 5)) / (count + 1);
      states.get(i).getCounts()[actions.get(i)]++;
      System.out.println("a: currState = " + currState);
    }
  }
  
  // ---- Returns qTable ----
  public ArrayList<State> getQTable() {
    return this.qTable;
  }
  
  public State getCurrState() {
    return currState;
  }
  
  public int getCurrAction() {
    return currAction;
  }
  
  // ==== Private Methods ====
  private State getStateObject(String[] state) {
    for (int i = 0; i < qTable.size(); i++) {
      State current = qTable.get(i);
      boolean match = true;
      for (int j = 0; j < state.length; j++) {
        if (!current.getState()[j].equals(state[j])) {
          match = false;
        }
      }
      if (match) {
        return current;
      }
    }
    State newState = new State(state);
    qTable.add(newState);
    return newState;
  } 
}

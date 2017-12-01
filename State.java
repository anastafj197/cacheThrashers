public class State {
  
  // ==== Class Constant ====
  
  public static double INITIAL_Q_VALUE = 100.0;
  
  
  // ==== Private Fields ====
  
  private String[] state;  // [name, oName, HP, oHP, status, oStatus]
  private double[] qs;     // q values associated with [m0, m1, m2, m3, s0, s1, s2, s3, s4, s5]
  private int[] counts;     // number of times each q value has been updated
  
  
  // ==== Constructors ====
  
  // Initializes blank State object for given state array
  public State(String[] state) {
    this.state = state;
    counts = new int[10];
    qs = new double[10];
    for (int i = 0; i < 10; i++) {
      qs[i] = INITIAL_Q_VALUE;
    }
  }
  
  // Initializes State object for given state array with given qs and counts
  public State(String[] state, double[] qs, int[] counts) {
    this.state = state;
    this.qs = qs;
    this.counts = counts;
  }

  // ==== Public Methods ====
  
  // Returns state array
  public String[] getState() {
    return this.state;
  }
  
  // Returns name
  public String getName() {
    return this.state[0];
  }
  
  // Returns oName
  public String getOName() {
    return this.state[1];
  }
  
  // Returns HP
  public String getHP() {
    return this.state[2];
  }
  
  // Returns oHP
  public String getOHP() {
    return this.state[3];
  }
  
  // Returns status
  public String getStatus() {
    return this.state[4];
  }
  
  // Returns oStatus
  public String getOStatus() {
    return this.state[5];
  }
  
  // Returns qs array
  public double[] getQs() {
    return qs;
  }
  
  // Returns counts array
  public int[] getCounts() {
    return counts;
  }
  
  public String toString() {
    String result = state[0];
    for (int i = 1; i < state.length; i++) {
      result += ", " + state[i];
    }
    for (int i = 0; i < qs.length; i++) {
      result += ", " + qs[i];
    }
    for (int i = 0; i < counts.length; i++) {
      result += ", " + counts[i];
    }
    return result;
  }
}

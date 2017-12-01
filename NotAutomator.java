import java.io.*;
import java.util.*;
public class NotAutomator {
  public int currIndex;
  public int oCurrIndex;
  public String[] teamNames;
  public String[] oTeamNames;
  public String status;
  public String oStatus;
  public Scanner sc;
  public NotAutomator() {
    sc = new Scanner(System.in);
  }
  
  public int getHP() {
    System.out.print("nA: HP? ");
    return Integer.parseInt(sc.next());
  }
  
  public int getOHP() {
    System.out.print("nA: oHP? ");
    return Integer.parseInt(sc.next());
  }
  
  public String getName() {
    System.out.print("nA: name? ");
    return sc.next();
  }
  
  public String getOName() {
    System.out.print("nA: oName? ");
    return sc.next();
  }
  
  public String getStatus() {
    System.out.print("nA: status? ");
    return sc.next();
  }
  
  public String getOStatus() {
    System.out.print("nA: oStatus? ");
    return sc.next();
  }
  
  public int getCurrIndex() {
    System.out.print("nA: currIndex? ");
    return Integer.parseInt(sc.next());
  }
  
  public int getOCurrIndex() {
    System.out.print("nA: oCurrIndex? ");
    return Integer.parseInt(sc.next());
  }
  
  public int move(int n) {
    System.out.println("nA: ## Use Move " + n);
    return 0;
  }
  
  public int swap(int n) {
    System.out.println("nA: ## Swap to Poke " + n);
    return 0;
  }
  
  public void login(String u, String p) {
    System.out.println("nA: ## Login");
  }
  
  public void startGame() {
    System.out.println("nA: ## Start Game");
  }
  
  public boolean done() {
    System.out.print("nA: Done? ");
    return sc.next().equals("y");
  }
}


import java.io.*;

public class MyJob implements CallableSer<Integer>, Serializable {

  int count = 1; 
  public static int fib(int n){if (n<2) return 1;else return (fib(n-1)+fib(n-2));}

  MyJob(int n){count=n;}

  public Integer call() throws Exception {
  int n = count;
  System.out.println("[Y] Je suis dans le thread " +Thread.currentThread().getName()+" pour fib("+n+")"); 
  Integer res = fib(n);
  System.out.println("[Y] Je suis dans le thread " +
   Thread.currentThread().getName() + "res = fib("+n+") = " + res); return res;
  }
}

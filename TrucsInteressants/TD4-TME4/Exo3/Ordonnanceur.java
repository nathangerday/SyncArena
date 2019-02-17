import java.util.Vector;
import java.lang.Thread;

class OrdonneSolution
{
    public static void main(String[] args)
    {
	Ordonnanceur mainOrd = new Ordonnanceur();
	Task t0 = new Task(0);
	Task t1 = new Task(1);
	Task t2 = new Task(2);
	Task t3 = new Task(3);

	mainOrd.addTask(t0);
	mainOrd.addTask(t1);
	mainOrd.addTask(t2);
	mainOrd.addTask(t3);
	mainOrd.start();
	try { mainOrd.join(); }
	catch (InterruptedException e) { e.printStackTrace(); }
    }
}
 
class Ordonnanceur extends Thread
{
    int          nbTasks;
    int          curTask;
    Vector<Task> tasks;
    String       doneCond;
	
    Ordonnanceur() { tasks = new Vector<Task>(); doneCond = new String(); }
	
    public synchronized void addTask(Task t)
    { 
	tasks.addElement(t);
	t.setOrdonnanceur(this);
	nbTasks++;
    }
	
    public int getCurrent() { return curTask; }
	
    public String getDoneCondition() { return doneCond; }
	
    public void run()
    {
	Task waitTask;
	int  i, j;
		
	curTask = -1;
	for (i = 0; i < nbTasks; i++)
	    {
		System.out.println("Starting task " + i + ".");
		tasks.elementAt(i).start();
	    }
	while (true)
	    {
		for (i = 0; i < nbTasks; i++)
		    {
			try { Thread.sleep(1000); }
			catch(InterruptedException ex)
			    { Thread.currentThread().interrupt(); }
			synchronized(this)
			    {
				curTask = i;
				this.notifyAll();
			    }
			for (j = 0; j < nbTasks; j++)
			    {
				System.out.println(
				  "Ordonnanceur wait n." + i);
				synchronized (doneCond) 
				    { 
					try { doneCond.wait(); }
					catch (InterruptedException e)
					    { e.printStackTrace(); }
				    }
			    }
		    }
	    }
    }
}

class Task extends Thread
{
    String       cond;
    Ordonnanceur ord;
    int          idTask;
	
    Task(int idT) { idTask = idT; }
	
    public void setOrdonnanceur (Ordonnanceur or)
    { ord = or; cond = or.getDoneCondition();}

    public void run()
    {
	while (true)
	    {
		synchronized(ord)
		    {
			System.out.println("Task " + idTask + " - Looping");
			try { ord.wait(); }
			catch (InterruptedException e)
			    { e.printStackTrace(); }
			if (ord.getCurrent() == idTask)
			    {
				System.out.println(
				  "Task " + idTask + " - Activated");
				// Computations here
			    }
		    }
		synchronized(cond) { cond.notify(); }
	    }
    }
}

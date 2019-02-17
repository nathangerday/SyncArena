class PointStopThreadCorrectbis extends Thread {
    boolean stopped   = false;
    boolean suspended = false;

    PointStop p;
    int       val;

    PointStopThreadCorrectbis (PointStop a_p, int a_val) {
	super("t[" + a_val + "]");	// Il est conseille de donner un nom
	p   = a_p;			//   distinct a chaque thread
	val = a_val;
    }

    void threadStop () {
	stopped = true;
    }

    void threadSuspend () {
	suspended = true;
    }

    void threadResume () {
	suspended = false;
	synchronized (this) {
	    this.notify();
	}
    }

    void traiterInterruption () {
	System.out.println("Retour Interruption");
    }

    public void run () {
	int k;		//nombre de modifications du point

	System.out.println("Processus " + getName() + " commence");
	k = 0;
	while (!stopped		//condition générale
	       && k < 20) {	//condition specifique du Thread
	    try {
		if (suspended) {  //assurer la coherence
		    		  //accepter la suspension jusqu'a threadResume
		    System.out.println("t[" + val + "] : suspension");	
		    synchronized (this) {
			this.wait();
			System.out.println("t[" + val +
					   "] : sortie suspension");
		    }
		} else {	//le travail du Thread	
		    synchronized (p) {
			p.moveTo(val, val);
			System.out.println("t[" + val + "]\tk = " +
					   k + "\tet p = " + p);
			k++;
		    }
		}
	    } catch (InterruptedException e) {
		traiterInterruption();
	    }	
	    if (interrupted()) {
		traiterInterruption();
	    }
	}
	System.out.println("Processus " +
			   getName() + " termine, stopped = " + stopped);
    }
}

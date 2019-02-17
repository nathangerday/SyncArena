class Producteur  extends Thread {
    IntFifo fifo;
    int     n;
    String  nom;

    public Producteur (String a_nom, int a_n, IntFifo a_fifo) {
	super(a_nom);

	nom  = a_nom;
	n    = a_n;
	fifo = a_fifo;
    }

    public void run () {
	int k;

	k = 0;
	while(k < n) {
	    synchronized (fifo) { 
		try {
		    fifo.add(k);
		    System.out.println(nom + " met " + k);
		    k++;
		    if (k % 5 == 0)
			Thread.yield();
		} catch (FifoFullException e) {
		    System.out.println(nom + " : fifopleine");
		}
	    }
	}
    }
}


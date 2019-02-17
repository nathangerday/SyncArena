class Consommateur extends Thread {
    IntFifo fifo;
    int     m;
    String  nom;

    public Consommateur (String a_nom, int a_m, IntFifo a_fifo) {
	super(a_nom);

	nom  = a_nom;
	m    = a_m;
	fifo = a_fifo;
    }

    public void run () {
	int k, val;

	k = val = 0; 
	
	while (k < m) {
	    synchronized (fifo) {
		try {
		    val = fifo.take();
		    System.out.println(nom + " prend " + val);
		    k++;
		    if (k % 5 == 0)
			Thread.yield();
		} catch (FifoEmptyException e) {
		    System.out.println(nom + " : fifovide");
		}
	    }
	}
    }
}

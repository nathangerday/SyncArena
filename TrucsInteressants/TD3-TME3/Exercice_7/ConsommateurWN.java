import java.util.Random;

class ConsommateurWN extends Thread {
    IntFifo fifo;
    int     m;
    String  nom;

    ConsommateurWN (String a_nom, int a_m, IntFifo a_fifo) {
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
		    if (!fifo.isEmpty()) {
			val = fifo.take();
			System.out.println(nom + " prend " + val);
			fifo.notifyAll();
			k++;
		    } else {
			System.out.println(nom +
					   " attend et laisse la main au " +
					   (k + 1) + "ieme tour");
			fifo.wait();
		    }
		    if (k % 5 == 0)
			Thread.yield();
		} catch (Exception e) {
		    System.out.println(nom + " : " + e);
		}
	    }
	}
    }
}

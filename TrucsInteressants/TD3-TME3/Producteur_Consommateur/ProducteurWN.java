import java.util.Random;

class ProducteurWN  extends Thread {
    IntFifo fifo;
    int     n;
    String  nom;

    ProducteurWN (String a_nom, int a_n, IntFifo a_fifo) {
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
		    if (!fifo.isFull()) {
			fifo.add(k);
			System.out.println(nom + " met " + k);
			fifo.notify();
			k++;
			if (k % 5 == 0)
			    Thread.yield();
		    } else {
			System.out.println(nom +
					   " attend et laisse la main au " +
					   (k + 1) + "ieme tour");
			fifo.wait();
		    }
		} catch (Exception e) {
		    System.out.println(nom + " : " + e);
		}
	    }
	}
    }
}

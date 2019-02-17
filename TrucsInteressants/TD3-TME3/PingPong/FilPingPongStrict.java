import java.util.Random;

class FilPingPongStrict implements Runnable {
    String  affich;
    Random  alea;
    Bascule bascule;
    boolean bval;

    FilPingPongStrict(String a_affich, Bascule a_bascule, boolean a_bval) {
	affich  = a_affich;
	alea    = new Random();
	bascule = a_bascule;
	bval    = a_bval;
    }

    public void run () {
	int k;

	k = 0;
	while (k < 20) {
	    synchronized (bascule) {
		if (bascule.getVal() == bval) {
		    System.out.println(affich);
		    bascule.setVal(!bval);
		    k++;
		}
	    }
	}
    }
}

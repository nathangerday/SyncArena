import java.util.Random;

class FilPingPong implements Runnable {
    String affich;
    Random alea;

    FilPingPong (String a_affich) {
	affich = a_affich;
	alea   = new Random();
    }

    public void run () {
	int k, kyield;

	kyield = alea.nextInt(3) + 1;
	for (k = 0; k < 20; k++) {
	    System.out.println(affich);
	    if (k == kyield) {
		Thread.yield();
		kyield += alea.nextInt(3) + 1;
	    }
	}
    }
}

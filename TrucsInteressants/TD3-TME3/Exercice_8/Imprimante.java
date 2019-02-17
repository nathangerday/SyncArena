class Imprimante implements Runnable {


    String[] spooler = new String[100];
    int      index = 0;
    boolean  pagesEnAttente = false;
    Thread   daemon;


    Imprimante () {
	daemon = new Thread(this);
	daemon.setDaemon(true);
	daemon.setPriority(Thread.MIN_PRIORITY);
	daemon.start();
    }


    public synchronized void imprimer () {
	System.out.println("Debut d'impression");
	for (int i = 0; i < spooler.length; i++) {
	    if (spooler[i] != null) {
		System.out.println(spooler[i]);
		spooler[i] = null;
	    }
	}
	System.out.println("Fin d'impression");
	pagesEnAttente = false;
	notifyAll();
    }


    public synchronized void spooler(String[] s) {
	for (int i = 0; i < s.length; ++i)
	    spooler[index++] = s[i];
	pagesEnAttente = true;
	try {
	    wait();
	} catch (InterruptedException e) {}
    }


    public void run () {
	while (true) {
	    if (pagesEnAttente)
		imprimer();
	}
    }
}

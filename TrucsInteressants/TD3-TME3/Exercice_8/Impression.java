public class Impression extends Thread {


    static Imprimante imprimante;
    String[]          pages;


    Impression (String s, Imprimante i) {
	super(s);
	imprimante = i;
    }


    public void run () {
	synchronized (imprimante) {
	    pages = new String[5];

	    for (int i = 0; i < pages.length; ++i)
		pages[i] = "Page " + (i + 1) + " du " + getName();
	    imprimante.spooler(pages);
	}
    }
}

public class TestImpression {

    static Imprimante    imprimante;
    static Impression [] impression;;


    public static void main (String[] args) {
	imprimante = new Imprimante();
	impression = new Impression[5];
	for (int i = 0; i < 5; i++) {
	    impression[i] = new Impression("Document " + (i + 1), imprimante);
	    impression[i].start();
	}
    }
}

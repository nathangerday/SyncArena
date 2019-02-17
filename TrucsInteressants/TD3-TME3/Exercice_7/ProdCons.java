class ProdCons {

    static public void main (String  args[]) {
	IntFifo      fifo = new IntFifo(10);;
	Producteur   producteurs[] = new Producteur[2];
	Consommateur consommateurs[] = new Consommateur[3];
	int i, j;

	for (i = 0; i < 2; i++)
	    producteurs[i] = new Producteur("Producteur " + i, 15, fifo);
	for (j = 0; j < 3; j++)
	    consommateurs[j] = new Consommateur("Consommateur " + j, 10, fifo);

	for (j = 0; j < 3; j++)
	    consommateurs[j].start();
	for (i = 0; i < 2; i++)
	    producteurs[i].start();

	try {
	    for (j = 0; j < 3; j++)
		consommateurs[j].join();
	    for (i = 0; i < 2; i++)
		producteurs[i].join();
	}
	catch (InterruptedException e) {}
    }
}

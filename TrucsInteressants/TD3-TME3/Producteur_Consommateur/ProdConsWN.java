class ProdConsWN {

    static public void main (String  args []) {
	IntFifo        fifo = new IntFifo(10);;
	ProducteurWN   producteurs[] = new ProducteurWN[2];
	ConsommateurWN consommateurs[] = new ConsommateurWN[3];
	int i, j;

	for (i = 0; i < 2; i++)
	    producteurs[i] = new ProducteurWN("Producteur " + i, 15, fifo);
	for (j = 0; j < 3; j++)
	    consommateurs[j] = new ConsommateurWN("Consommateur " + j,
						  10, fifo);

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

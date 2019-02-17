class TestPointStopThreadCorrectbis {
    // plusieurs threads manipulant le meme point

    public static void main (String args[]) {
	PointStop                 p = new PointStop();
	PointStopThreadCorrectbis t[] = new PointStopThreadCorrectbis[6];
	int                       i;

	for (i = 0; i < 6; ++i)
	    t[i] = new PointStopThreadCorrectbis(p, i + 1);

	for (i = 0; i < 6; ++i)
	    t[i].start();

	try {
	    Thread.sleep(10);		// augmenter le temps 100, 1000, ...
	} catch (Exception e) {}	//   du main() pour que les threads
					//   aient le temps de modifier chacun
	for (i = 1; i < 6; ++i)		//   plusieurs fois le meme point p
	    t[i].threadStop();

	try {
	    Thread.sleep(10);		// idem
	} catch (Exception e) {}

	synchronized (p) {
	    System.out.println("main : " + p.toString());
	}
    }
}

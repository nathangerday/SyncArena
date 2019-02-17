class TestFilPingPong {

    public static void main (String args[]) {
	FilPingPong fping, fpong;
	Thread ping, pong;
	int k, nb;

	fping = new FilPingPong("PING");
	fpong = new FilPingPong("PONG");
	ping  = new Thread(fping);
	pong  = new Thread(fpong);

	ping.start();
	pong.start();

	try {
	    ping.join();
	    pong.join();
	} catch (InterruptedException e) {};

	System.out.println("fin PING et PONG");
    }
}

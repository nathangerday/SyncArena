class TestFilPingPongStrict {

    public static void main (String args[]) {
	Bascule           b;
	FilPingPongStrict fping, fpong;
	Thread            ping, pong;
	int               k, nb;

	b = new Bascule();
	fping = new FilPingPongStrict("PING", b, false);
	fpong = new FilPingPongStrict("PONG", b, true);
	ping = new Thread(fping);
	pong = new Thread(fpong);


	ping.start();
	pong.start();

	try {
	    ping.join();
	    pong.join();
	} catch (InterruptedException e) {};

	System.out.println("fin PING et PONG");
    }
}

class Point {
    int x, y;

    Point () {
	x = 0;
	y = 0;
    }

    void moveTo (int a_x, int a_y) {
	x = a_x;
	Thread.yield();
	y = a_y;
    }

    public String toString () {
	return "(" + x + "," +  y + ")";
    }
}

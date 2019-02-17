class PointStop {
    int x, y;
    int xcumul, ycumul;

    PointStop () {
	x = 0;
	y = 0;
	xcumul = 0;
	ycumul = 0;
    }

    void moveTo (int a_x, int a_y) {
	x       = a_x;
	xcumul += x;
	Thread.yield();
	y       = a_y;
	ycumul += y;
    }

    public String toString () {
	return "(" + x + "," +  y + ")";
    }

    int getXcumul () {
	return xcumul;
    }

    int getYcumul () {
	return ycumul;
    }
}

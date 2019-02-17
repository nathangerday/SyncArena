public class IntFifo {
    int[] tab;
    int   size, iadd, itake, nbelem;

    IntFifo (int a_size) {
	tab  = new int[a_size];
	size =a_size;
	iadd = itake =nbelem = 0;
    }

    boolean isEmpty () {
	return (nbelem == 0);
    }

    boolean isFull () {
	return (nbelem == size);
    }

    void add (int n) throws FifoFullException {
	if (isFull())
	    throw new FifoFullException();
	else {
	    tab[iadd] = n;
	    iadd      = (iadd + 1) % size;
	    nbelem++;
	}
    }

    int take () throws FifoEmptyException {
	int val;

	if (isEmpty( ))
	    throw new FifoEmptyException();
	else {
	    val   = tab[itake];
	    itake = (itake  + 1) % size;
	    nbelem--;
	    return val;
	}
    }
}



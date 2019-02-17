#include<gmp.h>

mpq_t accu;
int n;

void * arctandev(void *arg)
{
  mpq_t x=(mpq_t) arg;
  mpz_t tmpz;
  mpq_t tmp;
  mpq_init(tmp);
  mpz_init(tmpz);
  for(int i=0;i<n;i++)
    {
      mpz_pow_ui(tmpz,mpqnuref(x)....
    }
}

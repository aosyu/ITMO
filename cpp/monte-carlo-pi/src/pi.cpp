#include "pi.h"

#include "random_gen.h"

double calculate_pi(unsigned long runs)
{
    int count = 0;
    int r = 1;
    for (unsigned long i = 0; i < runs; i++) {
        double x = get_random_number();
        double y = get_random_number();
        if ((x * x + y * y) <= r * r) {
            count += 1;
        }
    }
    return (runs == 0? 0 : 4.0 * (static_cast<double>(count) / runs));
}
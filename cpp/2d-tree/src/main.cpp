#include "primitives.h"

#include <iostream>

int main()
{

    int x = 1;
    int y = 2;
    int& a = x;
    int&& b = 1;
//    int&& c = b;
    std::cout << c;
    a = y;
    b = 2;
    std::cout << a << " " << b;
//    std::cout << "To be done..." << std::endl;
}

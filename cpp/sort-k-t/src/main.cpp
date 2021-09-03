#include <algorithm>
#include <cstring>
#include <fstream>
#include <iostream>
#include <string>
#include <utility>
#include <vector>
#include <sstream>


struct my_table {
    std::string line;
    std::vector<std::string> splitted;

    my_table(std::string new_line, std::vector<std::string> new_splitted) : line(std::move(new_line)),
                                                                            splitted(std::move(new_splitted)) {}
};

std::vector<std::string> split_by_sep(const std::string &s, char separator) {
    std::vector<std::string> splitted;
    std::istringstream is(s);
    std::string str;
    if (separator == '\0') {
        while (is >> str) {
            splitted.push_back(str);
        }
    } else {
        while (std::getline(is, str, separator)) {
            splitted.push_back(str);
        }
    }
    return splitted;
}


int main(int argc, char **argv) {
    std::size_t key_field1 = 0;
    std::size_t key_field2 = 0;
    char sep = '\0';

    for (int i = 2; i < argc; ++i) {
        const std::string &token = argv[i];
        if (token == "-k") {
            key_field1 = std::stoi(argv[++i]) - 1;
            key_field2 = key_field1;
        } else if (token == "-t") {
            sep = argv[++i][0];
        } else if (token.substr(0, token.length() - 1) == "--field-separator=") {
            sep = token[token.length() - 1];
        } else if (token.substr(0, token.length() - 1) == "--key=") {
            size_t sepIndex = token.find(',');
            if (sepIndex == std::string::npos) {
                key_field1 = std::stoi(token) - 1;
                key_field2 = key_field1;
            } else {
                key_field1 = std::stoi(token.substr(0, sepIndex + 1)) - 1;
                key_field2 = std::stoi(token.substr(sepIndex + 1)) - 1;
            }
        }
    }

    std::ifstream in(argv[1]);
    std::vector<my_table> table;
    std::string line;

    while (std::getline(in, line)) {
        table.emplace_back(line, split_by_sep(line, sep));
    }

    std::sort(table.begin(), table.end(),
              [&key_field1, &key_field2](const my_table &x, const my_table &y) {
                  for (std::size_t column_ind = key_field1; column_ind <= key_field2; column_ind++) {
                      if (x.splitted[column_ind] == y.splitted[column_ind]) {
                          continue;
                      }
                      if (column_ind >= y.splitted.size() || column_ind >= x.splitted.size()) {
                          return x.splitted.size() <= column_ind;
                      } else {
                          return y.splitted[column_ind] > x.splitted[column_ind];
                      }
                  }
                  return x.line < y.line;
              }
    );

    for (auto &i : table) {
        std::cout << i.line << "\n";
    }
}

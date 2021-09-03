#include "searcher.h"

#include <algorithm>
#include <istream>
#include <sstream>

bool Searcher::is_good_char(char current) const
{
    return static_cast<bool>(std::isalpha(static_cast<unsigned char>(current))) || static_cast<bool>(std::isdigit(static_cast<unsigned char>(current)));
}

void Searcher::split_line(const std::string & line, std::vector<std::string> & words_list) const
{
    for (size_t i = 0; i < line.size(); i++) {
        while (!is_good_char(line[i]) && i < line.size()) {
            ++i;
        }

        int begin = i;
        while (i < line.size() - 1 && !std::isspace(line[i + 1])) {
            ++i;
        }

        int end = i;
        while (end >= 0 && !is_good_char(line[end])) {
            end--;
        }
        std::string word = line.substr(begin, end - begin + 1);
        std::transform(word.begin(), word.end(), word.begin(), [](unsigned char c) { return std::tolower(c); });
        if (!word.empty()) {
            words_list.push_back(word);
        }
    }
}

void Searcher::add_document(const Searcher::Filename & filename, std::istream & strm)
{
    filenames.push_back(filename);
    size_t starting_position = 0;
    std::map<std::string, std::set<int>> word_positions;
    std::string line;
    while (std::getline(strm, line)) {
        std::vector<std::string> words_list;
        split_line(line, words_list);

        for (size_t i = 0; i < words_list.size(); i++) {
            word_positions[words_list[i]].insert(i + starting_position);
        }
        starting_position += words_list.size();
    }

    for (auto & it : word_positions) {
        words[it.first].emplace(filename, it.second);
    }
}

void Searcher::remove_document(const Searcher::Filename & filename)
{
    for (const auto & pair : words) {
        words[pair.first].erase(filename);
    }
}

void Searcher::parse_query(const std::string & query, std::vector<std::string> & single_words, std::vector<std::vector<std::string>> & phrases) const
{
    size_t cnt = std::count(query.begin(), query.end(), '"');
    size_t i;
    for (i = 0; i < query.size(); i++) {
        if (is_good_char(query[i])) {
            break;
        }
    }
    if (cnt % 2 == 1 || i == query.size()) {
        throw BadQuery("Unclosed \"", query);
    }

    size_t j = 0;
    bool starting_hard = false;
    cnt = 0;
    while (j < query.size() && !is_good_char(query[j])) {
        if (query[j++] == '"') {
            cnt++;
        }
    }
    if (cnt % 2 != 0) {
        starting_hard = true;
    }

    std::stringstream ss(query.substr(j));
    std::string item;

    bool tmp = starting_hard;
    while (std::getline(ss, item, '\"')) {
        if (item.empty()) {
            continue;
        }

        if (tmp) {
            std::vector<std::string> words_list;
            split_line(item, words_list);
            if (!words_list.empty()) {
                phrases.push_back(words_list);
            }
        }
        else {
            split_line(item, single_words);
        }
        tmp = !tmp;
    }
}

bool Searcher::index_contains_all_words(const std::vector<std::string> & single_words, const std::vector<std::vector<std::string>> & phrases) const
{
    for (auto & word : single_words) {
        if (words.find(word) == words.end()) {
            return false;
        }
    }
    for (auto & phrase : phrases) {
        for (auto & word : phrase) {
            if (words.find(word) == words.end()) {
                return false;
            }
        }
    }
    return true;
}

std::pair<Searcher::DocIterator, Searcher::DocIterator> Searcher::search(const std::string & query) const
{
    std::vector<std::string> single_words;
    std::vector<std::vector<std::string>> phrases;
    parse_query(query, single_words, phrases);
    if (!index_contains_all_words(single_words, phrases)) {
        auto empty_iter = DocIterator(std::make_shared<std::set<std::string>>());
        return {empty_iter, empty_iter};
    }

    std::vector<std::string> files = filenames;
    files = check_single(single_words, filenames);
    files = check_phrases(phrases, files);

    std::shared_ptr<std::set<std::string>> ans_ptr = std::make_shared<std::set<std::string>>(files.begin(), files.end());
    return {DocIterator(ans_ptr->begin(), ans_ptr), DocIterator(ans_ptr->end(), ans_ptr)};
}

std::vector<std::string> Searcher::check_single(const std::vector<std::string> & single_words, const std::vector<std::string> & files) const
{
    std::vector<std::string> res;
    if (single_words.empty()) {
        return files;
    }

    for (auto & file : files) {
        bool contains_all = true;
        for (const auto & word : single_words) {
            if (words.at(word).find(file) == words.at(word).end()) {
                contains_all = false;
                break;
            }
        }
        if (contains_all) {
            res.push_back(file);
        }
    }

    return res;
}

std::vector<std::string> Searcher::check_phrases(const std::vector<std::vector<std::string>> & phrases, std::vector<std::string> files) const
{
    if (phrases.empty()) {
        return files;
    }

    for (const auto & phrase : phrases) {
        std::vector<std::string> good_files;

        auto fls = check_single(phrase, files);
        for (const auto & file : fls) {
            auto first = phrase[0];
            for (auto pos : words.at(first).at(file)) {
                int begin = pos;

                for (size_t i = 1; i < phrase.size(); i++) {
                    std::string word = phrase[i];
                    if (words.at(word).at(file).find(pos + 1) == words.at(word).at(file).end()) {
                        break;
                    }
                    pos++;
                }
                if (pos == begin + static_cast<int>(phrase.size()) - 1) {
                    good_files.push_back(file);
                    break;
                }
            }
        }
        files = good_files;
    }
    return files;
}
#pragma once

#include <functional>
#include <list>
#include <map>
#include <memory>
#include <set>
#include <string>
#include <utility>

class Searcher
{
public:
    using Filename = std::string;
    using FileIndex = std::pair<std::string, std::set<int>>;

    // index modification
    void add_document(const Filename & filename, std::istream & strm);

    void remove_document(const Filename & filename);

    // queries
    class DocIterator
    {
    public:
        using difference_type = std::ptrdiff_t;
        using value_type = Filename;
        using pointer = const value_type *;
        using reference = const value_type &;
        using iterator_category = std::forward_iterator_tag;

        DocIterator(const std::shared_ptr<std::set<std::string>> & files)
            : m_it(files->begin())
            , files_ptr(files)
        {
        }

        DocIterator(const std::set<std::string>::iterator it, std::shared_ptr<std::set<std::string>> files)
            : m_it(it)
            , files_ptr(std::move(files))
        {
        }

        reference operator*() const { return *m_it; }

        pointer operator->() const { return &*m_it; }

        DocIterator & operator++()
        {
            m_it++;
            return *this;
        }

        DocIterator operator++(int)
        {
            auto tmp = *this;
            m_it++;
            return tmp;
        }

        bool operator==(const DocIterator & other) const { return m_it == other.m_it; }

        bool operator!=(const DocIterator & other) const { return !(*this == other); }

    private:
        std::set<Filename>::iterator m_it;
        std::shared_ptr<std::set<Filename>> files_ptr;
    };

    class BadQuery : public std::exception
    {
    private:
        std::string m_message;

    public:
        BadQuery(const std::string & message, const std::string & query)
            : m_message("Search query syntax error: " + message + " in query: " + query)
        {
        }

        const char * what() const noexcept override
        {
            return m_message.c_str();
        }
    };

    std::pair<DocIterator, DocIterator> search(const std::string & query) const;

private:
    std::unordered_map<std::string, std::unordered_map<std::string, std::set<int>>> words;
    std::vector<std::string> filenames;
    bool is_good_char(char current) const;

    void split_line(const std::string & line, std::vector<std::string> & words_list) const;
    std::vector<std::string> check_phrases(const std::vector<std::vector<std::string>> & phrases, std::vector<std::string> files) const;
    std::vector<std::string> check_single(const std::vector<std::string> & single_words, const std::vector<std::string> & files) const;
    void parse_query(const std::string & query, std::vector<std::string> & single_words, std::vector<std::vector<std::string>> & phrases) const;
    bool index_contains_all_words(const std::vector<std::string> & single_words, const std::vector<std::vector<std::string>> & phrases) const;
};

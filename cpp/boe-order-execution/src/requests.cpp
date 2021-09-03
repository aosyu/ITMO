#include "requests.h"

#include <ios>
namespace {

void encode_new_order_opt_fields(unsigned char * bitfield_start,
                                 const double price,
                                 const char ord_type,
                                 const char time_in_force,
                                 const unsigned max_floor,
                                 const std::string & symbol,
                                 const char capacity,
                                 const std::string & account)
{
    auto * p = bitfield_start + new_order_bitfield_num();
#define FIELD(name, bitfield_num, bit)                    \
    set_opt_field_bit(bitfield_start, bitfield_num, bit); \
    p = encode_field_##name(p, name);
#include "new_order_opt_fields.inl"
}

uint8_t encode_request_type(const RequestType type)
{
    switch (type) {
    case RequestType::New:
        return 0x38;
    }
    return 0;
}

unsigned char * add_request_header(unsigned char * start, unsigned length, const RequestType type, unsigned seq_no)
{
    *start++ = 0xBA;
    *start++ = 0xBA;
    start = encode(start, static_cast<uint16_t>(length));
    start = encode(start, encode_request_type(type));
    *start++ = 0;
    return encode(start, (seq_no));
}

char convert_side(const Side side)
{
    switch (side) {
    case Side::Buy: return '1';
    case Side::Sell: return '2';
    }
    return 0;
}

char convert_ord_type(const OrdType ord_type)
{
    switch (ord_type) {
    case OrdType::Market: return '1';
    case OrdType::Limit: return '2';
    case OrdType::Pegged: return 'P';
    }
    return 0;
}

char convert_time_in_force(const TimeInForce time_in_force)
{
    switch (time_in_force) {
    case TimeInForce::Day: return '0';
    case TimeInForce::IOC: return '3';
    case TimeInForce::GTD: return '6';
    }
    return 0;
}

char convert_capacity(const Capacity capacity)
{
    switch (capacity) {
    case Capacity::Agency: return 'A';
    case Capacity::Principal: return 'P';
    case Capacity::RisklessPrincipal: return 'R';
    }
    return 0;
}

} // anonymous namespace

std::array<unsigned char, calculate_size(RequestType::New)> create_new_order_request(const unsigned seq_no,
                                                                                     const std::string & cl_ord_id,
                                                                                     const Side side,
                                                                                     const double volume,
                                                                                     const double price,
                                                                                     const OrdType ord_type,
                                                                                     const TimeInForce time_in_force,
                                                                                     const double max_floor,
                                                                                     const std::string & symbol,
                                                                                     const Capacity capacity,
                                                                                     const std::string & account)
{
    static_assert(calculate_size(RequestType::New) == 78, "Wrong New Order message size");

    std::array<unsigned char, calculate_size(RequestType::New)> msg;
    auto * p = add_request_header(&msg[0], msg.size() - 2, RequestType::New, seq_no);
    p = encode_text(p, cl_ord_id, 20);
    p = encode_char(p, convert_side(side));
    p = encode_binary4(p, static_cast<uint32_t>(volume));
    p = encode(p, static_cast<uint8_t>(new_order_bitfield_num()));
    encode_new_order_opt_fields(p,
                                price,
                                convert_ord_type(ord_type),
                                convert_time_in_force(time_in_force),
                                max_floor,
                                symbol,
                                convert_capacity(capacity),
                                account);
    return msg;
}

std::string decode_all_non_null(const std::vector<unsigned char> & message, int & start_index, int size)
{
    std::string res = {reinterpret_cast<const char *>(&message[start_index]), reinterpret_cast<const char *>(&message[start_index + size])};
    start_index += size;
    res.erase(std::remove(res.begin(), res.end(), 0), res.end());
    return res;
}

double to_decimal_number(const std::vector<unsigned char> & message, int size, int & begin_index)
{
    double res = 0;
    for (int i = 0; i < size; i++) {
        res += (message[i + begin_index]) * (1ull << (8ull * i));
    }
    begin_index += size;
    return res;
}

unsigned char to_base36(int number)
{
    if (number < 10) {
        return ('0' + number);
    }
    return ('A' + (number - 10));
}

std::string from_decimal_to_any(unsigned long long dex_number, int radix)
{
    std::string res;
    while (dex_number > 0) {
        res += to_base36(dex_number % radix);
        dex_number /= radix;
    }
    std::reverse(res.begin(), res.end());
    return res;
}

LiquidityIndicator decode_liquidity_indicator(const std::vector<unsigned char> & message, const int & index)
{
    if (message[index] == 'R') {
        return LiquidityIndicator::Removed;
    }
    return LiquidityIndicator::Added;
}

ExecutionDetails decode_order_execution(const std::vector<unsigned char> & message)
{
    ExecutionDetails exec_details;
    const unsigned char begin_index_of_optional_fields = 70 + message[69];
    int index = 18;

    exec_details.cl_ord_id = decode_all_non_null(message, index, 20);

    unsigned long long dex_number = to_decimal_number(message, 8, index);
    exec_details.exec_id = from_decimal_to_any(dex_number, 36);

    exec_details.filled_volume = to_decimal_number(message, 4, index);

    exec_details.price = to_decimal_number(message, 8, index) / 10000;

    exec_details.active_volume = to_decimal_number(message, 4, index);

    exec_details.liquidity_indicator = decode_liquidity_indicator(message, index += 4);

    index = begin_index_of_optional_fields;
    exec_details.symbol = decode_all_non_null(message, index, 8);

    exec_details.last_mkt = decode_all_non_null(message, index, 4);

    exec_details.fee_code = decode_all_non_null(message, index, 2);

    return exec_details;
}

std::vector<unsigned char> request_optional_fields_for_message(const ResponseType)
{
    return {0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x80, 0x01};
}

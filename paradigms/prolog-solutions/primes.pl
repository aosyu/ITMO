init(MAX_N) :- init(2, MAX_N).

init(X, MAX_N) :-
	XX is X * X, 
	XX > MAX_N, !.
	
init(X, MAX_N) :- 
	composite(X),
	Next is X + 1,
	init(Next, MAX_N).
	
init(X, MAX_N) :- 
	XX is X * X, 
	sieve(XX, X, MAX_N), 
	Next is X + 1, 
	init(Next, MAX_N).

sieve(Xx, X, MAX_N) :- MAX_N < Xx, !.

sieve(Xx, X, MAX_N) :- 
	assert(divisors(Xx, X)), 
	Next is Xx + X, 
	sieve(Next, X, MAX_N).

prime(N) :- \+ divisors(N, _).

composite(N) :- divisors(N, _).

%-----------------------prime_divisors----------------------%


prime_divisors(1, []) :- !.

prime_divisors(N, [N]) :- prime(N), !.
	
prime_divisors(N, Divisors) :- 
	number(N), 
	check_divisors(N, Divisors, 1), !.

prime_divisors(N, Divisors) :- 
	check_divisors(N, Divisors, 1, 1), !.


check_divisors(N, [N], _) :- prime(N), !.

check_divisors(N, [], N, _) :- !.

check_divisors(N, [H | T], _) :- 
	number(N),
	divisors(N, H),
	Nn is div(N, H),
	check_divisors(Nn, T, H).

check_divisors(N, [H | T], Res, Last_Divisor) :- 
	Last_Divisor =< H, 
	New is Res * H, 
	check_divisors(N, T, New, H).

%--------------------------gcd------------------------------%

get_gcd(L, [], Res, Gcd):- Gcd is Res, !.
get_gcd([], L, Res, Gcd):- Gcd is Res, !.
get_gcd([], [], Res, Gcd):- Gcd is Res, !.

get_gcd([H|Ta], [H|Tb], Res, Gcd):- 
	New_res = Res * H,
	get_gcd(Ta, Tb, New_res, Gcd).
get_gcd([Ha|Ta], [Hb|Tb], Res, Gcd):- Ha < Hb, get_gcd(Ta, [Hb|Tb], Res, Gcd).
get_gcd([Ha|Ta], [Hb|Tb], Res, Gcd):- Ha > Hb, get_gcd([Ha|Ta], Tb, Res, Gcd).

gcd(A, B, Gcd):-
	prime_divisors(A, List_a),
	prime_divisors(B, List_b),
	get_gcd(List_a, List_b, 1, Gcd), !. 
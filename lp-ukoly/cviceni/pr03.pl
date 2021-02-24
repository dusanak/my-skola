pocet([], 0).
pocet([_|T], P) :- pocet(T, P1), P is 1 + P1.

suma([], 0).
suma([H|T], P) :- suma(T, P1), P is H + P1.

hledej([], _, false).
hledej([X|_], X, true).
hledej([H|T], X, Result) :- H \= X, hledej(T, X, Result).

prvek(X, [X|_]).
prvek(Y, [_|Z]) :- prvek(Y, Z).

liche([], []).
liche([H|T1], [H|T2]) :- 1 is H mod 2, liche(T1, T2).
liche([H|T1], T2) :- 0 is H mod 2, liche(T1, T2).

h(a, b, 1).
h(b, c, 2).
h(a, d, 3).
h(d, c, 1).

cesta(Z, Do, C) :- h(Z, Do, C).
cesta(Z, Do, C) :- h(Z, X, C1), cesta(X, Do, C2), C is C1 + C2.
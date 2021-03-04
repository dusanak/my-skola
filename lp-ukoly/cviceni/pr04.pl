h(a, b, 1).
h(b, c, 2).
h(a, d, 3).
h(d, c, 1).

cesta(Z, Do, C, []) :- h(Z, Do, C).
cesta(Z, Do, C, [H|T]) :- h(Z, H, C1), cesta(H, Do, C2, [T]), C is C1 + C2.

s(1, 1).
s(2, 1).
s(3, 1).
s(1, 2).
s(2, 2).
s(3, 2).
s(1, 3).
s(2, 3).
s(3, 3).

low(-2).
high(2).

isInChessboard(X, Y) :- low(A), high(B), A =< X, A =< Y, X =< B, Y =< B.

move([X, Y], [X1, Y1]) :- isInChessboard(X, Y), X1 is X + 1, Y1 is Y + 2, isInChessboard(X1, Y1).
move([X, Y], [X1, Y1]) :- isInChessboard(X, Y), X1 is X - 1, Y1 is Y + 2, isInChessboard(X1, Y1).
move([X, Y], [X1, Y1]) :- isInChessboard(X, Y), X1 is X + 1, Y1 is Y - 2, isInChessboard(X1, Y1).
move([X, Y], [X1, Y1]) :- isInChessboard(X, Y), X1 is X - 1, Y1 is Y - 2, isInChessboard(X1, Y1).
move([X, Y], [X1, Y1]) :- isInChessboard(X, Y), X1 is X + 2, Y1 is Y + 1, isInChessboard(X1, Y1).
move([X, Y], [X1, Y1]) :- isInChessboard(X, Y), X1 is X - 2, Y1 is Y + 1, isInChessboard(X1, Y1).
move([X, Y], [X1, Y1]) :- isInChessboard(X, Y), X1 is X + 2, Y1 is Y - 1, isInChessboard(X1, Y1).
move([X, Y], [X1, Y1]) :- isInChessboard(X, Y), X1 is X - 2, Y1 is Y - 1, isInChessboard(X1, Y1).

path(X, Y, P) :- path(X, Y, [X], P).
path(X, X, P, R) :- R = P.
path(X, Y, P, R) :- move(X, Z), not(member(Z, P)), path(Z, Y, [Z|P], R).
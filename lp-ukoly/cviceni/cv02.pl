printNum(1) :- write(1), nl.
printNum(A) :- A > 1, B is A - 1, printNum(B), write(A), nl.

suma(1, 1).
suma(X, Y) :- X > 1, X1 is X - 1, suma(X1, Y1), Y is X + Y1.
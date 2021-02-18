c(1).
c(2).
c(3).
c(4).

ocisluj(A, B, C, D, E, F, G, H, I) :- 
    c(A), c(B), c(C), R1 is A + B + C,
    c(D), c(G),       S1 is A + D + G, R1 = S1,
    c(E), c(F),       R2 is D + E + F,
    c(H),             S2 is B + E + H, R2 = S2,
    c(I),             R3 is G + H + I,
                      S3 is C + F + I, R3 = S3.

b(1).
b(2).
b(3).
b(4).

obarvi(A, B, C, D, E, F) :-
    b(A), b(B), A \= B,
    b(C),       A \= C,
    b(D),       A \= D, B \= D, C \= D,
    b(E),       C \= E, D \= E,
    b(F),       B \= F, D \= F, E \= F.

fact(0, 1).
fact(A, B) :- A > 0, A1 is A - 1, fact(A1, C), B is A * C.

fib(0, 1).
fib(1, 1).
fib(A, B) :- A > 1, A1 is A - 1, A2 is A - 2, fib(A1, B1), fib(A2, B2), B is B1 + B2. 
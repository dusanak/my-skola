hanoi(N) :- move(N, src, dest, help).
info(X, Y) :- write([move, from, X, to, Y]).
move(0, _, _, _) :- !.
move(N, A, B, C) :- M is N-1, move(M, A, C, B), info(A, B), move(M, C, B, A).

range(I, I, [I]).
range(I, K, [I|L]) :- I < K, I1 is I + 1, range(I1, K, L).

dupli([], []).
dupli([H1|T1], [H1, H1|T2]) :- dupli(T1, T2).

my_reverse(L1, L2) :- my_rev(L1, L2, []).
my_rev([], L2, L2).
my_rev([X|Xs], L2, Acc) :- my_rev(Xs, L2, [X|Acc]).

list_equality([], []).
list_equality([H1|T1], [H1|T2]) :- list_equality(T1, T2).

palindrome(L1) :- my_reverse(L1, L2), list_equality(L1, L2).

pali_smart(L1) :- my_reverse(L1, L1).


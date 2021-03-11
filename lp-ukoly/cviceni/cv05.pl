isSorted([]).
isSorted([_]).
isSorted([A, B|T]) :- A =< B, isSorted([B|T]).

mergesort([], []).
mergesort([X], [X]).
mergesort(X, Y) :- halve(X, X1, X2), mergesort(X1, Y1), mergesort(X2, Y2), merge(Y1, Y2, Y).

halve([], [], []).
halve([X], [X], []).
halve([H1, H2|T], T1, T2) :- halve(T, Y1, Y2), T1 = [H1|Y1], T2 = [H2|Y2].

merge([], [], []).
merge([X], [], [X]).
merge([], [X], [X]).
merge([H1|T1], [H2|T2], X) :- H1 < H2, merge(T1, [H2|T2], X1), X = [H1|X1].
merge([H1|T1], [H2|T2], X) :- H1 > H2, merge([H1|T1], T2, X1), X = [H2|X1].
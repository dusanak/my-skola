drop_last([], []).
drop_last([_], []).
drop_last([H|T], [H|X]) :- drop_last(T, X).

drop_k(H, [H|T], 0, T).
drop_k(X, [H|T], K, R) :- K > 0, K1 is K - 1, drop_k(X, T, K1, R1), R = [H|R1].

isSorted([]).
isSorted([_]).
isSorted([A, B|T]) :- A =< B, isSorted([B|T]).

bubble([], []).
bubble([A], [A]).
bubble([A, B|T], [A|X]) :- A =< B, bubble([B|T], X).
bubble([A, B|T], [B|X]) :- A > B, bubble([A|T], X).

bubblesort(X, X) :- isSorted(X).
bubblesort(X, R) :- not(isSorted(X)), bubble(X, Y), bubblesort(Y, R).

less_than(_, [], []).
less_than(X, [H|T], [H|R]) :- H =< X, less_than(X, T, R).
less_than(X, [H|T], R) :- H > X, less_than(X, T, R).

more_than(_, [], []).
more_than(X, [H|T], R) :- H =< X, more_than(X, T, R).
more_than(X, [H|T], [H|R]) :- H > X, more_than(X, T, R).

quicksort([], []).
quicksort([X], [X]).
quicksort([H|T], R) :- less_than(H, T, Less), more_than(H, T, More),
                       quicksort(Less, R1), quicksort(More, R2),
                       append([R1, [H], R2], R).

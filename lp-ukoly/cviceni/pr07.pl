:- dynamic s/2.

generuj_pole([], _).
generuj_pole([H|X], Y) :- generuj_y(H, Y),
                          generuj_pole(X, Y).

generuj_y(X, []).
generuj_y(H, [H|Y]) :- assert(s([X, H], ' ')),
                       generuj_y(X, Y).

o(1, [X, Y], [X1, Y], [X2, Y], [X3, Y]) :- X1 is X + 1, X2 is X + 2, X3 is X + 3.
o(2, [X, Y], [X, Y1], [X, Y2], [X, Y3]) :- Y1 is Y + 1, Y2 is Y + 2, Y3 is Y + 3.
o(3, [X, Y], [X1, Y1], [X2, Y2], [X3, Y3]) :- X1 is X + 1, X2 is X + 2, X3 is X + 3,
                                              Y1 is Y + 1, Y2 is Y + 2, Y3 is Y + 3.
o(4, [X, Y], [X1, Y1], [X2, Y2], [X3, Y3]) :- X1 is X + 1, X2 is X + 2, X3 is X + 3,
                                              Y1 is Y - 1, Y2 is Y - 2, Y3 is Y - 3.

umisti(ID, S1) :- s(S1, _),
                  o(ID, S1, S2, S3, S4), 
                  s(S2, _), s(S3, _), s(S4, _),
                  retract(s(S1, _)), assert(s(S1, x)),
                  retract(s(S2, _)), assert(s(S2, x)),
                  retract(s(S3, _)), assert(s(S3, x)),
                  retract(s(S4, _)), assert(s(S4, x)).

%Samotny vypis pole
%vypis_pole(SOurX, OtoceneSourY).

vypis_pole() :- zjisti_sour(x, SX),
              zjisti_sour(y, SY),
              length(SX, PocX),
              nl, write('   '), vypis_f(PocX)

              reverse(SY, RSY),

              vypis_pole(SX, RSY),

              nl, write('   '),
              vypis_l(SX).

zjisti_sour(x, SX) :- findall(X, s([X, _], _), ListX),
                      sort(ListX, SX).

zjisti_sour(y, SY) :- findall(Y, s([_, Y], _), ListY),
                      sort(ListY, SY).

vypis_f(0).
vypis_f(PocX) :- write(' ---'),
                 PocX1 is PocX - 1,
                 vypis_f(PocX1).

vypis_l([]).
vypis_l([H|T]) :- write(' '), write(H), write('  '),
                  vypis_l(T).

najdi_o(R) :- s(S1, x),
              o(ID, S1, S2, S3, S4),
              s(S2, x), s(S3, x), s(S4, x),
              R = [ID, S1, S2, S3, S4].

vypis_s([]).
vypis_s([H|T]) :- nl,
                  write(H),
                  vypis_s(T).
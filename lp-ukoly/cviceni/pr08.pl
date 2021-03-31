:- dynamic s/2.

generuj_pole([],_).                              %v teto rekurzi generujeme sloupce
generuj_pole([H|X],Y) :- generuj_y(H,Y),
                         generuj_pole(X,Y).


generuj_y(_,[]).                                 % zde generujeme radky
generuj_y(X,[H|Y]) :- assert(s([X,H],' ')),
                      generuj_y(X,Y).


vypis_pole  :- zjisti_sour(x,SX),               %zjisti sour X
               zjisti_sour(y,SY),               %zjisti sour Y
               length(SX,PocX),                 %zjisti pocet souradnic X
               nl, write('   '), vypis_f(PocX), % vypsani horniho ohraniceno tabulky

               reverse(SY,RSY),                 %otocim poradi souradnic Y - bude se
                                                %vypisovat od nejvyssi souradnice Y

               vypis_pole(SX,RSY),              %samotny vypis pole

               nl, write('   '),
               vypis_l(SX).                     %vypis hodnot osy X

vypis_pole(_,[]).
vypis_pole(SX,[Y|SY]) :- nl, write(Y), write(' |'),   %vypis cislo radku (hodnota souradnice Y)

                         vypis_radek(SX,Y),           %vypis radek souranice Y

                         nl, write('  '),             %potrhni radek
                         length(SX,XL),               %    "
                         vypis_f(XL),                 %    "

                         vypis_pole(SX,SY).

vypis_radek([],_).
vypis_radek([X|SX],Y) :- s([X,Y],H),                  % zjisti, co je na souradnici [X,Y]
                         write(' '), write(H), write(' |'),
                         vypis_radek(SX,Y).

zjisti_sour(x,SX) :- findall(X,s([X,_],_),ListX),
                     sort(ListX,SX).

zjisti_sour(y,SY) :- findall(Y,s([_,Y],_),ListY),
                     sort(ListY,SY).

vypis_f(0).
vypis_f(PocX) :- write(' ---'),
                 PocX1 is PocX-1,
                 vypis_f(PocX1).

vypis_l([]).
vypis_l([H|T])  :- write(' '), write(H), write('  '),
                   vypis_l(T).

zrus_pole :- retract(s([_,_],_)),fail.

vypis_s([]).
vypis_s([H|T]) :- nl,
                  write(H),
                  vypis_s(T).


%Objekty delky 5
o(1, [X, Y], [X1, Y], [X2, Y], [X3, Y], [X4, Y]) :- X1 is X + 1, X2 is X + 2,
                                                    X3 is X + 3, X4 is X + 4.
o(2, [X, Y], [X, Y1], [X, Y2], [X, Y3], [X, Y4]) :- Y1 is Y + 1, Y2 is Y + 2,
                                                    Y3 is Y + 3, Y4 is Y + 4.
o(3, [X, Y], [X1, Y1], [X2, Y2], [X3, Y3], [X4, 4]) :- X1 is X + 1, X2 is X + 2,
                                                       X3 is X + 3, X4 is X + 4,
                                                       Y1 is Y - 1, Y2 is Y - 2,
                                                       Y3 is Y - 3, Y4 is Y - 4.
o(4, [X, Y], [X1, Y1], [X2, Y2], [X3, Y3], [X4, 4]) :- X1 is X + 1, X2 is X + 2,
                                                       X3 is X + 3, X4 is X + 4,
                                                       Y1 is Y + 1, Y2 is Y + 2,
                                                       Y3 is Y + 3, Y4 is Y + 4.

% vyhra(Hrac, Souradnice)
vyhra(H, Souradnice) :- s(S1, H), o(ID, S1, S2, S3, S4, S5),
                        s(S2, H), s(S3, H), s(S4, H), s(S5, H),
                        Souradnice = [ID, S1, S2, S3, S4, S5].
vyhra(H, []).

% tah(Souradnice)
tah(Souradnice) :- s(Souradnice, ' '), retract(s(Souradnice, ' ')), assert(s(Souradnice, o)),
                   vyhra(o, VS), write(VS), nl, vypis_pole.

% hraje pocitac
% --------------------
% -xxx
tp :- s(S1, ' '), o(ID, S1, S2, S3, S4, S5), s(S2, x), s(S3, x), s(S4, x), s(S5, ' '),
      retract(s(S5, x)), assert(s(S5, x)),
      vyhra(x, VS), member(S5, VS),
      write(VS), write([Souradnice, 'xxxx-']), nl, vypis_pole.

% xxx-
tp :- s(S1, x), o(ID, S1, S2, S3, S4, S5), s(S2, x), s(S3, x), s(S4, x), s(S5, ' '),
      retract(s(S5, ' ')), assert(s(S5, x)),
      vyhra(x, VS), member(S5, VS),
      write(VS), write([Souradnice, 'xxxx-']), nl, vypis_pole.

% xx-x
% x-xx
% kriz
tp :- s(S1, ' '), o(ID, S1, S2, S3, S4, S5), s(S2, ' '), s(S3, x), s(S4, x), s(S5, ' '),
      s(S6, ' '), S1 \= S6, o(ID2, S6, S2, S7, S8, S9), s(S7, x), s(S8, x), s(S9, ' ')
      retract(s(S2, ' ')), assert(s(S2, x)),
      vyhra(x, VS), member(S2, VS),
      write(VS), write([Souradnice, 'xxxx-']), nl, vypis_pole.

% nahod tah
tp :- s(Souradnice, ' '), retract(s(Souradnice, ' ')), assert(s(Souradnice, x)),
      vyhra(x, VS), member(Souradnice, VS),
      write(VS), write([Souradnice, 'nahod tah']), nl, vypis_pole.

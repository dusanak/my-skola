:- dynamic s/2.
:- dynamic tah/3.
:- dynamic hra/3.

/*
 * PROGRAM HRANI PISKVOREK na 5 vyteznych v linii
 * -------------------------------------------------------------
 *
 * vygenerování pole: argumenty jsou hodnoty osy X a hodnoty
 * osy Y zapsane v seznamech, napr:
 *
 * ?- generuj_pole([0,1,2],[0,1,2,3,4]).
 * generuj_pole([5,6,7,8,9,0,1,2,3,4],[5,6,7,8,9,0,1,2,3,4]).
 *
 *
 * Vypis pole: pole je vypisovano podle aktualni situace. Prikaz si
 * zjisti, jake a kolik hodnot jsou na sach X a Y a podle toho vypise
 * pole. V levo dole je souradnice [X-min,Y-min].
 *
 * ?- vypis_pole.
 *
 * Pro vymazani (nikoli reset) pole: Vymaze se i posledni hrana hra
 * ulozena v predikatech tah/3.
 *
 * ?- zrus_pole.
 *
 * Pak je nutne znovu vygenerovat nove hraci pole.
 *
 * Tah hrace: zadani konkretni souradnice [X,Y], na kterou se hraje
 * kolecko "o"
 *
 * ?-tah([X,Y]).
 *
 * Tah pocitace:
 * ?-tp.
 *
 * Zetne prehrani hry: Mezi jednotlivymi tahy se ceka na libovolny
 * vstup z klavesnice ukoncen teckou a enterem
 *
 * ?-hra.
 *
 *
 * PRO SPRAVNE ZOBRAZENI JE NUTNE NASTAVIT NEPROPORCIONALNI PISMO, TREBA
 * 'COURIER NEW', JINAK BUDE VYPIS POLE "ROZHAZEN"
 *
 */

% Generovani pole
% ---------------
%
% Vnejsi rekurze generuje sloupce a vnitrni rekurze generuje radky.
%
% generuj_pole([X-souradnice],[Y-souradnice]).

generuj_pole([],_).                              %v teto rekurzi generujeme sloupce
generuj_pole([H|X],Y) :- generuj_y(H,Y),
                         generuj_pole(X,Y).


generuj_y(_,[]).                                 % zde generujeme radky
generuj_y(X,[H|Y]) :- assert(s([X,H],' ')),
                      generuj_y(X,Y).


% Vypis pole
% ----------
% Prvni je nutne zjistit, jake souradnice (hodnoty souradnic) X a Y
% pouzivame.
%
% Pak potrebujeme pocet souradnic X, abychom vedeli, kolik bude bunek na
% kazdem radku.
% Prikaz 'sort/2' utridi seznam a vyhodi duplicity -> zjistime jedinecne
% hodnoty a tim i pocet pomoci prikazu 'length/2'.
%
% Diky tomu, ze se bude pole vypisovat shora dolu, tak je nutne otocit
% souradnice Y. Souradnice [X-min, Y-min] se nachazi v levem dolnim
% rohu.
%
% Priklad vypisu prazdneho pole X=[0,1,2], Y=[0,1,2,3]
%
%    --- --- ---
% 3 |   |   |   |
%    --- --- ---
% 2 |   |   |   |
%    --- --- ---
% 1 |   |   |   |
%    --- --- ---
% 0 |   |   |   |
%    --- --- ---
%     0   1   2

vypis_pole  :- zjisti_sour(x,SX),               %zjisti sour X
               zjisti_sour(y,SY),               %zjisti sour Y
               length(SX,PocX),                 %zjisti pocet souradnic X
               nl, write('  '), vypis_f(PocX), % vypsani horniho ohraniceno tabulky

               reverse(SY,RSY),                 %otocim poradi souradnic Y - bude se
                                                %vypisovat od nejvyssi souradnice Y

               vypis_pole(SX,RSY),              %samotny vypis pole

               nl, write('   '),
               vypis_l(SX).                     %vypis hodnot osy X




%Samotny vypis pole
%vypis_pole([SouradniceX],[OtoceneSouradniceY])

vypis_pole(_,[]).
vypis_pole(SX,[Y|SY]) :- nl, write(Y), write(' |'),   %vypis cislo radku (hodnota souradnice Y)

                         vypis_radek(SX,Y),           %vypis radek souranice Y

                         nl, write('  '),             %podtrhni radek
                         length(SX,XL),               %    "
                         vypis_f(XL),                 %    "

                         vypis_pole(SX,SY).

%vypis_radek([SouradniceX],RadekY).
vypis_radek([],_).
vypis_radek([X|SX],Y) :- s([X,Y],H),                  % zjisti, co je na souradnici [X,Y]
                         write(' '), write(H), write(' |'),
                         vypis_radek(SX,Y).



% Zjisteni vsech pouzitych souradnic
% ----------------------------------
% Zjisti se vsechny souradnice a nasledne
% prikaz 'sort/2' jej setridi od nejmensi po nejvetsi a vypusti
% duplicity. Zajimaji nas jen hodnoty  souradnic, nikoli, co na
% nich lezi.
%
% zjist_sour(Osa, [Souradnice])

zjisti_sour(x,SX) :- findall(X,s([X,_],_),ListX),
                     sort(ListX,SX).

zjisti_sour(y,SY) :- findall(Y,s([_,Y],_),ListY),
                     sort(ListY,SY).

%vypis horniho oraniceni tabulky
vypis_f(0).
vypis_f(PocX) :- write(' ---'),
                 PocX1 is PocX-1,
                 vypis_f(PocX1).

%vypis posledniho radku - hodnoty souradnic X
vypis_l([]).
vypis_l([H|T])  :- write(' '), write(H), write('  '),
                   vypis_l(T).


% Zrus pole
% =========
% Zrusi cele pole, pak je nutne jej znovy vygenerovat.

zrus_pole :- retractall(s(_,_)),
             retractall(tah(_,_,_)).



% Objekty delky 5
% ---------------
% Tyto objekty specifikuji vsechny 4 smery na hraci plose o delce 5

o(1,[X,Y],[X1,Y],[X2,Y],[X3,Y],[X4,Y]) :- X1 is X+1, X2 is X+2, X3 is X+3, X4 is X+4.

o(2,[X,Y],[X,Y1],[X,Y2],[X,Y3],[X,Y4]) :- Y1 is Y+1, Y2 is Y+2, Y3 is Y+3, Y4 is Y+4.

o(3,[X,Y],[X1,Y1],[X2,Y2],[X3,Y3],[X4,Y4]) :- X1 is X+1, X2 is X+2, X3 is X+3, X4 is X+4,
                                              Y1 is Y-1, Y2 is Y-2, Y3 is Y-3, Y4 is Y-4.

o(4,[X,Y],[X1,Y1],[X2,Y2],[X3,Y3],[X4,Y4]) :- X1 is X+1, X2 is X+2, X3 is X+3, X4 is X+4,
                                              Y1 is Y+1, Y2 is Y+2, Y3 is Y+3, Y4 is Y+4.



% Testovani vyhry
% ---------------
% H - hrac x/o

vyhra(H,Sour) :- s(S1,H), o(ID,S1,S2,S3,S4,S5),
                 s(S2,H), s(S3,H), s(S4,H), s(S5,H),
                 Sour = [ID, S1, S2, S3, S4, S5].

vyhra(_,[]).


% Mapuj pole
% ----------
%
% zjisti, jak vypada hraci pole a uloz informaci o hraci, souradnici, na
% kterou tahl a konfigurace pole pred danym tahem

mapuj_pole(ID_hrac,S) :- findall([X,Y],s(X,Y),Pole),     % zmapuj pole
                         sort(Pole,SPole),               % utrid pole
                         assert(tah(ID_hrac,S,SPole)).   % uloz dany tah




% Hraje hrac - clovek
% ===================
%
tah(S) :- s(S,' '),
          mapuj_pole(o,S),
          retract(s(S,' ')), assert(s(S,o)),
          vyhra(o,VS),
          write(VS), nl,
          vypis_pole.

zrus(S) :- retract(s(S, _)), assert(s(S, ' ')),
           retract(tah(_, S, _)),
           vypis_pole.


% hraje pocitac
% --------------------
%vitezstvi

% -xxxx
tp :- s(S1, ' '), o(ID, S1, S2, S3, S4, S5), s(S2, x), s(S3, x), s(S4, x), s(S5, x),
      mapuj_pole(x, S1),
      retract(s(S1, ' ')), assert(s(S1, x)),
      vyhra(x, VS),
      write(VS), write([S1, '-xxxx']), nl, vypis_pole.
% xxxx-
tp :- s(S1, x), o(ID, S1, S2, S3, S4, S5), s(S2, x), s(S3, x), s(S4, x), s(S5, ' '),
      mapuj_pole(x, S5),
      retract(s(S5, ' ')), assert(s(S5, x)),
      vyhra(x, VS),
      write(VS), write([S5, 'xxxx-']), nl, vypis_pole.

% x-xxx
tp :- s(S1, x), o(ID, S1, S2, S3, S4, S5), s(S2, ' '), s(S3, x), s(S4, x), s(S5, x),
      mapuj_pole(x, S2),
      retract(s(S2, ' ')), assert(s(S2, x)),
      vyhra(x, VS),
      write(VS), write([S2, 'x-xxx']), nl, vypis_pole.
% xxx-x
tp :- s(S1, x), o(ID, S1, S2, S3, S4, S5), s(S2, x), s(S3, x), s(S4, ' '), s(S5, x),
      mapuj_pole(x, S4),
      retract(s(S4, ' ')), assert(s(S4, x)),
      vyhra(x, VS),
      write(VS), write([S4, 'xxx-x']), nl, vypis_pole.
% xx-xx
tp :- s(S1, x), o(ID, S1, S2, S3, S4, S5), s(S2, x), s(S3, ' '), s(S4, x), s(S5, x),
      mapuj_pole(x, S3),
      retract(s(S3, ' ')), assert(s(S3, x)),
      vyhra(x, VS),
      write(VS), write([S3, 'xx-xx']), nl, vypis_pole.
% blokovani vyhry
% -oooo
tp :- s(S1, ' '), o(ID, S1, S2, S3, S4, S5), s(S2, o), s(S3, o), s(S4, o), s(S5, o),
      mapuj_pole(x, S1),
      retract(s(S1, ' ')), assert(s(S1, x)),
      vyhra(x, VS),
      write(VS), write([S1, '-oooo']), nl, vypis_pole.
% oooo-
tp :- s(S1, o), o(ID, S1, S2, S3, S4, S5), s(S2, o), s(S3, o), s(S4, o), s(S5, ' '),
      mapuj_pole(x, S5),
      retract(s(S5, ' ')), assert(s(S5, x)),
      vyhra(x, VS),
      write(VS), write([S5, 'oooo-']), nl, vypis_pole.

% o-ooo
tp :- s(S1, o), o(ID, S1, S2, S3, S4, S5), s(S2, ' '), s(S3, o), s(S4, o), s(S5, o),
      mapuj_pole(x, S2),
      retract(s(S2, ' ')), assert(s(S2, x)),
      vyhra(x, VS),
      write(VS), write([S2, 'o-ooo']), nl, vypis_pole.
% ooo-o
tp :- s(S1, o), o(ID, S1, S2, S3, S4, S5), s(S2, o), s(S3, o), s(S4, ' '), s(S5, o),
      mapuj_pole(x, S4),
      retract(s(S4, ' ')), assert(s(S4, x)),
      vyhra(x, VS),
      write(VS), write([S4, 'ooo-o']), nl, vypis_pole.
% oo-oo
tp :- s(S1, o), o(ID, S1, S2, S3, S4, S5), s(S2, o), s(S3, ' '), s(S4, o), s(S5, o),
      mapuj_pole(x, S3),
      retract(s(S3, ' ')), assert(s(S3, x)),
      vyhra(x, VS),
      write(VS), write([S3, 'oo-oo']), nl, vypis_pole.
% dalsi pravidla
% -xxx-
tp :- s(S1, ' '), o(ID, S1, S2, S3, S4, S5), s(S2, x), s(S3, x), s(S4, x), s(S5, ' '),
      s(S6, ' '), o(ID2, S2, S3, S4, S5, S6),
      mapuj_pole(x, S5),
      retract(s(S5, ' ')), assert(s(S5, x)),
      vyhra(x, VS),
      write(VS), write([S5, '-xxx-']), nl, vypis_pole.
% -xxx-
tp :- s(S1, ' '), o(ID, S1, S2, S3, S4, S5), s(S2, x), s(S3, x), s(S4, x), s(S5, ' '),
      s(S6, ' '), o(ID2, S6, S1, S2, S3, S4),
      mapuj_pole(x, S1),
      retract(s(S1, ' ')), assert(s(S1, x)),
      vyhra(x, VS),
      write(VS), write([S1, '-xxx-']), nl, vypis_pole.
% -xxx-
tp :- s(S1, ' '), o(ID, S1, S2, S3, S4, S5), s(S2, x), s(S3, x), s(S4, x), s(S5, ' '),
      mapuj_pole(x, S5),
      retract(s(S5, ' ')), assert(s(S5, x)),
      vyhra(x, VS),
      write(VS), write([S5, '-xxx-']), nl, vypis_pole.
% x-x-x
tp :- s(S1, x), o(ID, S1, S2, S3, S4, S5), s(S2, ' '), s(S3, x), s(S4, ' '), s(S5, x),
      mapuj_pole(x, S4),
      retract(s(S4, ' ')), assert(s(S4, x)),
      vyhra(x, VS),
      write(VS), write([S4, 'x-x-x']), nl, vypis_pole.
% xx-x-
tp :- s(S1, x), o(ID, S1, S2, S3, S4, S5), s(S2, x), s(S3, ' '), s(S4, x), s(S5, ' '),
      mapuj_pole(x, S3),
      retract(s(S3, ' ')), assert(s(S3, x)),
      vyhra(x, VS),
      write(VS), write([S3, 'xx-x-']), nl, vypis_pole.
% x-xx-
tp :- s(S1, x), o(ID, S1, S2, S3, S4, S5), s(S2, ' '), s(S3, x), s(S4, x), s(S5, ' '),
      mapuj_pole(x, S2),
      retract(s(S2, ' ')), assert(s(S2, x)),
      vyhra(x, VS),
      write(VS), write([S2, 'x-xx-']), nl, vypis_pole.
% -x-xx
tp :- s(S1, -), o(ID, S1, S2, S3, S4, S5), s(S2, x), s(S3, ' '), s(S4, x), s(S5, x),
      mapuj_pole(x, S3),
      retract(s(S3, ' ')), assert(s(S3, x)),
      vyhra(x, VS),
      write(VS), write([S3, '-x-xx']), nl, vypis_pole.
% -xx-x
tp :- s(S1, -), o(ID, S1, S2, S3, S4, S5), s(S2, x), s(S3, x), s(S4, ' '), s(S5, x),
      mapuj_pole(x, S4),
      retract(s(S4, ' ')), assert(s(S4, x)),
      vyhra(x, VS),
      write(VS), write([S4, '-xx-x']), nl, vypis_pole.
% blokovani
% -ooo-
tp :- s(S1, ' '), o(ID, S1, S2, S3, S4, S5), s(S2, o), s(S3, o), s(S4, o), s(S5, ' '),
      mapuj_pole(x, S5),
      retract(s(S5, ' ')), assert(s(S5, x)),
      vyhra(x, VS),
      write(VS), write([S5, '-ooo-']), nl, vypis_pole.
% o-o-o
tp :- s(S1, o), o(ID, S1, S2, S3, S4, S5), s(S2, ' '), s(S3, o), s(S4, ' '), s(S5, o),
      mapuj_pole(x, S4),
      retract(s(S4, ' ')), assert(s(S4, x)),
      vyhra(x, VS),
      write(VS), write([S4, 'o-o-o']), nl, vypis_pole.
% oo-o-
tp :- s(S1, o), o(ID, S1, S2, S3, S4, S5), s(S2, o), s(S3, ' '), s(S4, o), s(S5, ' '),
      mapuj_pole(x, S3),
      retract(s(S3, ' ')), assert(s(S3, x)),
      vyhra(x, VS),
      write(VS), write([S3, 'oo-o-']), nl, vypis_pole.
% o-oo-
tp :- s(S1, o), o(ID, S1, S2, S3, S4, S5), s(S2, ' '), s(S3, o), s(S4, o), s(S5, ' '),
      mapuj_pole(x, S2),
      retract(s(S2, ' ')), assert(s(S2, x)),
      vyhra(x, VS),
      write(VS), write([S2, 'o-oo-']), nl, vypis_pole.
% -o-oo
tp :- s(S1, ' '), o(ID, S1, S2, S3, S4, S5), s(S2, o), s(S3, ' '), s(S4, o), s(S5, o),
      mapuj_pole(x, S3),
      retract(s(S3, ' ')), assert(s(S3, x)),
      vyhra(x, VS),
      write(VS), write([S3, '-o-oo']), nl, vypis_pole.
% -oo-o
tp :- s(S1, ' '), o(ID, S1, S2, S3, S4, S5), s(S2, o), s(S3, o), s(S4, ' '), s(S5, o),
      mapuj_pole(x, S4),
      retract(s(S4, ' ')), assert(s(S4, x)),
      vyhra(x, VS),
      write(VS), write([S4, '-oo-o']), nl, vypis_pole.
% kriz
tp :- s(S1, ' '), o(ID, S1, S2, S3, S4, S5), s(S2, x), s(S3, ' '), s(S4, x), s(S5, ' '),
      s(S6, ' '), S1 \= S6, o(ID2, S6, S2, S7, S8, S9), s(S7, x), s(S8, x), s(S9, ' '),
      mapuj_pole(x, S2),
      retract(s(S2, ' ')), assert(s(S2, x)),
      vyhra(x, VS),
      write(VS), write([S2, 'kriz']), nl, vypis_pole.
% kriz
tp :- s(S1, ' '), o(ID, S1, S2, S3, S4, S5), s(S2, x), s(S3, ' '), s(S4, x), s(S5, ' '),
      s(S6, ' '), S1 \= S6, o(ID2, S6, S7, S3, S8, S9), s(S7, x), s(S8, x), s(S9, ' '),
      mapuj_pole(x, S3),
      retract(s(S3, ' ')), assert(s(S3, x)),
      vyhra(x, VS),
      write(VS), write([S3, 'kriz']), nl, vypis_pole.
% -x-x-
tp :- s(S1, ' '), o(ID, S1, S2, S3, S4, S5), s(S2, x), s(S3, ' '), s(S4, x), s(S5, ' '),
      mapuj_pole(x, S3),
      retract(s(S3, ' ')), assert(s(S3, x)),
      vyhra(x, VS),
      write(VS), write([S3, '-x-x-']), nl, vypis_pole.
% x-x--
tp :- s(S1, x), o(ID, S1, S2, S3, S4, S5), s(S2, ' '), s(S3, x), s(S4, ' '), s(S5, ' '),
      mapuj_pole(x, S2),
      retract(s(S2, ' ')), assert(s(S2, x)),
      vyhra(x, VS),
      write(VS), write([S2, 'x-x--']), nl, vypis_pole.
% --x-x
tp :- s(S1, ' '), o(ID, S1, S2, S3, S4, S5), s(S2, ' '), s(S3, x), s(S4, ' '), s(S5, x),
      mapuj_pole(x, S4),
      retract(s(S4, ' ')), assert(s(S4, x)),
      vyhra(x, VS),
      write(VS), write([S4, '--x-x']), nl, vypis_pole.
% --xx-
tp :- s(S1, ' '), o(ID, S1, S2, S3, S4, S5), s(S2, ' '), s(S3, x), s(S4, x), s(S5, ' '),
      mapuj_pole(x, S2),
      retract(s(S2, ' ')), assert(s(S2, x)),
      vyhra(x, VS),
      write(VS), write([S2, '--xx-']), nl, vypis_pole.
% -xx--
tp :- s(S1, ' '), o(ID, S1, S2, S3, S4, S5), s(S2, x), s(S3, x), s(S4, ' '), s(S5, ' '),
      mapuj_pole(x, S4),
      retract(s(S4, ' ')), assert(s(S4, x)),
      vyhra(x, VS),
      write(VS), write([S4, '-xx--']), nl, vypis_pole.
% blokovani
% -o-o-
tp :- s(S1, ' '), o(ID, S1, S2, S3, S4, S5), s(S2, o), s(S3, ' '), s(S4, o), s(S5, ' '),
      mapuj_pole(x, S3),
      retract(s(S3, ' ')), assert(s(S3, x)),
      vyhra(x, VS),
      write(VS), write([S3, '-o-o-']), nl, vypis_pole.
% o-o--
tp :- s(S1, o), o(ID, S1, S2, S3, S4, S5), s(S2, ' '), s(S3, o), s(S4, ' '), s(S5, ' '),
      mapuj_pole(x, S2),
      retract(s(S2, ' ')), assert(s(S2, x)),
      vyhra(x, VS),
      write(VS), write([S2, 'o-o--']), nl, vypis_pole.
% --o-o
tp :- s(S1, ' '), o(ID, S1, S2, S3, S4, S5), s(S2, ' '), s(S3, o), s(S4, ' '), s(S5, o),
      mapuj_pole(x, S4),
      retract(s(S4, ' ')), assert(s(S4, x)),
      vyhra(x, VS),
      write(VS), write([S4, '--o-o']), nl, vypis_pole.
% --oo-
tp :- s(S1, ' '), o(ID, S1, S2, S3, S4, S5), s(S2, ' '), s(S3, o), s(S4, o), s(S5, ' '),
      mapuj_pole(x, S2),
      retract(s(S2, ' ')), assert(s(S2, x)),
      vyhra(x, VS),
      write(VS), write([S2, '--oo-']), nl, vypis_pole.
% -oo--
tp :- s(S1, ' '), o(ID, S1, S2, S3, S4, S5), s(S2, o), s(S3, o), s(S4, ' '), s(S5, ' '),
      mapuj_pole(x, S4),
      retract(s(S4, ' ')), assert(s(S4, x)),
      vyhra(x, VS),
      write(VS), write([S4, '-oo--']), nl, vypis_pole.
% --x--
tp :- s(S1, ' '), o(ID, S1, S2, S3, S4, S5), s(S2, ' '), s(S3, x), s(S4, ' '), s(S5, ' '),
      mapuj_pole(x, S4),
      retract(s(S4, ' ')), assert(s(S4, x)),
      vyhra(x, VS),
      write(VS), write([S4, '--x--']), nl, vypis_pole.
% -x---
tp :- s(S1, ' '), o(ID, S1, S2, S3, S4, S5), s(S2, x), s(S3, ' '), s(S4, ' '), s(S5, ' '),
      mapuj_pole(x, S3),
      retract(s(S3, ' ')), assert(s(S3, x)),
      vyhra(x, VS),
      write(VS), write([S3, '-x---']), nl, vypis_pole.
% ---x-
tp :- s(S1, ' '), o(ID, S1, S2, S3, S4, S5), s(S2, ' '), s(S3, ' '), s(S4, x), s(S5, ' '),
      mapuj_pole(x, S3),
      retract(s(S3, ' ')), assert(s(S3, x)),
      vyhra(x, VS),
      write(VS), write([S3, '---x-']), nl, vypis_pole.
% _-xo_
tp :- s(S1, _), o(ID, S1, S2, S3, S4, S5), s(S2, ' '), s(S3, x), s(S4, o), s(S5, _),
      mapuj_pole(x, S2),
      retract(s(S2, ' ')), assert(s(S2, x)),
      vyhra(x, VS),
      write(VS), write([S2, '_-xo_']), nl, vypis_pole.
% _ox-_
tp :- s(S1, _), o(ID, S1, S2, S3, S4, S5), s(S2, o), s(S3, x), s(S4, ' '), s(S5, _),
      mapuj_pole(x, S4),
      retract(s(S4, ' ')), assert(s(S4, x)),
      vyhra(x, VS),
      write(VS), write([S4, '_ox-_']), nl, vypis_pole.
% _-x__
tp :- s(S1, _), o(ID, S1, S2, S3, S4, S5), s(S2, ' '), s(S3, x), s(S4, _), s(S5, _),
      mapuj_pole(x, S2),
      retract(s(S2, ' ')), assert(s(S2, x)),
      vyhra(x, VS),
      write(VS), write([S2, '_-x__']), nl, vypis_pole.
% __x-_
tp :- s(S1, _), o(ID, S1, S2, S3, S4, S5), s(S2, _), s(S3, x), s(S4, ' '), s(S5, _),
      mapuj_pole(x, S4),
      retract(s(S4, ' ')), assert(s(S4, x)),
      vyhra(x, VS),
      write(VS), write([S4, '__x-_']), nl, vypis_pole.
% nahod tah
tp :- s(S, ' '),
      mapuj_pole(x, S),
      retract(s(S, ' ')), assert(s(S, x)),
      vyhra(x, VS),
      write(VS), write([S, 'nahod tah']), nl, vypis_pole.


% ZPETNE PREHRANI HRY
% ===================
% Zde se po kazdem vypsanem tahu ceka na vstup z klavesnice, kde staci
% zadat treba prikaz |:a. Nezalezi co, ale aby se neco nacetlo. Nemuze
% to byt jen tecka.

hra :- findall([S,Hrac,Pole],tah(Hrac,S,Pole),Tahy),   %zjisti vsechny provedene tahy
       vypis_hry(Tahy).

vypis_hry([]).
vypis_hry([[S,Hrac,Pole]|Tahy]) :- vypis_tahu([S,Hrac],Pole),   % vypis provedeny tah a pole
                                   nl,write([S,Hrac]),          % vypis info kam a kdo tahl
                                   nl, read(_),                 % cekej na vstup z klavesnice
                                   vypis_hry(Tahy).


vypis_tahu([S,Hrac],Pole)
            :- zjisti_sour(x,SX),               %zjisti vsechny sour X
               zjisti_sour(y,SY),               %zjisti vsechny sour Y
               length(SX,PocX),                 %zjisti pocet souradnic X
               nl, write('  '), vypis_f(PocX),  %vypsani horniho ohraniceni tabulky

               reverse(SY,RSY),                 %otocim poradi souradnic Y - bude se
                                                %vypisovat od nejvyssi souradnice Y

               vypis_tahu([S,Hrac],SX,RSY,Pole),%samotny vypis pole a prirazeneho tahu

               nl, write('   '),
               vypis_l(SX).                     %vypis hodnot osy X




%Samotny vypis pole
%------------------
% vypis_tahy([HranaSour,Hrac],[SouradniceX],[OtoceneSouradniceY],CelePole).

vypis_tahu(_,_,[],_).
vypis_tahu([S,Hrac],SX,[Y|SY],P) :-
                         nl, write(Y), write(' |'),   %vypis cislo radku (hodnota souradnice Y)

                         vypis_radek([S,Hrac],SX,Y,P),%vypis radek souranice Y

                         nl, write('  '),             %podtrhni radek
                         length(SX,XL),               %    "
                         vypis_f(XL),                 %    "

                         vypis_tahu([S,Hrac],SX,SY,P).

%vypis_radek([HranaSour,Hrac],[SouradniceX],RadekY,CelePole).
%-------------

vypis_radek(_,[],_,_).

% pokud se jedna o souradnici shodnou s provedenym tahem
vypis_radek([[X,Y],Hrac],[X|SX],Y,P) :-
                                    write(' '), write(Hrac), write(' |'),
                                    vypis_radek([[X,Y],Hrac],SX,Y,P).

%pokud se NEjedna o souradnici shodnou s provedenym tahem
vypis_radek([S,Hrac],[X|SX],Y,P) :- S \= [X,Y],
                                    member([[X,Y],H],P),         % zjisti, co je na souradnici [X,Y]
                                    write(' '), write(H), write(' |'),
                                    vypis_radek([S,Hrac],SX,Y,P).

% ULOZENI STRATEGIE
% =================

%vyhral pocitac
%--------------
%prebira se strategie pocitace

uloz_hru(x) :- findall([S,Pole],tah(x,S,Pole),Tahy),
               zaeviduj(x,Tahy).

%vyhral hrac
%--------------
%prebira se strategie hrace

uloz_hru(o) :- findall([S,Pole],tah(o,S,Pole),Tahy),        %pokud vyhraje hrac
               preved_tahy(Tahy,TahyNew),                   %prehod hry
               zaeviduj(x,TahyNew).



zaeviduj(_,[]).
zaeviduj(Hrac,[[S,Pole]|Tahy]) :- not(hra(Hrac,S,Pole)),   %jeste neni ulozeno z predchozich her
                                  assert(hra(Hrac,S,Pole)), %uloz
                                  zaeviduj(Hrac,Tahy).

zaeviduj(Hrac,[[S,Pole]|Tahy]) :- hra(Hrac,S,Pole),         %jiz ulozeno z predchozich her
                                  zaeviduj(Hrac,Tahy).




%Prevedeni tahu
%--------------
%Tam, kde tahl x bude tahnout o a naopak. Pak se ulozi x.
preved_tahy([],[]).
preved_tahy([[S,Pole]|Tahy],[[S,PoleNew]|TahyNew]) :-

                         projdi_pole(Pole,PoleNew),        %ukazdeho tahu (pole) proved zmenu
                         preved_tahy(Tahy,TahyNew).

projdi_pole([],[]).
projdi_pole([[S,' ']|Pole],[[S,' ']|PoleNew]) :- projdi_pole(Pole,PoleNew).
projdi_pole([[S,x]|Pole],[[S,o]|PoleNew]) :- projdi_pole(Pole,PoleNew).
projdi_pole([[S,o]|Pole],[[S,x]|PoleNew]) :- projdi_pole(Pole,PoleNew).
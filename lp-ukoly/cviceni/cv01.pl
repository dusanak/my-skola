% swipl -s cv01.pl.

% muz(Kdo).

muz(petr).
muz(tomas).
muz(marek).
muz(ondra).
muz(david).

% zena(Kdo).

zena(jana).
zena(iveta).
zena(andrea).
zena(tereza).
zena(eva).

% rodic(Kdo, Koho).

rodic(petr, marek).
rodic(petr, andrea).
rodic(jana, marek).
rodic(jana, andrea).

rodic(tomas, ondra).
rodic(tomas, tereza).
rodic(iveta, ondra).
rodic(iveta, tereza).

rodic(ondra, david).
rodic(ondra, eva).
rodic(andrea, david).
rodic(andrea, eva).

otec(X, Y) :- muz(X), rodic(X, Y).
matka(X, Y) :- zena(X), rodic(X, Y).

bratr(X, Y) :- muz(X), rodic(Z, X), rodic(Z, Y), X \= Y.
sestra(X, Y) :- zena(X), rodic(Z, X), rodic(Z, Y), X \= Y.

stryc(X, Y) :- bratr(X, Z), rodic(Z, Y).

deda(X, Y) :- otec(X, Z), rodic(Z, Y).
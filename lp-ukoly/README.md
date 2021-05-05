# LP
## Prolog
To load a script in Prolog into memory:
    swipl -s <FILENAME>.pl

To generate a new map:
    generuj_pole([5,6,7,8,9,0,1,2,3,4],[5,6,7,8,9,0,1,2,3,4]).
    generuj_pole([5,4,6,3,7,2,8,1,9,0],[5,4,6,3,7,2,8,1,9,0]).

To do a random move:
    tp.

To react to an enemy move [X, Y]:
    tah([X, Y]), nl, tp.

To save game:
    tell('./hra.txt'),listing(tah(_,_,_)),told.
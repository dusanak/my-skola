d(1, a, 1).
d(1, a, 2).
d(1, b, 1).
d(2, b, 3).

i(1).
f(3).

startQ(I):- findall(X, i(X), I).

add_unique(Item, List, Result) :- not(member(Item, List)), append(Item, List, Result).
add_unique(Item, List, Result) :- member(Item, List), Result = List.

prechod([], _, []).
prechod([Stav|Stavy], Znak, Q) :- findall(X, d(Stav, Znak, X), Y), sort(Y, Sorted), 
                                  prechod(Stavy, Znak, Q1), add_unique(Sorted, Q1, Q).

prechody(_, [], []).
prechody(Stav, [Znak|Abeceda], SeznamQ) :- prechod(Stav, Znak, Q), prechody(Stav, Abeceda, SeznamQ1),
                                           SeznamQ = [[Stav, Znak, Q]|SeznamQ1].

getQs([], []) :- !.
getQs([[_, _, NewQ]|Ds], [NewQ|Qs]) :- getQs(Ds, Qs).

vypis_delta([]):- !.
vypis_delta([[Stav, Znak, NovyStav]|Ds]) :- write('d( '),
                                            write(Stav),
                                            write(', '),
                                            write(Znak),
                                            write(', '),
                                            write(NovyStav),
                                            write(')'),nl,
                                            vypis_delta(Ds).

%DAL ZKOPIROVANE

getF([],[]).                                            % pokud sjme zpracovali vsechny stavy, jsme na konci listu F
getF([Q|Qs],[Q|Fs]):- findall(X,f(X),F), % získá množinu koncových stavu NKF
                 match(Q,F),                        % zjsití, zda stav DKA obsahuje alespon jeden z koncových stavu NKF
                 getF(Qs,Fs).                       % jestli ano, prida tento stav do F (v hlave predikatu)

getF([Q|Qs],Fs):- findall(X,f(X),F),        % získá množinu koncových stavu NKF
                  \+ match(Q,F),                 % zjsití, zda stav DKA obsahuje alespon jeden z koncových stavu NKF
                  getF(Qs,Fs).              % jestli ano, preskoci tento stav do F (v hlave predikatu)

match(Qs,Fs):- member(Q,Qs),member(Q,Fs). %porovná, zda dva listy mají alespon jeden spolecný prvek

% convert prevadi NKA na DKA
% convert má 5 argumentu:
% - Qs (výstupní): list stavu DKA, napr. [[1], [1, 2], [1, 3]]
% - Abeceda (vstupní): list znaku, napr. [a,b]
% - Delta(výstupní): list prechodu ze stavu, pres znak do
%   dalsiho stavu napr. [ [[1], a, [1, 2] ]
% - I pocatecni stav automatu napr. [1]
% - F mnozina koncovych stavu napr. [[1, 3]]
%
% Nejprve ziska pocatecni stav, získá mnozinu stavu Qs a prechodové
% funkce delta, z mnoziny stavu ziska koncove stavy, vypise Prechodove
% vyspise prechody mezi stavy.
%
%
% ?- convert(Qs,[a,b],Prechody,I,F).

convert(Stavy, Abeceda, Delta, I, F):- startQ(I), convert_(Stavy,[I], [], Abeceda, [],Delta), getF(Stavy,F), vypis_delta(Delta),!.

% convert_ je pomocný predikát s initem akumulatoru
% Stavy mnozina stavu DKA
% [I] akumulator mnoziny stavu DKA s pocatecnim stavem
% [] akumulator proslych stavu
% Abeceda (vstupní)
% []akumulator prechodu
% Delta mnozina prechodu
convert_(ProsleStavy,[],ProsleStavy,_,Delta,Delta):-!. % pokud dojdeme na konec akumulatoru stavu, v proslych stavech jsou vsechny stavy DKA, v akum. prechodu jsou vsechny prechody
convert_(Stavy,[Q|Qs],ProsleStavy,Abeceda,Prechody,Delta):- \+ member(Q, ProsleStavy), % zkonstrolujeme, zda stav Q na vstupu jiz nebyl zpracovan
                                                znaky(Q, Abeceda, D), % získame ze stavu Q prechody pres vsechny znaky abecedy
                                                getQs(D,Q1s), % z novych prechodu ziskame stavy
                                                append(Qs,Q1s,UpdatedStavy), % ziskane stavy vlozime do akumulatoru stavu
                                                append(Prechody, D, UpdatedPrechody), % nove prechody vlozime do akumulatoru
                                                append(ProsleStavy, [Q], UpdatedProsleStavy),% stav Q vlozime do zpracovanych stavu
                                                convert_(Stavy, UpdatedStavy,UpdatedProsleStavy, Abeceda, UpdatedPrechody,Delta).

convert_(Stavy,[Q|Qs],ProsleStavy,Abeceda,Prechody,Delta):- member(Q, ProsleStavy), % pokud stav Q byl jiz zpracovan, preskocime ho a pokracujeme dal v akumulatoru stavu
                                                convert_(Stavy,Qs,ProsleStavy, Abeceda, Prechody,Delta).
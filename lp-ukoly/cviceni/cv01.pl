% pythagoras
% pythagoras(3, 4, prepona)

pythagoras(A, B, C) :- pow(A, 2, A2),
                       pow(B, 2, B2),
                       C2 is A2 + B2,
                       sqrt(C2, C).

% Posta
% balik(vyska, sirka, hloubka, cena)
%
% cena = zaklad + prirazka

cena(maly, 50).
cena(stredni, 100).
cena(velky, 150).

prirazka(maly, 20).
prirazka(stredni, 50).
prirazka(velky, 70).

velikost(V, S, H, maly) :- V =< 50, S =< 50, H =< 50.
velikost(V, S, H, stredni) :- (V > 50, V =< 100); (S > 50, S =< 100); (H > 50, H =< 100).
velikost(V, S, H, velky) :- V > 100; S > 100; H > 100.

balik(V, S, H, CenaBaliku) :- velikost(V, S, H, Typ),
                              cena(Typ, Zaklad),
                              prirazka(Typ, Prirazka),
                              CenaBaliku is Zaklad + Prirazka.

% Scheduler

time_(morning).
time_(afternoon).

expert(ai, tonda).
expert(ai, barbara).
expert(ai, adam).

expert(bioinformatics, barbara).
expert(bioinformatics, petr).
expert(bioinformatics, lojza).

expert(databases, denny).
expert(databases, adam).

session(Time, Topic, P1, P2) :- time_(Time),
                                expert(Topic, P1),
                                expert(Topic, P2),
                                P1 \= P2.

no_conflict(Time1, _, _, Time2, _, _) :- Time1 \= Time2.
no_conflict(Time, P1, P2, Time, Q1, Q2) :- P1 \= Q1, P1 \= Q2,
                                           P2 \= Q1, P2 \= Q2.

scheduler(TimeA, A1, A2, TimeB, B1, B2, TimeC, C1, C2) :- 
    session(TimeA, ai, A1, A2),
    session(TimeB, bioinformatics, B1, B2),
    session(TimeC, databases, C1, C2),
    no_conflict(TimeA, A1, A2, TimeB, B1, B2),
    no_conflict(TimeA, A1, A2, TimeC, C1, C2),
    no_conflict(TimeB, B1, B2, TimeC, C1, C2).
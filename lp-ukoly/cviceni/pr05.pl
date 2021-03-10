isSubstring(_, []).
isSubstring([H1|T1], [H1|T2]) :- isSubstring(T1, T2).
isSubstring([H1|T1], [H2|T2]) :- H1 \= H2, isSubstring(T1, [H2|T2]).
isSubstring(String, Substring) :- string(String), string(Substring), atom_chars(String, X), atom_chars(Substring, Y), isSubstring(X, Y).
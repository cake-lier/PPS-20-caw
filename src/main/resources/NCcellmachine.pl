last_index_right(_, enemy, X, _, X) :- !.
last_index_right(B, T, X, Y, EX) :- T \= wall,
                                    T \= block_ver,
                                    T \= mover_left,
                                    X1 is X + 1,
                                    (member(cell(_, T1, X1, Y), B) -> last_index_right(B, T1, X1, Y, EX); EX = X).
move_right([], _, _, _, _, []).
move_right([cell(_, enemy, EX, Y) | Cs], B, SX, EX, Y, NB) :- move_right(Cs, B, SX, EX, Y, NB), !.
move_right([cell(I, T, X, Y) | Cs], B, SX, EX, Y1, [cell(I, T, X, Y) | NCs]) :- ((T = generator_right, X =:= SX, Y =:= Y1);
                                                                                 X < SX;
                                                                                 X > EX;
                                                                                 Y =\= Y1),
                                                                                move_right(Cs, B, SX, EX, Y1, NCs),
                                                                                !.
move_right([cell(_, _, X, Y) | Cs], B, SX, EX, Y, NB) :- X >= SX,
                                                         X < EX,
                                                         X1 is X + 1,
                                                         member(cell(_, enemy, X1, Y), B),
                                                         move_right(Cs, B, SX, EX, Y, NB),
                                                         !.
move_right([cell(I, T, X, Y) | Cs], B, SX, EX, Y, [cell(I, T, X1, Y)| NCs]) :- X >= SX,
                                                                               X =< EX,
                                                                               X1 is X + 1,
                                                                               move_right(Cs, B, SX, EX, Y, NCs).

mover_right_next_state(B, X, Y, NB) :- member(cell(_, mover_right, X, Y), B),
                                       last_index_right(B, mover_right, X, Y, EX),
                                       move_right(B, B, X, EX, Y, NB),
                                       !.
mover_right_next_state(B, _, _, B).

last_index_left(_, enemy, X, _, X) :- !.
last_index_left(B, T, X, Y, EX) :- T \= wall,
                                   T \= block_ver,
                                   T \= mover_right,
                                   X1 is X - 1,
                                   (member(cell(_, T1, X1, Y), B) -> last_index_left(B, T1, X1, Y, EX); EX = X).

move_left([], _, _, _, _, []).
move_left([cell(_, enemy, EX, Y) | Cs], B, SX, EX, Y, NB) :- move_left(Cs, B, SX, EX, Y, NB), !.
move_left([cell(I, T, X, Y) | Cs], B, SX, EX, Y1, [cell(I, T, X, Y) | NCs]) :- ((T = generator_left, X =:= EX, Y =:= Y1);
                                                                                X > SX;
                                                                                X < EX;
                                                                                Y =\= Y1),
                                                                               move_left(Cs, B, SX, EX, Y1, NCs),
                                                                               !.
move_left([cell(_, _, X, Y) | Cs], B, SX, EX, Y, NB) :- X =< SX,
                                                        X > EX,
                                                        X1 is X - 1,
                                                        member(cell(_, enemy, X1, Y), B),
                                                        move_left(Cs, B, SX, EX, Y, NB),
                                                        !.
move_left([cell(I, T, X, Y) | Cs], B, SX, EX, Y, [cell(I, T, X1, Y)| NCs]) :- X =< SX,
                                                                              X >= EX,
                                                                              X1 is X - 1,
                                                                              move_left(Cs, B, SX, EX, Y, NCs).

mover_left_next_state(B, X, Y, NB) :- member(cell(_, mover_left, X, Y), B),
                                      last_index_left(B, mover_left, X, Y, EX),
                                      move_left(B, B, X, EX, Y, NB),
                                      !.
mover_left_next_state(B, _, _, B).

last_index_down(_, enemy, _, Y, Y) :- !.
last_index_down(B, T, X, Y, EY) :- T \= wall,
                                  T \= block_hor,
                                  T \= mover_top,
                                  Y1 is Y + 1,
                                  (member(cell(_, T1, X, Y1), B) -> last_index_down(B, T1, X, Y1, EY); EY = Y).

move_down([], _, _, _, _, []).

move_down([cell(_, enemy, EY, X) | Cs], B, SY, EY, X, NB) :- move_down(Cs, B, SY, EY, X, NB), !.
move_down([cell(I, T, X, Y) | Cs], B, SY, EY, X1, [cell(I, T, X, Y) | NCs]) :- ((T = generator_down, Y =:= EY, X =:= X1);
                                                                               Y < SY;
                                                                               Y > EY;
                                                                               X =\= X1),
                                                                              move_down(Cs, B, SY, EY, X1, NCs),
                                                                              !.
move_down([cell(_, _, X, Y) | Cs], B, SY, EY, X, NB) :- Y >= SY,
                                                       Y < EY,
                                                       Y1 is Y + 1,
                                                       member(cell(_, enemy, X, Y1), B),
                                                       move_down(Cs, B, SY, EY, X, NB),
                                                       !.
move_down([cell(I, T, X, Y) | Cs], B, SY, EY, X, [cell(I, T, X, Y1)| NCs]) :- Y >= SY,
                                                                             Y =< EY,
                                                                             Y1 is Y + 1,
                                                                             move_down(Cs, B, SY, EY, X, NCs).

mover_down_next_state(B, X, Y, NB) :- member(cell(_,mover_down, X, Y), B),
                                     last_index_down(B, mover_down, X, Y, EY),
                                     move_down(B, B, Y, EY, X, NB),
                                     !.
mover_down_next_state(B, _, _, B).

last_index_top(_, enemy, _, Y, Y) :- !.
last_index_top(B, T, X, Y, EY) :-  T \= wall,
                                    T \= block_hor,
                                    T \= mover_down,
                                    Y1 is Y - 1,
                                    (member(cell(_, T1, X, Y1), B) -> last_index_top(B, T1, X, Y1, EY); EY = Y).

move_top([], _, _, _, _, []).
move_top([cell(_, enemy, EY, X) | Cs], B, SY, EY, X, NB) :- move_top(Cs, B, SY, EY, X, NB), !.
move_top([cell(I, T, X, Y) | Cs], B, SY, EY, X1, [cell(I, T, X, Y) | NCs]) :- ((T = generator_top, Y =:= EY, X =:= X1);
                                                                                Y > SY;
                                                                                Y < EY;
                                                                                X =\= X1),
                                                                               move_top(Cs, B, SY, EY, X1, NCs),
                                                                               !.
move_top([cell(_, _, X, Y) | Cs], B, SY, EY, X, NB) :- Y =< SY,
                                                        Y > EY,
                                                        Y1 is Y - 1,
                                                        member(cell(enemy, X, Y1), B),
                                                        move_top(Cs, B, SY, EY, X, NB),
                                                        !.
move_top([cell(I, T, X, Y) | Cs], B, SY, EY, X, [cell(I, T, X, Y1)| NCs]) :- Y =< SY,
                                                                              Y >= EY,
                                                                              Y1 is Y - 1,
                                                                              move_top(Cs, B, SY, EY, X, NCs).

mover_top_next_state(B, X, Y, NB) :- member(cell(_, mover_top, X, Y), B),
                                      last_index_top(B, mover_top, X, Y, EY),
                                      move_top(B, B, Y, EY, X, NB),
                                      !.
mover_top_next_state(B, _, _, B).

rotate_right([], _, _, []).
rotate_right([cell(I, T, X1, Y1) | Cs], X, Y, [cell(I, T1, X1, Y1) | NCs]) :- ((X1 is X + 1, Y1 =:= Y);
                                                                               (X1 =:= X, Y1 is Y + 1);
                                                                               (X1 is X - 1, Y1 =:= Y);
                                                                               (X1 =:= X, Y1 is Y - 1)),
                                                                              ((T = mover_right, T1 = mover_down);
                                                                               (T = mover_down, T1 = mover_left);
                                                                               (T = mover_left, T1 = mover_top);
                                                                               (T = mover_top, T1 = mover_right);
                                                                               (T = generator_right, T1 = generator_down);
                                                                               (T = generator_down, T1 = generator_left);
                                                                               (T = generator_left, T1 = generator_top);
                                                                               (T = generator_top, T1 = generator_right);
                                                                               (T = block_hor, T1 = block_ver);
                                                                               (T = block_ver, T1 = block_hor)),
                                                                              rotate_right(Cs, X, Y, NCs),
                                                                              !.
rotate_right([cell(I, T, X1, Y1) | Cs], X, Y, [cell(I, T, X1, Y1) | NCs]) :- rotate_right(Cs, X, Y, NCs).

rotator_right_next_state(B, X, Y, NB) :- member(cell(_, rotator_clockwise, X, Y), B), !, rotate_right(B, X, Y, NB).

rotate_left([], _, _, []).
rotate_left([cell(I, T, X1, Y1) | Cs], X, Y, [cell(I, T1, X1, Y1) | NCs]) :- ((X1 is X + 1, Y1 =:= Y);
                                                                              (X1 =:= X, Y1 is Y + 1);
                                                                              (X1 is X - 1, Y1 =:= Y);
                                                                              (X1 =:= X, Y1 is Y - 1)),
                                                                             ((T = mover_right, T1 = mover_top);
                                                                              (T = mover_down, T1 = mover_right);
                                                                              (T = mover_left, T1 = mover_down);
                                                                              (T = mover_top, T1 = mover_left);
                                                                              (T = generator_right, T1 = generator_top);
                                                                              (T = generator_down, T1 = generator_right);
                                                                              (T = generator_left, T1 = generator_down);
                                                                              (T = generator_top, T1 = generator_left);
                                                                              (T = block_hor, T1 = block_ver);
                                                                              (T = block_ver, T1 = block_hor)),
                                                                             rotate_left(Cs, X, Y, NCs),
                                                                             !.
rotate_left([cell(I, T, X1, Y1) | Cs], X, Y, [cell(I, T, X1, Y1) | NCs]) :- rotate_left(Cs, X, Y, NCs).

rotator_left_next_state(B, X, Y, NB) :- member(cell(_, rotator_counterclockwise, X, Y), B), !, rotate_left(B, X, Y, NB).

generate_right(B, _, X, EX, Y, _, NB) :- X1 is X + 1, member(cell(_, enemy, X1, Y), B), move_right(B, B, X, EX, Y, NB), !.
generate_right(B, T, X, EX, Y, M, [cell(M, T, X1, Y) | NB]) :- X1 is X + 1, move_right(B, B, X, EX, Y, NB).

generator_right_next_state(B, X, Y, M, NB) :- member(cell(_, generator_right, X, Y), B),
                                              X1 is X - 1,
                                              member(cell(_, T, X1, Y), B),
                                              last_index_right(B, generator_right, X, Y, EX),
                                              generate_right(B, T, X, EX, Y, M, NB),
                                              !.
generator_right_next_state(B, _, _, _, B).

generate_left(B, _, X, EX, Y, _, NB) :- X1 is X - 1, member(cell(enemy, X1, Y), B), move_left(B, B, X, EX, Y, NB), !.
generate_left(B, T, X, EX, Y, M, [cell(M, T, X1, Y) | NB]) :- X1 is X - 1, move_left(B, B, X, EX, Y, NB).

generator_left_next_state(B, X, Y, M, NB) :- member(cell(_, generator_left, X, Y), B),
                                             X1 is X + 1,
                                             member(cell(_, T, X1, Y), B),
                                             last_index_left(B, generator_left, X, Y, EX),
                                             generate_left(B, T, X, EX, Y, M, NB),
                                             !.
generator_left_next_state(B, _, _, _, B).

generate_down(B, _, Y, EY, X, _, NB) :- Y1 is Y + 1, member(cell(enemy, X, Y1), B), move_down(B, B, Y, EY, X, NB), !.
generate_down(B, T, Y, EY, X, M, [cell(M, T, X, Y1) | NB]) :-  Y1 is Y + 1, move_down(B, B, Y, EY, X, NB).

generator_down_next_state(B, X, Y, M, NB) :- member(cell(_, generator_down, X, Y), B),
                                            Y1 is Y - 1,
                                            member(cell(_, T, X, Y1), B),
                                            last_index_down(B, generator_down, X, Y, EY),
                                            generate_down(B, T, Y, EY, X, M, NB),
                                            !.
generator_down_next_state(B, _, _, _, B).

generate_top(B, _, Y, EY, X, _, NB) :- Y1 is Y - 1, member(cell(enemy, X, Y1), B), move_top(B, B, Y, EY, X, NB), !.
generate_top(B, T, Y, EY, X, M, [cell(M, T, X, Y1) | NB]) :-  Y1 is Y - 1, move_top(B, B, Y, EY, X, NB).

generator_top_next_state(B, X, Y, M, NB) :- member(cell(_, generator_top, X, Y), B),
                                             Y1 is Y + 1,
                                             member(cell(_, T, X, Y1), B),
                                             last_index_top(B, generator_top, X, Y, EY),
                                             generate_top(B, T, Y, EY, X, M, NB),
                                             !.
generator_top_next_state(B, _, _, _, B).

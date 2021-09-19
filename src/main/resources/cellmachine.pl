% last_index_right(Board, Type, XCoordinate, YCoordinate, EndXCoordinate)
% 
% This predicate checks whether or not a board contains a row of cells that can be moved to the right by one position starting
% from the cell whose coordinates and type are given. The check starts from the initial cell and then recursively moves along the
% board from left to right on the same row of the previous cell. This means that getting the next cell is equivalent to getting
% the one adjacent to the right of the previous one. Any given cell can move to the right if it is one that can always move to the
% right, like an "enemy" cell, or if it is a cell that can move to the right if it is adjacent to a cell that can move to the
% right, which is any cell except the the "wall" cell and the "vertical block" cell (which can never be moved in the right
% direction) and the "generator left" and the "arrow left" cell (because they move in a direction opposite to the right one). If
% no other cell is found while traversing the board as previously described, it is assumed that an empty cell is met, which can
% always be moved to the right.
last_index_right(_, enemy, X, _, X) :- !.
last_index_right(B, T, X, Y, EX) :- T \= wall,
                                    T \= block_ver,
                                    T \= arrow_left,
                                    X1 is X + 1,
                                    (member(cell(T1, X1, Y), B) -> last_index_right(B, T1, X1, Y, EX); EX = X).

% move_right(Board, Board, StartXCoordinate, EndXCoordinate, YCoordinate, NextBoard)
% 
% This predicate applies the rule for the "arrow right" cell behavior. This predicate considers only the cells which are adjacent
% to the right of the "arrow right" cell which coordinates are given. If, while moving left to right from the "arrow right" cell,
% an "enemy" cell is found, then the remaining cells are ignored by this predicate. This row of cells is then moved one position
% to the right. If the last cell of the row is an enemy cell, the cell is destroyed as is destroyed the one previous to the enemy
% cell. Those two cells will not be present in the next board state. No ordering is required for the cells in the board and the
% empty cells must not be present.
move_right([], _, _, _, _, []).
move_right([cell(enemy, EX, Y) | CS], B, SX, EX, Y, NB) :- move_right(CS, B, SX, EX, Y, NB), !.
move_right([cell(T, X, Y) | CS], B, SX, EX, Y1, [cell(T, X, Y) | NCS]) :- ((T = generator_right, X =:= SX, Y =:= Y1);
                                                                           X < SX;
                                                                           X > EX;
                                                                           Y =\= Y1),
                                                                          move_right(CS, B, SX, EX, Y1, NCS),
                                                                          !.
move_right([cell(_, X, Y) | CS], B, SX, EX, Y, NB) :- X >= SX, 
                                                      X < EX,
                                                      X1 is X + 1,
                                                      member(cell(enemy, X1, Y), B),
                                                      move_right(CS, B, SX, EX, Y, NB),
                                                      !.
move_right([cell(T, X, Y) | CS], B, SX, EX, Y, [cell(T, X1, Y)| NCS]) :- X >= SX, 
                                                                         X =< EX,
                                                                         X1 is X + 1,
                                                                         move_right(CS, B, SX, EX, Y, NCS).

% arrow_right_next_state(Board, XCoordinate, YCoordinate, NextBoard)
% 
% This predicate allows to update the state of a board and obtain its next state applying the rule for the "arrow right" cell
% behavior. This predicate considers only the cells which are adjacent to the right of the "arrow right" cell whose coordinates
% are given. If, while moving left to right from the "arrow right" cell, an "enemy" cell is found, then the remaining cells are
% ignored by this predicate. If the row of cells built as previously described is deemed to be movable to the right by one
% position, the row is then moved. If the last cell of the row is an enemy cell, the cell is destroyed as is destroyed the one
% previous to the enemy cell. Those two cells will not be present in the next board state. If the coordinates do not point to an
% "arrow right" cell, the predicate simply evaluates to "no". No ordering is required for the cells in the board and the empty
% cells must not be present.
arrow_right_next_state(B, X, Y, NB) :- member(cell(arrow_right, X, Y), B),
                                       last_index_right(B, arrow_right, X, Y, EX),
                                       move_right(B, B, X, EX, Y, NB),
                                       !.
arrow_right_next_state(B, _, _, B).

%-----------------------------------------------------------------------------------------------------------------------------------

% last_index_left(Board, Type, XCoordinate, YCoordinate, EndXCoordinate)
last_index_left(_, enemy, X, _, X) :- !.
last_index_left(B, T, X, Y, EX) :- T \= wall,
                                    T \= block_ver,
                                    T \= arrow_right,
                                    X1 is X - 1,
                                    (member(cell(T1, X1, Y), B) -> last_index_left(B, T1, X1, Y, EX); EX = X).

% move_left([cell | tail], Board, StartXCoordinate, EndXCoordinate, YCoordinate, NextBoard)
move_left([], _, _, _, _, []).
move_left([cell(enemy, EX, Y) | CS], B, SX, EX, Y, NB) :- move_left(CS, B, SX, EX, Y, NB), !.
move_left([cell(T, X, Y) | CS], B, SX, EX, Y1, [cell(T, X, Y) | NCS]) :- ((T = generator_left, X =:= EX, Y =:= Y1);
                                                                           X > SX;
                                                                           X < EX;
                                                                           Y =\= Y1),
                                                                          move_left(CS, B, SX, EX, Y1, NCS),
                                                                          !.
move_left([cell(_, X, Y) | CS], B, SX, EX, Y, NB) :- X =< SX, 
                                                     X > EX,
                                                     X1 is X - 1,
                                                     member(cell(enemy, X1, Y), B),
                                                     move_left(CS, B, SX, EX, Y, NB),
                                                     !.
move_left([cell(T, X, Y) | CS], B, SX, EX, Y, [cell(T, X1, Y)| NCS]) :- X =< SX, 
                                                                        X >= EX,
                                                                        X1 is X - 1,
                                                                        move_left(CS, B, SX, EX, Y, NCS).

% arrow_left_next_state(Board, XCoordinate, YCoordinate, NextBoard)
arrow_left_next_state(B, X, Y, NB) :- member(cell(arrow_left, X, Y), B),
                                      last_index_left(B, arrow_left, X, Y, EX),
                                      move_left(B, B, X, EX, Y, NB),
                                      !.
arrow_left_next_state(B, _, _, B).

%-----------------------------------------------------------------------------------------------------------------------------------

% rotate_right(Board, XCoordinate, YCoordinate, NextBoard)
% 
% This predicate applies the rule for the "rotate right" cell behavior. The rule rotate in a clockwise direction the cells which
% are orthogonally adjacent to the "rotate cell" which coordinates are given. If they can not be rotated or are not orthogonally
% adjacent, the cells are left as they were.
rotate_right([], _, _, []).
rotate_right([cell(T, X1, Y1) | CS], X, Y, [cell(T1, X1, Y1) | NCS]) :- ((X1 is X + 1, Y1 =:= Y);
                                                                         (X1 =:= X, Y1 is Y + 1);
                                                                         (X1 is X - 1, Y1 =:= Y);
                                                                         (X1 =:= X, Y1 is Y - 1)),
                                                                        ((T = arrow_right, T1 = arrow_down);
                                                                         (T = arrow_down, T1 = arrow_left);
                                                                         (T = arrow_left, T1 = arrow_top);
                                                                         (T = arrow_top, T1 = arrow_right);
                                                                         (T = generator_right, T1 = generator_down);
                                                                         (T = generator_down, T1 = generator_left);
                                                                         (T = generator_left, T1 = generator_top);
                                                                         (T = generator_top, T1 = generator_right);
                                                                         (T = block_hor, T1 = block_ver);
                                                                         (T = block_ver, T1 = block_hor)),
                                                                        rotate_right(CS, X, Y, NCS),
                                                                        !.
rotate_right([cell(T, X1, Y1) | CS], X, Y, [cell(T, X1, Y1) | NCS]) :- rotate_right(CS, X, Y, NCS).

% rotate_right_next_state(Board, XCoordinate, YCoordinate, NextBoard)
% 
% This predicate allows to update the state of a board and obtain its next state applying the rule for the "rotate right" cell
% behavior. The rule rotate in a clockwise direction the cells which are orthogonally adjacent to the "rotate cell" which
% coordinates are given. If they can not be rotated or are not orthogonally adjacent, the cells are left as they were. If the
% coordinates does not point to a "rotate right" cell, the predicate simply evaluates to "no". No ordering is required for the
% cells in the board and the empty cells must not be present.
rotate_right_next_state(B, X, Y, NB) :- member(cell(rotate_right, X, Y), B), !, rotate_right(B, X, Y, NB).

%-----------------------------------------------------------------------------------------------------------------------------------

generate_right(B, _, X, EX, Y, NB) :- X1 is X + 1, member(cell(enemy, X1, Y), B), move_right(B, B, X, EX, Y, NB), !.
generate_right(B, T, X, EX, Y, [cell(T, X1, Y) | NB]) :- X1 is X + 1, move_right(B, B, X, EX, Y, NB).

generator_right_next_state(B, X, Y, NB) :- member(cell(generator_right, X, Y), B),
                                           X1 is X - 1,
                                           member(cell(T, X1, Y), B),
                                           last_index_right(B, generator_right, X, Y, EX),
                                           generate_right(B, T, X, EX, Y, NB),
                                           !.
generator_right_next_state(B, _, _, B).

%-----------------------------------------------------------------------------------------------------------------------------------
% rotate_left(Board, XCoordinate, YCoordinate, NextBoard)
% 
% This predicate applies the rule for the "rotate left" cell behavior. The rule rotate in a counterclockwise direction the cells which
% are orthogonally adjacent to the "rotate cell" which coordinates are given. If they can not be rotated or are not orthogonally
% adjacent, the cells are left as they were.
rotate_left([], _, _, []).
rotate_left([cell(T, X1, Y1) | CS], X, Y, [cell(T1, X1, Y1) | NCS]) :- ((X1 is X + 1, Y1 =:= Y);
                                                                         (X1 =:= X, Y1 is Y + 1);
                                                                         (X1 is X - 1, Y1 =:= Y);
                                                                         (X1 =:= X, Y1 is Y - 1)),
                                                                        ((T = arrow_right, T1 = arrow_top);
                                                                         (T = arrow_down, T1 = arrow_right);
                                                                         (T = arrow_left, T1 = arrow_down);
                                                                         (T = arrow_top, T1 = arrow_left);
                                                                         (T = generator_right, T1 = generator_top);
                                                                         (T = generator_down, T1 = generator_right);
                                                                         (T = generator_left, T1 = generator_down);
                                                                         (T = generator_top, T1 = generator_left);
                                                                         (T = block_hor, T1 = block_ver);
                                                                         (T = block_ver, T1 = block_hor)),
                                                                        rotate_left(CS, X, Y, NCS),
                                                                        !.
rotate_left([cell(T, X1, Y1) | CS], X, Y, [cell(T, X1, Y1) | NCS]) :- rotate_left(CS, X, Y, NCS).

% rotate_left_next_state(Board, XCoordinate, YCoordinate, NextBoard)
% 
% This predicate allows to update the state of a board and obtain its next state applying the rule for the "rotate left" cell
% behavior. The rule rotate in a counterclockwise direction the cells which are orthogonally adjacent to the "rotate cell" which
% coordinates are given. If they can not be rotated or are not orthogonally adjacent, the cells are left as they were. If the
% coordinates does not point to a "rotate left" cell, the predicate simply evaluates to "no". No ordering is required for the
% cells in the board and the empty cells must not be present.
rotate_left_next_state(B, X, Y, NB) :- member(cell(rotate_left, X, Y), B), !, rotate_left(B, X, Y, NB).
%-----------------------------------------------------------------------------------------------------------------------------------
% last_index_right(Board, Type, XCoordinate, YCoordinate, EndYCoordinate)

last_index_top(_, enemy, _, Y, Y) :- !.
last_index_top(B, T, X, Y, EY) :-   T \= wall,
                                    T \= block_hor,
                                    T \= arrow_down,
                                    Y1 is Y + 1,
                                    (member(cell(T1, X, Y1), B) -> last_index_top(B, T1, X, Y1, EY); EY = Y).

last_index_down(_, enemy, _, Y, Y) :- !.
last_index_down(B, T, X, Y, EY) :-  T \= wall,
                                    T \= block_hor,
                                    T \= arrow_top,
                                    Y1 is Y - 1,
                                    (member(cell(T1, X, Y1), B) -> last_index_down(B, T1, X, Y1, EY); EY = Y).
%-----------------------------------------------------------------------------------------------------------------------------------
generate_left(B, _, X, EX, Y, NB) :- X1 is X - 1, member(cell(enemy, X1, Y), B), move_left(B, B, X, EX, Y, NB), !.
generate_left(B, T, X, EX, Y, [cell(T, X1, Y) | NB]) :- X1 is X - 1, move_left(B, B, X, EX, Y, NB).

generator_left_next_state(B, X, Y, NB) :- member(cell(generator_left, X, Y), B),
                                           X1 is X + 1,
                                           member(cell(T, X1, Y), B),
                                           last_index_left(B, generator_left, X, Y, EX),
                                           generate_left(B, T, X, EX, Y, NB),
                                           !.
generator_left_next_state(B, _, _, B).

%-----------------------------------------------------------------------------------------------------------------------------------
generate_top(B, _, Y, EY, X, NB) :- Y1 is Y + 1, member(cell(enemy, X, Y1), B), move_top(B, B, Y, EY, X, NB), !.
generate_top(B, T, Y, EY, X, [cell(T, X, Y1) | NB]) :-  Y1 is Y + 1, move_top(B, B, Y, EY, X, NB).

generator_top_next_state(B, X, Y, NB) :- member(cell(generator_top, X, Y), B),
                                           Y1 is Y - 1,
                                           member(cell(T, X, Y1), B),
                                           last_index_top(B, generator_top, X, Y, EY),
                                           generate_top(B, T, Y, EY, X, NB),
                                           !.
generator_top_next_state(B, _, _, B).
%-----------------------------------------------------------------------------------------------------------------------------------
generate_down(B, _, Y, EY, X, NB) :- Y1 is Y - 1, member(cell(enemy, X, Y1), B), move_down(B, B, Y, EY, X, NB), !.
generate_down(B, T, Y, EY, X, [cell(T, X, Y1) | NB]) :-  Y1 is Y - 1, move_down(B, B, Y, EY, X, NB).

generator_down_next_state(B, X, Y, NB) :- member(cell(generator_down, X, Y), B),
                                           Y1 is Y + 1,
                                           member(cell(T, X, Y1), B),
                                           last_index_down(B, generator_down, X, Y, EY),
                                           generate_down(B, T, Y, EY, X, NB),
                                           !.
generator_down_next_state(B, _, _, B).
%-----------------------------------------------------------------------------------------------------------------------------------
% move_top([cell | tail], Board, StartYCoordinate, EndYCoordinate, XCoordinate, NextBoard)
move_top([], _, _, _, _, []).

move_top([cell(enemy, EY, X) | CS], B, SY, EY, X, NB) :- move_top(CS, B, SY, EY, X, NB), !.
move_top([cell(T, X, Y) | CS], B, SY, EY, X1, [cell(T, X, Y) | NCS]) :- ((T = generator_top, Y =:= EY, X =:= X1);
                                                                           Y < SY;
                                                                           Y > EY;
                                                                           X =\= X1),
                                                                          move_top(CS, B, SY, EY, X1, NCS),
                                                                          !.
move_top([cell(_, X, Y) | CS], B, SY, EY, X, NB) :-  Y >= SY, 
                                                     Y < EY,
                                                     Y1 is Y + 1,
                                                     member(cell(enemy, X, Y1), B),
                                                     move_top(CS, B, SY, EY, X, NB),
                                                     !.
move_top([cell(T, X, Y) | CS], B, SY, EY, X, [cell(T, X, Y1)| NCS]) :-  Y >= SY, 
                                                                         Y =< EY,
                                                                         Y1 is Y + 1,
                                                                         move_top(CS, B, SY, EY, X, NCS).

arrow_top_next_state(B, X, Y, NB) :- member(cell(arrow_top, X, Y), B),
                                     last_index_top(B, arrow_top, X, Y, EY),
                                     move_top(B, B, Y, EY, X, NB),
                                     !.
arrow_top_next_state(B, _, _, B).
%-----------------------------------------------------------------------------------------------------------------------------------
% move_down([cell | tail], Board, StartYCoordinate, EndYCoordinate, XCoordinate, NextBoard)
move_down([], _, _, _, _, []).
move_down([cell(enemy, EY, X) | CS], B, SY, EY, X, NB) :- move_down(CS, B, SY, EY, X, NB), !.
move_down([cell(T, X, Y) | CS], B, SY, EY, X1, [cell(T, X, Y) | NCS]) :- ((T = generator_down, Y =:= EY, X =:= X1);
                                                                           Y > SY;
                                                                           Y < EY;
                                                                           X =\= X1),
                                                                          move_down(CS, B, SY, EY, X1, NCS),
                                                                          !.
move_down([cell(_, X, Y) | CS], B, SY, EY, X, NB) :- Y =< SY, 
                                                     Y > EY,
                                                     Y1 is Y - 1,
                                                     member(cell(enemy, X, Y1), B),
                                                     move_down(CS, B, SY, EY, X, NB),
                                                     !.
move_down([cell(T, X, Y) | CS], B, SY, EY, X, [cell(T, X, Y1)| NCS]) :-  Y =< SY, 
                                                                         Y >= EY,
                                                                         Y1 is Y - 1,
                                                                         move_down(CS, B, SY, EY, X, NCS).
arrow_down_next_state(B, X, Y, NB) :-  member(cell(arrow_down, X, Y), B),
                                       last_index_down(B, arrow_down, X, Y, EY),
                                       move_down(B, B, Y, EY, X, NB),
                                       !.
arrow_down_next_state(B, _, _, B).
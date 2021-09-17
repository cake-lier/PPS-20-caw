% is_movable_right(Machine, Type, XCoordinate, YCoordinate)
% 
% This predicate checks whether or not a given machine contains cells that can be moved as a whole to the right by one position. 
% The check starts from the given initial cell and then recursively moves along the machine from left to right on the same row of 
% the previous cell. This means that getting the next cell is equivalent to getting the one adjacent to the right of the previous.
% Any given cell can move to the right if it is one that can always move to the right (an empty cell or an enemy cell) or if it is
% a cell that can move to the right if it is adjacent to a cell that can move to the right, which is any cell except the the wall
% cell and the vertical block cell (which can never be moved in the right direction) and the generator left and the arrow left
% cell (because they move in a direction opposite to the right one).
is_movable_right(_, enemy, _, _).
is_movable_right(M, T, X, Y) :- T \= wall,
                                T \= block_ver,
                                T \= generator_left,
                                T \= arrow_left,
                                X1 is X + 1,
                                (member(cell(T1, X1, Y), M) -> is_movable_right(M, T1, X1, Y); true).

% move_right(Machine, XCoordinate, YCoordinate, NextMachine)
% 
% This predicate shifts all cells in the machine to the right by one position except the enemy cells and the empty cells, which
% are deleted.
move_right([], _, _, []).
move_right([cell(enemy, X, Y) | CS], X, Y, CS).
move_right([cell(T, X, Y) | CS], X1, Y1, [cell(T, X, Y) | NCS]) :- (X =\= X1; Y =\= Y1), move_right(CS, X1, Y1, NCS), !.
move_right([cell(_, X, Y) | CS], X, Y, NM) :- X1 is X + 1, member(cell(enemy, X1, Y), CS), move_right(CS, X1, Y, NM), !.
move_right([cell(T, X, Y) | CS], X, Y, [cell(T, X1, Y) | NCS]) :- X1 is X + 1, move_right(CS, X1, Y, NCS).

% arrow_right_next_state(Machine, XCoordinate, YCoordinate, NextMachine)
% 
% This predicate allows to update the state of a machine (a list of cells on a board) and obtain its next state when the first
% cell is a "arrow right" cell. Hence, this predicate is for applying the "arrow right" rule. The cells that makes its
% "surroundings" and needs to be passed with the first cell in the machine are all cells which are to the right of the first cell.
% These cells need to be inserted into the machine until an empty cell, an enemy cell or a wall cell is met while moving left to
% right from the first arrow cell. If the row of cells is deemed to be movable to the right, all cells are then moved and the last
% one deleted. This is because, in this case, the last cell can only be an enemy cell or an empty cell. The order of the cells in
% the list can not respect the order of the cells in the board.
arrow_right_next_state(M, X, Y, NM) :- member(cell(arrow_right, X, Y), M),
                                       is_movable_right(M, arrow_right, X, Y),
                                       move_right(M, X, Y, NM),
                                       !.
arrow_right_next_state(M, _, _, M).

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

% rotate_right_next_state(Machine, XCoordinate, YCoordinate, NextMachine)
% 
% This predicate allows to update the state of a machine (a list of cells on a board) and obtain its next state when the first
% cell is a "rotate right" cell. Hence, this predicate is for applying the "rotate right" rule. The cells that makes its
% "surroundings" and needs to be passed with the first cell in the machine are its orthogonally adjacents cells. Then each cell,
% except for the first, rotate cell, is then rotated clockwise if it can be rotated. If it can not, is then leaved as it is. No
% translation of coordinates is applied by this rule.
rotate_right_next_state(M, X, Y, NM) :- member(cell(rotate_right, X, Y), M), !, rotate_right(M, X, Y, NM).


%-----------------------------------------------------------------------------------------------------------------------------
% get_furthest_right(+Machine, +XCoordinate, +YCoordinate, +TemporaryFurthestRightCell, -FurthestRightCell)
% This predicate return the furthest right cell of the generator_right or of the already generated cells.
get_furthest_right(M, X, Y, TMP, NC) :- X1 is X + 1,
                                        member(cell(T, X1, Y), M),
                                        get_furthest_right(M, X1, Y, cell(T, X1, Y), NC), !.
get_furthest_right(M, X, Y, TMP, NC) :- NC = TMP.

% generate_right(+Machine, +Type, +FurthestRightCell, -NextMachine)
% This predicate adds a new cell to the machine only if the furthest right cell is the same type specified in input - that is the
% generator_right has already generated some cells and there is still a non-occupied cell in its right - or is a generator_right
% - that is the generator_right has not generated any cells yet. If the furthest right cell is a wall, a block_ver, an arrow_left
% the cell generation is blocked; if it's an enemy, this will be destroyed and removed from the machine.
generate_right([], _, _, []):- !.
generate_right([cell(enemy, X, Y) | CS], _, cell(enemy, X, Y), CS):- !.
generate_right([cell(T, X, Y) | CS], _, cell(T, X, Y), [cell(T, X, Y)|CS]) :- (T = wall; T = block_ver; T = arrow_left), !.
generate_right([cell(T, X, Y) | CS], T2, cell(T1, X1, Y1), [cell(T, X, Y) | NCS]) :-
                                                                              (X \= X1; Y \= Y1),
                                                                               generate_right(CS, T2, cell(T1, X1, Y1), NCS), !.
generate_right([cell(T, X, Y) | CS], T1, cell(T, X, Y), [cell(T, X, Y), cell(T1, X1, Y)| NCS]) :-
                                                                               (T = T1; T = generator_right),
                                                                                X1 is X + 1,
                                                                                generate_right(CS, T, cell(T, X, Y), NCS), !.

% generator_right_next_state(+Machine, XCoordinate, YCoordinate, NextMachine)
% This predicate applys the "right generation" rule: it takes the previous state of the machine and returns its new state. The
% rule is applied only if there is cell placed at the left of the generator_right and there is a non-occupied cell at the right
% of the generator_right or at the right of the already generated cells: if these conditions are true, the predicate will return a
% new machine containing a new generated cell, otherwise it will return the old machine. 
generator_right_next_state(M, X, Y, NM) :- member(cell(generator_right, X, Y), M),
                                           X1 is X - 1,
                                           member(cell(T, X1, Y), M),
                                           get_furthest_right(M, X, Y, cell(generator_right, X, Y), NC),
                                           generate_right(M, T, NC, NM),
                                           !.
generator_right_next_state(M, _, _, M).

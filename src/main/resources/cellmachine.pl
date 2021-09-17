% is_movable_right(Machine, Cell)
%
% This predicate checks whether or not a given machine contains cells that can be moved as a whole to the right by one position.
% The check starts from the given initial cell and then recursively moves along the machine from left to right on the same row of
% the previous cell. This means that getting the next cell is equivalent to getting the one adjacent to the right of the previous.
% Any given cell can move to the right if it is one that can always move to the right (an empty cell or an enemy cell) or if it is
% a cell that can move to the right if it is adjacent to a cell that can move to the right, which is any cell except the the wall
% cell and the vertical block cell (which can never be moved in the right direction) and the generator left and the arrow left
% cell (because they move in a direction opposite to the right one).
is_movable_right(_, cell(T, _, _)) :- (T = empty; T = enemy), !.
is_movable_right(M, cell(T, X, Y)) :- (T \= wall; T \= block_ver; T \= generator_left; T \= arrow_left),
                                      X1 is X + 1,
                                      member(cell(T1, X1, Y), M),
                                      is_movable_right(M, cell(T1, X1, Y)),
                                      !.

% move_right(Machine, NextMachine)
%
% This predicate shifts all cells in the machine to the right by one position except the enemy cells and the empty cells, which
% are deleted.
move_right([cell(T, X, Y) | CS], X, Y, CS) :- (T = empty; T = enemy), X1 is X + 1, !.
move_right([cell(T, X, Y) | CS], X1, Y1, [cell(T, X, Y) | NCS]) :- (X \= X1; Y \= Y1), move_right(CS, X1, Y1, NCS), !.
move_right([cell(_, X, Y) | CS], X, Y, NM) :- X1 is X + 1, member(cell(enemy, X1, Y), CS), move_right(CS, X1, Y, NM), !.
move_right([cell(T, X, Y) | CS], X, Y, [cell(T, X1, Y) | NCS]) :- X1 is X + 1, move_right(CS, X1, Y, NCS).

% arrow_right_next_state(Machine, NextMachine)
%
% This predicate allows to update the state of a machine (a list of cells on a board) and obtain its next state when the first
% cell is a "arrow right" cell. Hence, this predicate is for applying the "arrow right" rule. The cells that makes its
% "surroundings" and needs to be passed with the first cell in the machine are all cells which are to the right of the first cell.
% These cells need to be inserted into the machine until an empty cell, an enemy cell or a wall cell is met while moving left to
% right from the first arrow cell. If the row of cells is deemed to be movable to the right, all cells are then moved and the last
% one deleted. This is because, in this case, the last cell can only be an enemy cell or an empty cell. The order of the cells in
% the list can not respect the order of the cells in the board.
arrow_right_next_state(M, cell(arrow_right, X, Y), NM) :- member(cell(arrow_right, X, Y), M),
                                                          is_movable_right(M, cell(arrow_right, X, Y)),
                                                          move_right(M, X, Y, NM),
                                                          !.
arrow_right_next_state(M, M).

generate_right([cell(empty, X, Y) | CS], X, Y, T1, [cell(T1, X, Y) | CS]):- !.
generate_right([cell(enemy, X, Y) | CS], X, Y, T1, CS):- !.
generate_right([cell(T, X, Y) | CS], X, Y, T1, [cell(T, X, Y)|CS]) :- (T = wall; T = block_ver; T = arrow_left), !.
generate_right([cell(T, X, Y) | CS], X1, Y1, T1, [cell(T, X, Y) | NCS]) :- (X \= X1; Y \= Y1), generate_right(CS, X1, Y1, T1, NCS), !.
generate_right([cell(T1, X, Y) | CS], X, Y, T1, [cell(T1, X, Y) | NCS]) :- X1 is X + 1, generate_right(CS, X1, Y, T1, NCS).

generator_right_next_state(M, cell(generator_right, X, Y), NM) :- X1 is X - 1, X2 is X + 1,
                                                                  member(cell(T, X1, Y), M),
                                                                  generate_right(M, X2, Y, T, NM),
                                                                  !.

generator_right_next_state(M, cell(generator_right, X, Y), M).

rotate_right([], _, _, []).
rotate_right([cell(T, X1, Y1) | CS], X, Y, [cell(T1, X1, Y1) | NCS]) :- ((X1 is X + 1, Y1 is Y);
                                                                         (X1 is X, Y1 is Y + 1);
                                                                         (X1 is X - 1, Y1 is Y);
                                                                         (X1 is X, Y1 is Y - 1)),
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

% rotate_right_next_state(Machine, NextMachine)
%
% This predicate allows to update the state of a machine (a list of cells on a board) and obtain its next state when the first
% cell is a "rotate right" cell. Hence, this predicate is for applying the "rotate right" rule. The cells that makes its
% "surroundings" and needs to be passed with the first cell in the machine are its orthogonally adjacents cells. Then each cell,
% except for the first, rotate cell, is then rotated clockwise if it can be rotated. If it can not, is then leaved as it is. No
% translation of coordinates is applied by this rule.
rotate_right_next_state(M, cell(rotate_right, X, Y), NM) :- member(cell(rotate_right, X, Y), M), !, rotate_right(M, X, Y, NM).

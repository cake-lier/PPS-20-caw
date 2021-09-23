% last_index_right(@Board, @Type, @XCoordinate, @YCoordinate, -EndingXCoordinate)
%
% Returns the ending x coordinate of the range of positions which hold cells of the given board that can be moved to the right
% with the cell which coordinates and type are given as starting cell. For calculating this value, the predicate hence starts from
% the initial cell and then recursively moves along the board from left to right on the same row of the previous cell. This means
% that getting the next cell is equivalent to getting the one adjacent to the right to the previous one. Any given cell can move
% to the right if it is one that can always move to the right, like an "enemy" cell, or if it is a cell that can move to the right
% if it is adjacent to a cell that can move to the right, which is any cell except the "wall" and the "vertical block" cells
% (which can never be moved in the right direction) and the "generator left" and the "mover left" cells (because they move in a
% direction opposite to the right one). If no other cell is found while traversing the board as previously described, it is
% assumed that an "empty" cell has been met, which can always be moved to the right. No ordering is required for the cells in the
% board.
last_index_right(_, enemy, X, _, X) :- !.
last_index_right(B, T, X, Y, EX) :- T \= wall,
                                    T \= block_ver,
                                    T \= mover_left,
                                    X1 is X + 1,
                                    (member(cell(_, T1, X1, Y), B) -> last_index_right(B, T1, X1, Y, EX); EX = X).

% move_right(@Board, @Board, @StartingXCoordinate, @EndingXCoordinate, @YCoordinate, -NextBoard)
%
% Generates the next board applying the rules for moving to the right by one position the cells of the given board which have the
% given y coordinate and an x coordinate which lies between the given starting x coordinate and the ending x coordinate (endpoints
% included). There must be no "empty" cells between the ones that need to be moved, nor any cell that can not be moved, otherwise
% this predicate will yield an incorrect result. The checks are left to the invoker of this predicate. Any cell in the board that
% has coordinates which are not conforming to the ones previously specified are ignored and copied in the result as is. If the
% last cell met while moving them is an "enemy" cell, the cell is destroyed as is destroyed the one previous to the "enemy" cell.
% Those two cells then will not be present in the next board. No ordering is required for the cells in the board.
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

% mover_right_next_state(@Board, @XCoordinate, @YCoordinate, -NextBoard)
%
% Allows to update the state of a board and obtain its next state applying the rule for the "mover right" cell behavior. If the
% cells which could be pushed by the "mover right" cell are deemed to be really movable to the right by one position, the cells
% are then moved. If no action can be performed, the input board is given as output. Any cell in the board that is not affected by
% this rule is ignored and copied in the result as is. If the last cell which can be moved is an "enemy" cell, the cell is
% destroyed as is destroyed the one previous to the "enemy" cell. Hence, those two cells will not be present in the next board
% state. If the coordinates does not point to a "mover right" cell, the predicate simply evaluates to "no". No ordering is
% required for the cells in the board and the "empty" cells must not be represented.
mover_right_next_state(B, X, Y, NB) :- member(cell(_, mover_right, X, Y), B),
                                       last_index_right(B, mover_right, X, Y, EX),
                                       move_right(B, B, X, EX, Y, NB),
                                       !.
mover_right_next_state(B, _, _, B).

% last_index_left(@Board, @Type, @XCoordinate, @YCoordinate, -EndingXCoordinate)
%
% Returns the ending x coordinate of the range of positions which hold cells of the given board that can be moved to the left
% with the cell which coordinates and type are given as starting cell. For calculating this value, the predicate hence starts from
% the initial cell and then recursively moves along the board from right to left on the same row of the previous cell. This means
% that getting the next cell is equivalent to getting the one adjacent to the left to the previous one. Any given cell can move
% to the left if it is one that can always move to the left, like an "enemy" cell, or if it is a cell that can move to the left
% if it is adjacent to a cell that can move to the left, which is any cell except the "wall" and the "vertical block" cells
% (which can never be moved in the left direction) and the "generator right" and the "mover right" cells (because they move in a
% direction opposite to the left one). If no other cell is found while traversing the board as previously described, it is
% assumed that an "empty" cell has been met, which can always be moved to the left. No ordering is required for the cells in the
% board.
last_index_left(_, enemy, X, _, X) :- !.
last_index_left(B, T, X, Y, EX) :- T \= wall,
                                   T \= block_ver,
                                   T \= mover_right,
                                   X1 is X - 1,
                                   (member(cell(_, T1, X1, Y), B) -> last_index_left(B, T1, X1, Y, EX); EX = X).

% move_left(@Board, @Board, @StartingXCoordinate, @EndingXCoordinate, @YCoordinate, -NextBoard)
%
% Generates the next board applying the rules for moving to the left by one position the cells of the given board which have the
% given y coordinate and an x coordinate which lies between the given starting x coordinate and the ending x coordinate (endpoints
% included). There must be no "empty" cells between the ones that need to be moved, nor any cell that can not be moved, otherwise
% this predicate will yield an incorrect result. The checks are left to the invoker of this predicate. Any cell in the board that
% has coordinates which are not conforming to the ones previously specified are ignored and copied in the result as is. If the
% last cell met while moving them is an "enemy" cell, the cell is destroyed as is destroyed the one previous to the "enemy" cell.
% Those two cells then will not be present in the next board. No ordering is required for the cells in the board.
move_left([], _, _, _, _, []).
move_left([cell(_, enemy, EX, Y) | Cs], B, SX, EX, Y, NB) :- move_left(Cs, B, SX, EX, Y, NB), !.
move_left([cell(I, T, X, Y) | Cs], B, SX, EX, Y1, [cell(I, T, X, Y) | NCs]) :- ((T = generator_left, X =:= SX, Y =:= Y1);
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

% mover_left_next_state(@Board, @XCoordinate, @YCoordinate, -NextBoard)
%
% Allows to update the state of a board and obtain its next state applying the rule for the "mover left" cell behavior. If the
% cells which could be pushed by the "mover left" cell are deemed to be really movable to the left by one position, the cells
% are then moved. If no action can be performed, the input board is given as output. Any cell in the board that is not affected by
% this rule is ignored and copied in the result as is. If the last cell which can be moved is an "enemy" cell, the cell is
% destroyed as is destroyed the one previous to the "enemy" cell. Hence, those two cells will not be present in the next board
% state. If the coordinates does not point to a "mover left" cell, the predicate simply evaluates to "no". No ordering is required
% for the cells in the board and the "empty" cells must not be represented.
mover_left_next_state(B, X, Y, NB) :- member(cell(_, mover_left, X, Y), B),
                                      last_index_left(B, mover_left, X, Y, EX),
                                      move_left(B, B, X, EX, Y, NB),
                                      !.
mover_left_next_state(B, _, _, B).

% last_index_down(@Board, @Type, @XCoordinate, @YCoordinate, -EndingXCoordinate)
%
% Returns the ending y coordinate of the range of positions which hold cells of the given board that can be moved to the bottom
% with the cell which coordinates and type are given as starting cell. For calculating this value, the predicate hence starts from
% the initial cell and then recursively moves along the board from top to bottom on the same column of the previous cell. This
% means that getting the next cell is equivalent to getting the one adjacent to the bottom of the previous one. Any given cell can
% move to the bottom if it is one that can always move to the bottom, like an "enemy" cell, or if it is a cell that can move to
% the bottom if it is adjacent to a cell that can move to the bottom, which is any cell except the "wall" and the "horizontal
% block" cells (which can never be moved in the bottom direction) and the "generator top" and the "mover top" cells (because
% they move in a direction opposite to the bottom one). If no other cell is found while traversing the board as previously
% described, it is assumed that an "empty" cell has been met, which can always be moved to the bottom. No ordering is required for
% the cells in the board.
last_index_down(_, enemy, _, Y, Y) :- !.
last_index_down(B, T, X, Y, EY) :- T \= wall,
                                  T \= block_hor,
                                  T \= mover_top,
                                  Y1 is Y + 1,
                                  (member(cell(_, T1, X, Y1), B) -> last_index_down(B, T1, X, Y1, EY); EY = Y).

% move_down(@Board, @Board, @StartingXCoordinate, @EndingXCoordinate, @YCoordinate, -NextBoard)
%
% Generates the next board applying the rules for moving to the bottom by one position the cells of the given board which have the
% given x coordinate and a y coordinate which lies between the given starting y coordinate and the ending y coordinate (endpoints
% included). There must be no "empty" cells between the ones that need to be moved, nor any cell that can not be moved, otherwise
% this predicate will yield an incorrect result. The checks are left to the invoker of this predicate. Any cell in the board that
% has coordinates which are not conforming to the ones previously specified are ignored and copied in the result as is. If the
% last cell met while moving them is an "enemy" cell, the cell is destroyed as is destroyed the one previous to the "enemy" cell.
% Those two cells then will not be present in the next board. No ordering is required for the cells in the board.
move_down([], _, _, _, _, []).

move_down([cell(_, enemy, X, EY) | Cs], B, SY, EY, X, NB) :- move_down(Cs, B, SY, EY, X, NB), !.
move_down([cell(I, T, X, Y) | Cs], B, SY, EY, X1, [cell(I, T, X, Y) | NCs]) :- ((T = generator_down, Y =:= SY, X =:= X1);
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

% mover_down_next_state(@Board, @XCoordinate, @YCoordinate, -NextBoard)
%
% Allows to update the state of a board and obtain its next state applying the rule for the "mover down" cell behavior. If the
% cells which could be pushed by the "mover down" cell are deemed to be really movable to the bottom by one position, the cells
% are then moved. If no action can be performed, the input board is given as output. Any cell in the board that is not affected by
% this rule is ignored and copied in the result as is. If the last cell which can be moved is an "enemy" cell, the cell is
% destroyed as is destroyed the one previous to the "enemy" cell. Hence, those two cells will not be present in the next board
% state. If the coordinates does not point to a "mover down" cell, the predicate simply evaluates to "no". No ordering is
% required for the cells in the board and the "empty" cells must not be represented.
mover_down_next_state(B, X, Y, NB) :- member(cell(_,mover_down, X, Y), B),
                                     last_index_down(B, mover_down, X, Y, EY),
                                     move_down(B, B, Y, EY, X, NB),
                                     !.
mover_down_next_state(B, _, _, B).

% last_index_top(@Board, @Type, @XCoordinate, @YCoordinate, -EndingYCoordinate)
%
% Returns the ending y coordinate of the range of positions which hold cells of the given board that can be moved to the top
% with the cell which coordinates and type are given as starting cell. For calculating this value, the predicate hence starts from
% the initial cell and then recursively moves along the board from bottom to top on the same column of the previous cell. This
% means that getting the next cell is equivalent to getting the one adjacent on the top of the previous one. Any given cell can
% move to the top if it is one that can always move to the top, like an "enemy" cell, or if it is a cell that can move to the top
% if it is adjacent to a cell that can move to the top, which is any cell except the "wall" and the "horizontal block" cells
% (which can never be moved in the top direction) and the "generator down" and the "mover down" cells (because they move in a
% direction opposite to the top one). If no other cell is found while traversing the board as previously described, it is
% assumed that an "empty" cell has been met, which can always be moved to the top. No ordering is required for the cells in the
% board.
last_index_top(_, enemy, _, Y, Y) :- !.
last_index_top(B, T, X, Y, EY) :-  T \= wall,
                                    T \= block_hor,
                                    T \= mover_down,
                                    Y1 is Y - 1,
                                    (member(cell(_, T1, X, Y1), B) -> last_index_top(B, T1, X, Y1, EY); EY = Y).

% move_top(@Board, @Board, @StartingXCoordinate, @EndingXCoordinate, @YCoordinate, -NextBoard)
%
% Generates the next board applying the rules for moving to the top by one position the cells of the given board which have the
% given x coordinate and a y coordinate which lies between the given starting y coordinate and the ending y coordinate (endpoints
% included). There must be no "empty" cells between the ones that need to be moved, nor any cell that can not be moved, otherwise
% this predicate will yield an incorrect result. The checks are left to the invoker of this predicate. Any cell in the board that
% has coordinates which are not conforming to the ones previously specified are ignored and copied in the result as is. If the
% last cell met while moving them is an "enemy" cell, the cell is destroyed as is destroyed the one previous to the "enemy" cell.
% Those two cells then will not be present in the next board. No ordering is required for the cells in the board.
move_top([], _, _, _, _, []).
move_top([cell(_, enemy, X, EY) | Cs], B, SY, EY, X, NB) :- move_top(Cs, B, SY, EY, X, NB), !.
move_top([cell(I, T, X, Y) | Cs], B, SY, EY, X1, [cell(I, T, X, Y) | NCs]) :- ((T = generator_top, Y =:= SY, X =:= X1);
                                                                                Y > SY;
                                                                                Y < EY;
                                                                                X =\= X1),
                                                                               move_top(Cs, B, SY, EY, X1, NCs),
                                                                               !.
move_top([cell(_, _, X, Y) | Cs], B, SY, EY, X, NB) :- Y =< SY,
                                                        Y > EY,
                                                        Y1 is Y - 1,
                                                        member(cell(_, enemy, X, Y1), B),
                                                        move_top(Cs, B, SY, EY, X, NB),
                                                        !.
move_top([cell(I, T, X, Y) | Cs], B, SY, EY, X, [cell(I, T, X, Y1)| NCs]) :- Y =< SY,
                                                                              Y >= EY,
                                                                              Y1 is Y - 1,
                                                                              move_top(Cs, B, SY, EY, X, NCs).

% mover_top_next_state(@Board, @XCoordinate, @YCoordinate, -NextBoard)
%
% Allows to update the state of a board and obtain its next state applying the rule for the "mover top" cell behavior. If the
% cells which could be pushed by the "mover top" cell are deemed to be really movable to the top by one position, the cells
% are then moved. If no action can be performed, the input board is given as output. Any cell in the board that is not affected by
% this rule is ignored and copied in the result as is. If the last cell which can be moved is an "enemy" cell, the cell is
% destroyed as is destroyed the one previous to the "enemy" cell. Hence, those two cells will not be present in the next board
% state. If the coordinates does not point to a "mover right" cell, the predicate simply evaluates to "no". No ordering is
% required for the cells in the board and the "empty" cells must not be represented.
mover_top_next_state(B, X, Y, NB) :- member(cell(_, mover_top, X, Y), B),
                                      last_index_top(B, mover_top, X, Y, EY),
                                      move_top(B, B, Y, EY, X, NB),
                                      !.
mover_top_next_state(B, _, _, B).

% rotate_clockwise(@Board, @XCoordinate, @YCoordinate, -NextBoard)
%
% Generates the next board applying the rules for rotating in a clockwise direction the cells which are orthogonally adjacent to
% the cell which coordinates are given (more formally, the cells which have a L1 distance of 1 from the cell which coordinates are
% given). If they can not be rotated or are not orthogonally adjacent, no operation is performed and the cells are copied in the
% new board as is. No ordering is required for the cells in the board.
rotate_clockwise([], _, _, []).
rotate_clockwise([cell(I, T, X1, Y1) | Cs], X, Y, [cell(I, T1, X1, Y1) | NCs]) :- ((X1 is X + 1, Y1 =:= Y);
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
                                                                                  rotate_clockwise(Cs, X, Y, NCs),
                                                                                  !.
rotate_clockwise([cell(I, T, X1, Y1) | Cs], X, Y, [cell(I, T, X1, Y1) | NCs]) :- rotate_clockwise(Cs, X, Y, NCs).

% rotator_clockwise_next_state(@Board, @XCoordinate, @YCoordinate, -NextBoard)
%
% Allows to update the state of a board and obtain its next state applying the rule for the "rotator right" cell behavior. The
% rule rotate in a clockwise direction the cells which are orthogonally adjacent to the "rotator" cell which coordinates are
% given. If they can not be rotated or are not orthogonally adjacent, the cells are left as they were. If the coordinates does not
% point to a "rotator right" cell, the predicate simply evaluates to "no". No ordering is required for the cells in the board and
% the empty cells must not be present.
rotator_clockwise_next_state(B, X, Y, NB) :- member(cell(_, rotator_clockwise, X, Y), B), !, rotate_clockwise(B, X, Y, NB).

% rotate_counterclockwise(@Board, @XCoordinate, @YCoordinate, -NextBoard)
%
% Generates the next board applying the rules for rotating in a counterclockwise direction the cells which are orthogonally
% adjacent to the cell which coordinates are given (more formally, the cells which have a L1 distance of 1 from the cell which
% coordinates are given). If they can not be rotated or are not orthogonally adjacent, no operation is performed and the cells are
% copied in the new board as is. No ordering is required for the cells in the board.
rotate_counterclockwise([], _, _, []).
rotate_counterclockwise([cell(I, T, X1, Y1) | Cs], X, Y, [cell(I, T1, X1, Y1) | NCs]) :- ((X1 is X + 1, Y1 =:= Y);
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
                                                                                         rotate_counterclockwise(Cs, X, Y, NCs),
                                                                                         !.
rotate_counterclockwise([cell(I, T, X1, Y1) | Cs], X, Y, [cell(I, T, X1, Y1) | NCs]) :- rotate_counterclockwise(Cs, X, Y, NCs).

% rotator_counterclockwise_next_state(@Board, @XCoordinate, @YCoordinate, -NextBoard)
%
% Allows to update the state of a board and obtain its next state applying the rule for the "rotator left" cell behavior. The
% rule rotate in a clockwise direction the cells which are orthogonally adjacent to the "rotator" cell which coordinates are
% given. If they can not be rotated or are not orthogonally adjacent, the cells are left as they were. If the coordinates does not
% point to a "rotator left" cell, the predicate simply evaluates to "no". No ordering is required for the cells in the board and
% the empty cells must not be present
rotator_counterclockwise_next_state(B, X, Y, NB) :- member(cell(_, rotator_counterclockwise, X, Y), B), !, rotate_counterclockwise(B, X, Y, NB).

% generate_right(@Board, @Type, @StartingXCoordinate, @EndingXCoordinate, @YCoordinate, @MaxId, -NextBoard)
%
% Generates the next board applying the rules for generating a new cell to the right of the "generator right" cell which is
% located at the given starting x coordinate and the given y coordinate. It is assumed that this cell is present, as well as a
% cell to generate, so no checks are performed for ensuring that this is indeed the case. Those checks are left to the invoker of
% this predicate. For first, it checks whether or not the first cell after the "generator right" is an "enemy" cell. In this case,
% no generation needs to be performed and the "enemy" cell is to be destroyed, leaving all other cells unmodified. If that is not
% the case, then the cell to the left of the "generator right" cell is generated in the right position, knowing its type, which is
% given, and all cells adjacent to the right to the "generator right" cell, the ones which have an x coordinate which lies between
% the given starting x coordinate and the ending x coordinate (endpoints included) and the given y coordinate, are moved one
% position to the right using "move_right". No ordering is required for the cells in the board.
generate_right(B, _, X, EX, Y, _, NB) :- X1 is X + 1, member(cell(_, enemy, X1, Y), B), move_right(B, B, X, EX, Y, NB), !.
generate_right(B, T, X, EX, Y, M, [cell(M, T, X1, Y) | NB]) :- X1 is X + 1, move_right(B, B, X, EX, Y, NB).

% generator_right_next_state(@Board, @XCoordinate, @YCoordinate, @MaxId, -NextBoard)
%
% Allows to update the state of a board and obtain its next state applying the rule for the "generator right" cell behavior.
% If a cell is adjacent to the left of the "generator right" which coordinates are given and the cells which could be pushed by the
% generation are deemed to be really movable to the right by one position, the generation is then attempted. However, if the
% adjacent cell is an enemy cell, the board is unchanged. If no action can be performed, the input board is given as output.
% Any cell in the board that is not affected by this rule is ignored and copied in the result as is.
% If the coordinates does not point to a "generator right" cell, the predicate simply evaluates to "no".
% No ordering is required for the cells in the board and the empty cells must not be represented.
generator_right_next_state(B, X, Y, M, B) :- member(cell(_, generator_right, X, Y), B),
                                             X1 is X - 1,
                                             member(cell(_, enemy, X1, Y), B),
                                             !.
generator_right_next_state(B, X, Y, M, NB) :- member(cell(_, generator_right, X, Y), B),
                                              X1 is X - 1,
                                              member(cell(_, T, X1, Y), B),
                                              last_index_right(B, generator_right, X, Y, EX),
                                              generate_right(B, T, X, EX, Y, M, NB),
                                              !.
generator_right_next_state(B, _, _, _, B).

% generate_left(@Board, @Type, @StartingXCoordinate, @EndingXCoordinate, @YCoordinate, @MaxId, -NextBoard)
%
% Generates the next board applying the rules for generating a new cell to the right of the "generator left" cell which is
% located at the given starting x coordinate and the given y coordinate. It is assumed that this cell is present, as well as a
% cell to generate, so no checks are performed for ensuring that this is indeed the case. Those checks are left to the invoker of
% this predicate. For first, it checks whether or not the first cell before the "generator left" is an "enemy" cell. In this case,
% no generation needs to be performed and the "enemy" cell is to be destroyed, leaving all other cells unmodified. If that is not
% the case, then the cell to the right of the "generator left" cell is generated in the right position, knowing its type, which is
% given, and all cells adjacent to the left to the "generator left" cell, the ones which have an x coordinate which lies between
% the given starting x coordinate and the ending x coordinate (endpoints included) and the given y coordinate, are moved one
% position to the left using "move_left". No ordering is required for the cells in the board.
generate_left(B, _, X, EX, Y, _, NB) :- X1 is X - 1, member(cell(_, enemy, X1, Y), B), move_left(B, B, X, EX, Y, NB), !.
generate_left(B, T, X, EX, Y, M, [cell(M, T, X1, Y) | NB]) :- X1 is X - 1, move_left(B, B, X, EX, Y, NB).

% generator_left_next_state(@Board, @XCoordinate, @YCoordinate, @MaxId, -NextBoard)
%
% Allows to update the state of a board and obtain its next state applying the rule for the "generator left" cell behavior.
% If a cell is adjacent to the right of the "generator left" which coordinates are given and the cells which could be pushed by the
% generation are deemed to be really movable to the left by one position, the generation is then attempted. However, if the
% adjacent cell is an enemy cell, the board is unchanged. If no action can be performed, the input board is given as output.
% Any cell in the board that is not affected by this rule is ignored and copied in the result as is.
% If the coordinates does not point to a "generator left" cell, the predicate simply evaluates to "no".
% No ordering is required for the cells in the board and the empty cells must not be represented.
generator_left_next_state(B, X, Y, M, B) :- member(cell(_, generator_left, X, Y), B),
                                            X1 is X + 1,
                                            member(cell(_, enemy, X1, Y), B),
                                            !.
generator_left_next_state(B, X, Y, M, NB) :- member(cell(_, generator_left, X, Y), B),
                                             X1 is X + 1,
                                             member(cell(_, T, X1, Y), B),
                                             last_index_left(B, generator_left, X, Y, EX),
                                             generate_left(B, T, X, EX, Y, M, NB),
                                             !.
generator_left_next_state(B, _, _, _, B).

% generate_down(@Board, @Type, @StartingXCoordinate, @EndingXCoordinate, @YCoordinate, @MaxId, -NextBoard)
%
% Generates the next board applying the rules for generating a new cell below of the "generator down" cell which is
% located at the given starting y coordinate and the given x coordinate. It is assumed that this cell is present, as well as a
% cell to generate, so no checks are performed for ensuring that this is indeed the case. Those checks are left to the invoker of
% this predicate. For first, it checks whether or not the first cell below the "generator down" is an "enemy" cell. In this case,
% no generation needs to be performed and the "enemy" cell is to be destroyed, leaving all other cells unmodified. If that is not
% the case, then the cell above the "generator down" cell is generated in the right position, knowing its type, which is
% given, and all cells adjacent below the "generator down" cell, the ones which have a y coordinate which lies between
% the given starting y coordinate and the ending y coordinate (endpoints included) and the given x coordinate, are moved one
% position below using "move_down". No ordering is required for the cells in the board.
generate_down(B, _, Y, EY, X, _, NB) :- Y1 is Y + 1, member(cell(_, enemy, X, Y1), B), move_down(B, B, Y, EY, X, NB), !.
generate_down(B, T, Y, EY, X, M, [cell(M, T, X, Y1) | NB]) :-  Y1 is Y + 1, move_down(B, B, Y, EY, X, NB).

% generator_down_next_state(@Board, @XCoordinate, @YCoordinate, @MaxId, -NextBoard)
%
% Allows to update the state of a board and obtain its next state applying the rule for the "generator down" cell behavior.
% If a cell is adjacent above the "generator down" which coordinates are given and the cells which could be pushed by the
% generation are deemed to be really movable below by one position, the generation is then attempted. However, if the
% adjacent cell is an enemy cell, the board is unchanged. If no action can be performed, the input board is given as output.
% Any cell in the board that is not affected by this rule is ignored and copied in the result as is.
% If the coordinates does not point to a "generator down" cell, the predicate simply evaluates to "no".
% No ordering is required for the cells in the board and the empty cells must not be represented.
generator_down_next_state(B, X, Y, M, B) :- member(cell(_, generator_down, X, Y), B),
                                            Y1 is Y - 1,
                                            member(cell(_, enemy, X, Y1), B),
                                            !.
generator_down_next_state(B, X, Y, M, NB) :- member(cell(_, generator_down, X, Y), B),
                                            Y1 is Y - 1,
                                            member(cell(_, T, X, Y1), B),
                                            last_index_down(B, generator_down, X, Y, EY),
                                            generate_down(B, T, Y, EY, X, M, NB),
                                            !.
generator_down_next_state(B, _, _, _, B).

% generate_top(@Board, @Type, @StartingXCoordinate, @EndingXCoordinate, @YCoordinate, @MaxId, -NextBoard)
%
% Generates the next board applying the rules for generating a new cell above the "generator top" cell which is located at the
% given starting y coordinate and the given x coordinate. It is assumed that this cell is present, as well as a cell to generate,
% so no checks are performed for ensuring that this is indeed the case. Those checks are left to the invoker of this predicate.
% For first, it checks whether or not the first cell above the "generator top" is an "enemy" cell. In this case, no generation
% needs to be performed and the "enemy" cell is to be destroyed, leaving all other cells unmodified. If that is not the case, then
% the cell below the "generator top" cell is generated in the right position, knowing its type, which is given, and all
% cells adjacent above the "generator top" cell, the ones which have a y coordinate which lies between the given starting
% y coordinate and the ending y coordinate (endpoints included) and the given x coordinate, are moved one position above
% using "move_top". No ordering is required for the cells in the board.
generate_top(B, _, Y, EY, X, _, NB) :- Y1 is Y - 1, member(cell(_, enemy, X, Y1), B), move_top(B, B, Y, EY, X, NB), !.
generate_top(B, T, Y, EY, X, M, [cell(M, T, X, Y1) | NB]) :-  Y1 is Y - 1, move_top(B, B, Y, EY, X, NB).

% generator_top_next_state(@Board, @XCoordinate, @YCoordinate, @MaxId, -NextBoard)
%
% Allows to update the state of a board and obtain its next state applying the rule for the "generator top" cell behavior.
% If a cell is adjacent below the "generator top" which coordinates are given and the cells which could be pushed by the
% generation are deemed to be really movable above by one position, the generation is then attempted. However, if the
% adjacent cell is an enemy cell, the board is unchanged. If no action can be performed, the input board is given as output.
% Any cell in the board that is not affected by this rule is ignored and copied in the result as is.
% If the coordinates does not point to a "generator top" cell, the predicate simply evaluates to "no".
% No ordering is required for the cells in the board and the empty cells must not be represented.
generator_top_next_state(B, X, Y, M, B) :- member(cell(_, generator_top, X, Y), B),
                                           Y1 is Y + 1,
                                           member(cell(_, enemy, X, Y1), B),
                                           !.
generator_top_next_state(B, X, Y, M, NB) :- member(cell(_, generator_top, X, Y), B),
                                             Y1 is Y + 1,
                                             member(cell(_, T, X, Y1), B),
                                             last_index_top(B, generator_top, X, Y, EY),
                                             generate_top(B, T, Y, EY, X, M, NB),
                                             !.
generator_top_next_state(B, _, _, _, B).

arrow_right_next_state([cell(arrow_right,0,0),cell(block_hor,1,0),cell(block,10,0)],0,0,[cell(arrow_right,1,0),cell(block_hor,2,0),cell(block,10,0)]).

arrow_left_next_state([cell(arrow_left,10,0),cell(block_hor,9,0),cell(block,0,0)],10,0,[cell(arrow_left,9,0),cell(block_hor,8,0),cell(block,0,0)]).

arrow_top_next_state([cell(arrow_top,0,0),cell(block_ver,0,1),cell(block,0,10)],0,0,[cell(arrow_top,0,1),cell(block_ver,0,2),cell(block,0,10)]).

arrow_down_next_state([cell(arrow_down,0,10),cell(block_ver,0,9),cell(block,0,0)],0,10,[cell(arrow_down,0,9),cell(block_ver,0,8),cell(block,0,0)]).

generator_right_next_state([cell(block_hor,0,0),cell(generator_right,1,0),cell(block,10,0)],1,0,[cell(block_hor,2,0),cell(block_hor,0,0),cell(generator_right,1,0),cell(block,10,0)]).

generator_top_next_state([cell(block_ver,0,0),cell(generator_top,0,1),cell(block,0,10)],0,1,[cell(block_ver,0,2),cell(block_ver,0,0),cell(generator_top,0,1),cell(block,0,10)]).

generator_left_next_state([cell(block_hor,10,0),cell(generator_left,9,0),cell(block,0,0)],9,0,[cell(block_hor,8,0),cell(block_hor,10,0),cell(generator_left,9,0),cell(block,0,0)]).

generator_down_next_state([cell(block_ver,0,10),cell(generator_down,0,9),cell(block,0,0)],0,9,[cell(block_ver,0,8),cell(block_ver,0,10),cell(generator_down,0,9),cell(block,0,0)]).

rotate_left_next_state([cell(block_hor,0,1),cell(rotate_left,1,1),cell(block_hor,1,0),cell(block_hor,2,1),cell(block_hor,1,2)],1,1,[cell(block_ver,1,0),cell(rotate_left,1,1),cell(block_ver,1,2),cell(block_ver,2,1),cell(block_ver,0,1)]).

rotate_left_next_state([cell(block_hor,0,1),cell(rotate_left,1,1),cell(block_hor,1,0),cell(block_hor,2,1),cell(block_hor,1,2)],1,1,[cell(block_ver,1,0),cell(rotate_left,1,1),cell(block_ver,1,2),cell(block_ver,2,1),cell(block_ver,0,1)]).

rotate_left_next_state([cell(rotate_left,1,1),cell(block_hor,1,0),cell(block_hor,1,2),cell(block_hor,2,1),cell(block_hor,0,1)],1,1,[cell(block_ver,1,0),cell(rotate_left,1,1),cell(block_ver,1,2),cell(block_ver,2,1),cell(block_ver,0,1)]).

rotate_left_next_state([cell(rotate_left,1,1),cell(block_hor,0,1),cell(block_hor,1,2),cell(block_hor,1,0),cell(block_hor,2,1)],1,1,[cell(block_ver,1,0),cell(rotate_left,1,1),cell(block_ver,1,2),cell(block_ver,2,1),cell(block_ver,0,1)]).

rotate_right_next_state([cell(block_hor,1,2),cell(block_hor,0,1),cell(rotate_right,1,1),cell(block_hor,1,0),cell(block_hor,2,1)],1,1,[cell(block_ver,1,2),cell(block_ver,0,1),cell(rotate_right,1,1),cell(block_ver,1,0),cell(block_ver,2,1)]).

rotate_right_next_state([cell(block_hor,0,1),cell(rotate_right,1,1),cell(block_hor,1,0),cell(block_hor,2,1),cell(block_hor,1,2)],1,1,[cell(block_ver,1,2),cell(block_ver,0,1),cell(rotate_right,1,1),cell(block_ver,1,0),cell(block_ver,2,1)]).







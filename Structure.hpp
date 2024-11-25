#ifndef _STRUCTURE_H_
#define _STRUCTURE_H_

struct vector
{
    int x,
    int y,
    int z
};

struct Action
{
    bool*tire=NULL;
    bool*Level=NULL;
    bool*Move=NULL;
    vector* Move_direction;
};


#endif 

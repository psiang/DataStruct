#include <cstdio>
#include <cstdlib>
using namespace std;

#define MaxSize 1000010

typedef struct {
    int data[MaxSize];
    int top;
}Stack;

int n, ans = 0;
int row[MaxSize] = {0}, col[MaxSize] = {0}, leftDia[MaxSize] = {0}, rightDia[MaxSize] = {0};

void init();
void work();
bool pop(Stack*&);
void print(Stack*&);
int getTop(Stack*&);
int getLenth(Stack*&);  
bool push(Stack*&, int);
void initStack(Stack*&);
bool stackEmpty(Stack*&);
void destoryStack(Stack*&);

int main() {
    init();
    work();
    system("pause");
    return 0;
}

void work() {
    Stack *s;
    initStack(s);
    push(s, 0);
    while(stackEmpty(s)) {
        int x = getLenth(s), y = getTop(s);
        if (y != 0) recover(x, y);
        pop(s);
        for (y = y + 1; y <= n; y++)
            if (detect(x, y)) {
                if (x != n) {
                    prepare(x, y);
                    push(s, y);
                    push(s, 0);
                }
                else {
                    prepare(x, y);
                    push(s, y);
                    print(s);
                    ans++;
                }
                break;
            }
    }
       printf("Total: %d\n", ans);
}


void init() {
    scanf("%d", &n);
}

int getLenth(Stack *&s) {
    return s->top + 1;
}

void initStack(Stack *&s) {
    s = (Stack *)malloc(sizeof(Stack));
    s->top = -1;
}

bool stackEmpty(Stack *&s) {
    return (s->top > -1);
}

void destoryStack(Stack *&s) {
    free(s);
}

bool push(Stack *&s, int x) {
    if (s->top == MaxSize - 1)
        return false;
    s->top++;
    s->data[s->top] = x;
    return true;
}

bool pop(Stack *&s) {
    if (s->top == -1)
        return false;
    s->top--;
    return true;
}

int getTop(Stack *&s) {
    return s->data[s->top];
}
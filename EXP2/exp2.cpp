#include <cstdio>
#include <cstdlib>
using namespace std;

#define MaxSize 1000010

typedef struct {
    int data[MaxSize];
    int top;
}Stack;

int n, ans = 0;
int row[MaxSize] = {0}, col[MaxSize] = {0}, leftDia[2*MaxSize] = {0}, rightDia[2*MaxSize] = {0};

void init();                                //输入皇后个数n
void work();                                //N皇后求解算法实现
bool pop(Stack*&);                          //弹出栈顶元素
void print(Stack*&);                        //根据栈打印当前解
int getTop(Stack*&);                        //获取栈顶元素
int getLenth(Stack*&);                      //获取栈长度
bool detect(int, int);                      //前一个参数为行，后一参数为列，检测当前位置是否可以放皇后
void prepare(int, int);                     //前一个参数为行，后一参数为列，在当前位置放皇后
void recover(int, int);                     //前一个参数为行，后一参数为列，移除当前位置皇后
bool push(Stack*&, int);                    //入栈
void initStack(Stack*&);                    //新建栈并初始化
bool stackEmpty(Stack*&);                   //判断栈是否为空，为空返回1，不为空返回0
void destroyStack(Stack*&);                 //销毁栈

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
    destroyStack(s);
    printf("Total: %d\n", ans);
}

void print(Stack *&s) {
    for (int i = 0; i <= s->top; i++) {
        for (int j = 1; j <= n; j++)
            if (j != s->data[i])
                printf(" .");
            else
                printf(" x");
        puts("");
    }
    puts("");
}

void init() {
    scanf("%d", &n);
}

int getLenth(Stack *&s) {
    return s->top + 1;
}

void prepare(int x, int y) {
    col[y] = 1;
    leftDia[y - x + n] = 1;
    rightDia[(n << 1) - y - x + 1] = 1;
}

void recover(int x, int y) {
    col[y] = 0;
    leftDia[y - x + n] = 0;
    rightDia[(n << 1) - y - x + 1] = 0;
}

bool detect(int x, int y) {
    if (col[y] == 0 && leftDia[y - x + n] == 0 && rightDia[(n << 1) - y - x + 1] == 0)
        return true;
    return false;
}

void initStack(Stack *&s) {
    s = (Stack *)malloc(sizeof(Stack));
    s->top = -1;
}

bool stackEmpty(Stack *&s) {
    return (s->top > -1);
}

void destroyStack(Stack *&s) {
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

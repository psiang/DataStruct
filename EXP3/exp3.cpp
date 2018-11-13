#include <ctime>
#include <cstdio>
#include <cstdlib>
using namespace std;

#define MaxSize 5
#define CostPerSecond 0.3

typedef struct {
    int carId;
    long long inTime;
}Car;

typedef struct {
    Car data[MaxSize];
    int f, r;
}Queue;

typedef struct {
    Car data[MaxSize];
    int top;
}Stack;

int n;

void init();                        //读入停车场规模
void work();                        //操作函数，进行停车取车等操作
Car formatCar(int, long long);      //构造汽车类，第一个参数是id，第二个参数是绝对秒数

bool queueEmpty(Queue*);            //判断队列是否为空，1为空，0为不空
void initQueue(Queue*&);            //初始化队列
void destoryQueue(Queue*&);         //销毁队列
bool inQueue(Queue*&, Car);         //元素入队列
bool deQueue(Queue*&, Car&);        //元素出队列

bool pop(Stack*&);                  //弹出栈顶元素                  
Car getTop(Stack*&);                //获取栈顶元素
int getLenth(Stack*&);              //获取栈长度
bool push(Stack*&, Car);            //入栈
void initStack(Stack*&);            //新建栈并初始化
bool stackEmpty(Stack*&);           //判断栈是否为空，为空返回1，不为空返回0
void destoryStack(Stack*&);         //销毁栈
bool stackFind(Stack*&, int);       //查找栈中是否有id为第二个参数的元素，有则返回1，否则0

int main() {
    init();
    work();
    system("pause");
    return 0;
}

void work() {
    Queue *queue;
    Stack *stack, *temp;
    initQueue(queue);
    initStack(stack);
    initStack(temp);
    do {
        int x, id;
        printf("Please enter the operation number.(1.Park 2.Pick_up 3.Park_info 4.Wait_info 5.Quit)\n");
        scanf("%d", &x);
        while (x < 1 || x > 5) {
            puts("Out of range, re-enter please.");
            scanf("%d", &x);
        }
        if (x == 1) {
            printf("Please enter the license plate number:");
            scanf("%d", &id);
            if (getLenth(stack) < n) push(stack, formatCar(id, time(NULL)));
            else inQueue(queue, formatCar(id, time(NULL)));
        }
        else
        if (x == 2) {
            printf("Please enter the license plate number:");
            scanf("%d", &id);
            if (!stackFind(stack, id))
                printf("Not found 404.");
            else {
                while (getTop(stack).carId != id) {
                    push(temp, getTop(stack));
                    pop(stack);
                }
                long long outTime = time(NULL);
                Car now = getTop(stack);
                printf("For a total of %ld seconds, you should pay %.2lf yuan\n", outTime - now.inTime, CostPerSecond * (outTime - now.inTime));
                pop(stack);
                while (!stackEmpty(temp)) {
                    push(stack, getTop(temp));
                    pop(temp);
                }
                while (getLenth(stack) < n && !queueEmpty(queue)) {
                    Car inCar;
                    deQueue(queue, inCar);
                    inCar.inTime = time(NULL);
                    push(stack, inCar);
                }
            }
        }
        else
        if (x == 3) {
            printf("The license plates parked in the parking lot are:");
            for (int i = 0; i <= stack->top; i++)
                printf("%d ", stack->data[i].carId);
            puts("");
        }
        else
        if (x == 4) {
            printf("The license plates waitted in the waitting lot are:");
            if (!queueEmpty(queue)) {
                for (int i = (queue->f + 1) % MaxSize; i != queue->r; i = (i + 1) % MaxSize)
                    printf("%d ", queue->data[i].carId);
                printf("%d ", queue->data[queue->r].carId);
            }
            puts("");
        }
        else
            break;
    } while(1);
    destoryQueue(queue);
    destoryStack(stack);
    destoryStack(temp);
}


void init() {
    printf("---Welcome to Siang's Pasrking lot, each car costs 0.3 yuan per second---\n");
    printf("Please enter the size of the parking lot:");
    scanf("%d", &n);
}

Car formatCar(int id, long long nowTime) {
    Car x;
    x.carId = id;
    x.inTime = nowTime;
    return x;
}

void initQueue(Queue *&q) {
    q = (Queue *)malloc(sizeof(Queue));
    q->f = q->r = 0;
}

void destoryQueue(Queue *&q) {
    free(q);
}

bool queueEmpty(Queue *q) {
    return q->f == q->r;
}

bool inQueue(Queue *&q, Car x) {
    if ((q->r + 1) % MaxSize == q->f) 
        return false;
    q->r = (q->r + 1) % MaxSize;
    q->data[q->r] = x;
    return true;
}

bool deQueue(Queue *&q, Car &x) {
    if (q->r==q->f)
        return false;
    q->f = (q->f + 1) % MaxSize;
    x = q->data[q->f];
    return true;
}

int getLenth(Stack *&s) {
    return s->top + 1;
}

void initStack(Stack *&s) {
    s = (Stack *)malloc(sizeof(Stack));
    s->top = -1;
}

bool stackEmpty(Stack *&s) {
    return (s->top == -1);
}

bool stackFind(Stack *&s, int id) {
    for (int i = 0; i <= s->top; i++)
        if (s->data[i].carId == id)
            return true;
    return false;
}

void destoryStack(Stack *&s) {
    free(s);
}

bool push(Stack *&s, Car x) {
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

Car getTop(Stack *&s) {
    return s->data[s->top];
}

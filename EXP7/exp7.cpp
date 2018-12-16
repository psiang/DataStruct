#include <ctime>
#include <cstdio>
#include <cstring>
#include <cstdlib>
using namespace std;

#define MaxN 10010
#define max(a, b) ((a) > (b) ? (a) : (b))
#define min(a, b) ((a) < (b) ? (a) : (b))

int pair[MaxN] = {0};

typedef struct node{
    char d;
    struct node *lc;
    struct node *rc;
} node;

void work();
double caculate(node*);
node* build(int, int, char*);

int main() {
    work();
    system("pause");
    return 0;
}

node* build(int l, int r, char* s) {
    //printf("%d,%d\n",l,r);
    node *x;
    x = (node*)malloc(sizeof(node));
    if (l > r) {
        x->d = '0';
        x->lc = x->rc = NULL;
        return x;
    }
    if (l == r) {
        x->d = s[l];
        x->lc = x->rc = NULL;
        return x;
    }
    if (s[r] == ')' && pair[r] == l)
        return build(l + 1, r - 1, s);
    if (pair[pair[r] - 1] == pair[r] - 1) {
        int addpos = -1;
        for (int i = pair[r] - 1; i >= l; i = pair[i - 1] - 1)
            if (s[i] == '+' || s[i] == '-') {
                addpos = i;
                break;
            }
        for (int i = pair[r] - 1; i > addpos; i = pair[i - 1] - 1)
            pair[i] = addpos;
        if (addpos != -1) {
            x->d = s[addpos];
            x->lc = build(l, addpos - 1, s);
            x->rc = build(addpos + 1, r, s);
            return x;
        }
        else {
            x->d = s[pair[r] - 1];
            x->lc = build(l, pair[r] - 2, s);
            x->rc = build(pair[r], r, s);
            return x;
        }
    }
    x->d = s[pair[r] - 1];
    x->lc = build(l, pair[r] - 2, s);
    x->rc = build(pair[r], r, s);
    return x;
}

double caculate(node* x) {
    if (x->d >= '0' && x->d <= '9')
        return (x->d - '0') * 1.0;
    double y = 0;
    switch (x->d) {
        case '+': y = caculate(x->lc)+caculate(x->rc); break;
        case '-': y = caculate(x->lc)-caculate(x->rc); break;
        case '*': y = caculate(x->lc)*caculate(x->rc); break;
        case '/': y = caculate(x->lc)/caculate(x->rc); break;
    }
    //printf("%c %c %c %lf\n", x->d, x->lc->d, x->rc->d, y);
    return y;
}

void work() {
    node *x;
    char s[MaxN] = {'\0'};
    int stack[MaxN] = {0}, top = 0;
    gets(s);
    for (int i = 0; i < strlen(s); i++)
        if (s[i] == '(') {
            stack[top++] = i;
            pair[i] = i;
        }
        else if (s[i] == ')')
            pair[i] = stack[--top];
        else
            pair[i] = i;
    /*for (int i = 0; i < strlen(s); i++)
        printf("%d ", pair[i]);
    puts("");*/
    x = build(0, strlen(s) - 1, s);
    printf("%lf\n", caculate(x));
}

#include <ctime>
#include <cstdio>
#include <cstdlib>
using namespace std;

#define MAXN 100010
#define max(a, b) ((a) > (b) ? (a) : (b))
#define min(a, b) ((a) < (b) ? (a) : (b))

int n;
double ans = 0, w, v = 0;
double weight[MAXN] = {0}, value[MAXN] = {0};

void init();                    //输入函数
void find(int, int);            //递归求解背包问题

int main() {
    init();
    find(n, w);
    printf("%lf\n", ans);
    system("pause");
    return 0;
}

void find(int x, int w) {
    if (x == 0) {
        ans = max(ans, v);
        return;
    }
    find(x - 1, w);
    if (w >= weight[x]) {
        v += value[x];
        find(x - 1, w - weight[x]);
        v -= value[x];
    }
}

void init() {
    printf("Input N:");
    scanf("%d", &n);
    printf("Input W:");
    scanf("%lf", &w);
    printf("Input wights:");
    for (int i = 1; i <= n; i++)
        scanf("%lf", &weight[i]);
    printf("Input values:");
    for (int i = 1; i <= n; i++)
        scanf("%lf", &value[i]);
}

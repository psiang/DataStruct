#include <ctime>
#include <cstdio>
#include <cstring>
#include <cstdlib>
using namespace std;

#define MaxN 1001
#define pos(i, j, m) ((i) * (m) + (j))
#define max(a, b) ((a) > (b) ? (a) : (b))
#define min(a, b) ((a) < (b) ? (a) : (b))

typedef struct ANode {
    int d;
    struct ANode * next;
} ArcNode;

int top = 0, stack[MaxN * MaxN], ans = 0;
int m, n, visited[MaxN] = {0};
ArcNode* head[MaxN * MaxN];

void init();
void dfs(int);
void add(int, int);

int main() {
    init();
    dfs(pos(1, 1, m));
    printf("%d\n", ans);
    system("pause");
    return 0;
}

void dfs(int x) {
    //printf("%d %d\n", x/m, x%m);
    stack[top++] = x;
    if (x == pos(n, m, m)) {
        ans += 1;
        for (int i = 0; i < top; i++) {
            printf("(%d,%d)", stack[i]/m, stack[i]%m);
            if (i != top - 1)
                printf(" -> ");
            else
                puts("");
        }
        top--;
        return;
    }
    visited[x] = 1;
    for (ArcNode *nx = head[x]; nx != NULL; nx = nx->next)
        if (!visited[nx->d])
            dfs(nx->d);
    visited[x] = 0;
    top--;
    return;
}

void add(int u, int v) {
    //printf("(%d,%d) -- (%d,%d)\n", u/m, u%m, v/m, v%m);
    ArcNode *temp = (ArcNode*)malloc(sizeof(ArcNode));
    temp->d = v;
    temp->next = head[u];
    head[u] = temp;
}

void init() {
    scanf("%d%d", &n, &m);
    int p[m] = {0};
    for (int i = 0; i <= pos(n + 1, m + 1, m); i++)
        head[i] = NULL;
    for (int j = 0; j <= m + 1; j++)
        p[j] = 1;
    for (int i = 1; i <= n; i++)
        for (int j = 1; j <= m; j++) {
            int key = 1;
            if (p[j] == 0) key = 0;
            scanf("%d", &p[j]);
            if (key == 0 && p[j] == 0)
                add(pos(i - 1, j, m), pos(i, j, m)),
                add(pos(i, j, m), pos(i - 1, j, m));
            if (p[j - 1] == 0  && p[j] == 0)
                add(pos(i, j - 1, m), pos(i, j, m)),
                add(pos(i, j, m), pos(i, j - 1, m));
        }    
}

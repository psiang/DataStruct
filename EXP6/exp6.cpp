#include <ctime>
#include <cstdio>
#include <cstdlib>
using namespace std;

#define MaxN 10010
#define max(a, b) ((a) > (b) ? (a) : (b))
#define min(a, b) ((a) < (b) ? (a) : (b))

#define r(a, i) a.data[i].r
#define c(a, i) a.data[i].c
#define d(a, i) a.data[i].d
#define copy_int(a, i, row, col, value) a.data[i].r = row, a.data[i].c = col, a.data[i].d = value
#define copy_tup(a, i, b, j) a.data[i].r = b.data[j].r, a.data[i].c = b.data[j].c, a.data[i].d = b.data[j].d


typedef struct {
    int r;
    int c;
    int d;
} TupNode;

typedef struct {
    int rows;
    int cols;
    int nums;
    TupNode data[MaxN];
} Matrix;

void dispMatrix(Matrix);                //输出稀疏矩阵
void initMatrix(Matrix &);              //读入稀疏矩阵
Matrix tansposition(Matrix);            //反转稀疏矩阵
void findStart(int*, Matrix);           //找到该矩阵每一行的开头是第几个元素
Matrix addition(Matrix, Matrix);        //稀疏矩阵相加
Matrix multiply(Matrix, Matrix);        //稀疏矩阵相乘

int main() {
    Matrix a;
    Matrix b;
    Matrix c;
    /*a.rows=4, a.cols=4,a.nums=6;
    copy_int(a,0,0,0,1);
    copy_int(a,1,0,2,3);
    copy_int(a,2,1,1,1);
    copy_int(a,3,2,2,1);
    copy_int(a,4,3,2,1);
    copy_int(a,5,3,3,1);
    b.rows=4, b.cols=4,b.nums=4;
    copy_int(b,0,0,0,3);
    copy_int(b,1,1,1,4);
    copy_int(b,2,2,2,1);
    copy_int(b,3,3,3,2);*/
    puts("Matrix A");
    initMatrix(a);
    puts("Matrix B");
    initMatrix(b);
    printf("It's the transposition of Matrix A\n");
    dispMatrix(tansposition(a));
    printf("It's the addition of Matrix A\n");
    dispMatrix(addition(a, b));
    printf("It's the multiply of Matrix A\n");
    dispMatrix(multiply(a, b));
    system("pause");
    return 0;
}

void findStart(int cstart[], Matrix c) {
    int num[c.rows] = {0};
    for (int i = 0; i < c.nums; i++)
        num[r(c, i)]++;
    cstart[0] = 0;
    for (int row = 1; row < c.rows; row++)
        cstart[row] = cstart[row - 1] + num[row - 1];
}

Matrix multiply(Matrix a, Matrix b) {
    Matrix c;
    int ta, tb;
    int Arstart[a.rows] = {0}, Brstart[a.rows] = {0}, Crstart[a.rows] = {0};
    c.rows = a.rows;
    c.cols = b.cols;
    c.nums = 0;
    findStart(Arstart, a);
    findStart(Brstart, b);
    for (int i = 0; i < a.rows; i++) {
        int t, temp[b.cols] = {0};
        if (i < a.rows - 1) ta = Arstart[i + 1];
        else ta = a.nums;
        for (int j = Arstart[i]; j < ta; j++) {
            if (c(a, j) < b.rows - 1) tb = Brstart[c(a, j) + 1];
            else tb = b.nums;
            for (int k = Brstart[c(a, j)]; k < tb; k++)
                temp[c(b, k)] += d(a, j) * d(b, k);
        }
        for (int col = 0; col < b.cols; col++)
            if (temp[col])
                copy_int(c, c.nums, i, col, temp[col]), c.nums++;
    }
    return c;
}

Matrix addition(Matrix a, Matrix b) {
    Matrix c;
    int i = 0, j = 0, k = 0;
    c.cols = a.cols;
    c.rows = a.rows;
    while (i < a.nums && j < b.nums) {
        if (r(a, i) > r(b, j)) copy_tup(c, k, b, j), j++, k++;
        else if (r(a, i) < r(b, j)) copy_tup(c, k, a, i), i++, k++;
        else if (c(a, i) < c(b, j)) copy_tup(c, k, a, i), i++, k++;
        else if (c(a, i) > c(b, j)) copy_tup(c, k, b, j), j++, k++;
        else {
            int dd = d(a, i) + d(b, j);
            if (dd != 0) copy_int(c, k, r(a, i), c(a, i), dd), k++;
            i++, j++;
        }
    }
    while (i < a.nums) copy_tup(c, k, a, i), i++, k++;
    while (j < b.nums) copy_tup(c, k, b, j), j++, k++;
    c.nums = k;
    return c;
}

Matrix tansposition(Matrix s) {
    int j = 0;
    Matrix c;
    c.nums = s.nums;
    c.rows = s.rows;
    c.cols = s.cols;
    int num[c.cols] = {0}, cstart[c.cols] = {0};
    for (int i = 0; i < s.nums; i++)
        num[c(s, i)]++;
    cstart[0] = 0;
    for (int col = 1; col < c.cols; col++)
        cstart[col] = cstart[col - 1] + num[col - 1];
    for (int i = 0; i < s.nums; i++) {
        j = cstart[c(s, i)];
        copy_int(c, j, c(s, i), r(s, i), d(s, i));
        cstart[c(s, i)]++;
    }
    return c;
}

void dispMatrix(Matrix s) {
    printf("nums:%d\trows:%d\tcols:%d\n", s.nums, s.rows, s.cols);
    for (int i = 0; i < s.nums; i++)
        printf("row:%d\tcol:%d\tvalue:%d\n", r(s, i), c(s, i), d(s, i));
}

void initMatrix(Matrix &s) {
    printf("Please input the nums,rows,cols in Matrix:\n");
    scanf("%d %d %d", &s.nums, &s.rows, &s.cols);
    printf("Please input the row,col,value of numbers:\n");
    for (int i = 0; i < s.nums; i++)
        scanf("%d %d %d", &r(s, i), &c(s, i), &d(s, i));
}

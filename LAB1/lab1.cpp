#include <cstdio>
#include <cstdlib>
using namespace std;

#define MaxLenth 100010

typedef struct Poly {
    double coefficient, index;
    struct Poly * next;
}Polynomial;

void init();
void InitPolynomial(Polynomial*&);
void ReadPolynomial(Polynomial*&);
void PrintPolynomial(Polynomial*);
bool PolynomialEmpty(Polynomial*&);
void DestoryPolynomial(Polynomial*&);
void InsertPolynomial(Polynomial*&, Polynomial*&);
void AddPolynomial(Polynomial*&, Polynomial*&, Polynomial*&);
void SubPolynomial(Polynomial*&, Polynomial*&, Polynomial*&);
void MulPolynomial(Polynomial*&, Polynomial*&, Polynomial*&);

Polynomial *polyA, *polyB, *polyC;

int main() {
    init();
    system("pause");
    return 0;
}

void InitPolynomial(Polynomial *&L) {
    L = (Polynomial *)malloc(sizeof(Polynomial));
    L->next = NULL;
    L->coefficient = L->index = 0;
}

void DestoryPolynomial(Polynomial *&L) {
    Polynomial *pre = L, *p = L->next;
    while (p != NULL) {
        free(pre);
        pre = p;
        p = p->next;
    }
    free(pre);
}

void InsertPolynomial(Polynomial *&L, Polynomial *&x) {
    Polynomial *p = L;
    while (p->next != NULL && p->next->index >= x->index)
        p = p->next;
    if (p->index == x->index && p != L) {
        p->coefficient += x->coefficient;
        free(x);
    }
    else
    {
        x->next = p->next;
        p->next = x;
    }
}

void PrintPolynomial(Polynomial *L) {
    Polynomial *p = L->next;
    while (p != NULL) {
        if (p != L->next && p->coefficient > 0) printf("+");
        if (p->coefficient != 0) {
            if (p->coefficient != 1 && p->coefficient != -1) printf("%g", p->coefficient);
            if (p->coefficient == -1) printf("-");
            printf("x^%g", p->index);
        }   
        p = p->next;
    }
    puts("");
}

bool PolynomialEmpt(Polynomial *&L) {
    return (L->next == NULL);
}

void ReadPolynomial(Polynomial *&L) {
    char poly[MaxLenth]={'\0'};
    Polynomial *p;
    scanf("%s", poly);
    for (int i = 0; poly[i]!='\0';) {
        int c = 0, x = 0, f = 1;
        p = (Polynomial *)malloc(sizeof(Polynomial));
        if (poly[i]== '-') i++, f=-1;
        if (poly[i] >= '0' && poly[i] <= '9') {
            while (poly[i] >= '0' && poly[i] <= '9') {
                c *= 10;
                c += poly[i] - '0';
                i++;
            }
        }
        else c = 1;
        while (poly[i] < '0' || poly[i] > '9') i++;
        while (poly[i] >= '0' && poly[i] <= '9') {
            x *= 10;
            x += poly[i] - '0';
            i++;
        }
        p->coefficient = c * f;
        p->index = x;
        InsertPolynomial(L, p);
    }
}

void AddPolynomial(Polynomial *&A, Polynomial *&B, Polynomial *&C) {
    Polynomial *p = A->next, *q = B->next, *r = C, *s;
    while (p != NULL && q != NULL) {
        if (p->index > q -> index) {
            InitPolynomial(s);
            s->coefficient = p->coefficient;
            s->index = p->index;
            s->next = r->next;
            r->next = s;
            p = p->next;
            r = r->next;
        }
        else if (p->index < q -> index) {
            InitPolynomial(s);
            s->coefficient = q->coefficient;
            s->index = q->index;
            s->next = r->next;
            r->next = s;
            q = q->next;
            r = r->next;
        }
        else if (p->index == q -> index) {
            InitPolynomial(s);
            s->coefficient = p->coefficient + q->coefficient;
            s->index = q->index;
            s->next = r->next;
            r->next = s;
            p = p->next;
            q = q->next;
            r = r->next;
        }
    }
    while (p!=NULL) {
        InitPolynomial(s);
        s->coefficient = p->coefficient;
        s->index = p->index;
        s->next = r->next;
        r->next = s;
        p = p->next;
        r = r->next;
    }
    while (q!=NULL) {
        InitPolynomial(s);
        s->coefficient = q->coefficient;
        s->index = q->index;
        s->next = r->next;
        r->next = s;
        q = q->next;
        r = r->next;
    }
}

void SubPolynomial(Polynomial *&A, Polynomial *&B, Polynomial *&C) {
    Polynomial *p = A->next, *q = B->next, *r = C, *s;
    while (p != NULL && q != NULL) {
        if (p->index > q -> index) {
            InitPolynomial(s);
            s->coefficient = p->coefficient;
            s->index = p->index;
            s->next = r->next;
            r->next = s;
            p = p->next;
            r = r->next;
        }
        else if (p->index < q -> index) {
            InitPolynomial(s);
            s->coefficient = -q->coefficient;
            s->index = q->index;
            s->next = r->next;
            r->next = s;
            q = q->next;
            r = r->next;
        }
        else if (p->index == q -> index) {
            InitPolynomial(s);
            s->coefficient = p->coefficient - q->coefficient;
            s->index = q->index;
            s->next = r->next;
            r->next = s;
            p = p->next;
            q = q->next;
            r = r->next;
        }
    }
    while (p!=NULL) {
        InitPolynomial(s);
        s->coefficient = p->coefficient;
        s->index = p->index;
        s->next = r->next;
        r->next = s;
        p = p->next;
        r = r->next;
    }
    while (q!=NULL) {
        InitPolynomial(s);
        s->coefficient = q->coefficient;
        s->index = q->index;
        s->next = r->next;
        r->next = s;
        q = q->next;
        r = r->next;
    }
}

void MulPolynomial(Polynomial *&A, Polynomial *&B, Polynomial *&C) {
    Polynomial *p = A->next, *q = B->next, *r = C, *s;
    while (p!=NULL) {
        q = B->next;
        while (q!=NULL) {
            InitPolynomial(s);
            s->coefficient = p->coefficient * q->coefficient;
            s->index = p->index * q->index;
            printf("%g %g\n",s->coefficient,s->index);
            InsertPolynomial(C, s);
            q = q->next;
        }
        p = p->next;
    }
}

void init() {
    InitPolynomial(polyA);
    InitPolynomial(polyB);
    InitPolynomial(polyC);
    printf("Please enter the first polynomial:\n");
    ReadPolynomial(polyA);
    //PrintPolynomial(polyA);
    printf("Please enter the second polynomial:\n");
    ReadPolynomial(polyB);
    //PrintPolynomial(polyB);
    printf("Please select a polynomial operation(1.Add;2.Subtract;3.Multiply):\n");
    int x;
    scanf("%d", &x);
    if (x == 1) AddPolynomial(polyA, polyB, polyC);
    else if (x == 2) SubPolynomial(polyA, polyB, polyC);
    else if (x == 3) MulPolynomial(polyA, polyB, polyC);
    PrintPolynomial(polyC);
    DestoryPolynomial(polyA);
    DestoryPolynomial(polyB);
    DestoryPolynomial(polyC);
}
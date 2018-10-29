#include <cstdio>
#include <cstdlib>
using namespace std;

#define MaxLenth 100010

typedef struct Poly {
    double coefficient, index;
    struct Poly * next;
}Polynomial;

void init();
void initPolynomial(Polynomial*&);
void readPolynomial(Polynomial*&);
void printPolynomial(Polynomial*);
bool polynomialEmpty(Polynomial*&);
void destoryPolynomial(Polynomial*&);
void insertPolynomial(Polynomial*&, Polynomial*&);
void addPolynomial(Polynomial*&, Polynomial*&, Polynomial*&);
void subPolynomial(Polynomial*&, Polynomial*&, Polynomial*&);
void mulPolynomial(Polynomial*&, Polynomial*&, Polynomial*&);

Polynomial *polyA, *polyB, *polyC;

int main() {
    init();
    system("pause");
    return 0;
}

void initPolynomial(Polynomial *&L) {
    L = (Polynomial *)malloc(sizeof(Polynomial));
    L->next = NULL;
    L->coefficient = L->index = 0;
}

void destoryPolynomial(Polynomial *&L) {
    Polynomial *pre = L, *p = L->next;
    while (p != NULL) {
        free(pre);
        pre = p;
        p = p->next;
    }
    free(pre);
}

void insertPolynomial(Polynomial *&L, Polynomial *&x) {
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

void printPolynomial(Polynomial *L) {
    Polynomial *p = L->next;
    if (p == NULL) {
        puts("0");
        return;
    }
    while (p != NULL) {
        if (p != L->next && p->coefficient > 0) printf("+");
        if (p->coefficient != 0) {
            if (p->coefficient != 1 && p->coefficient != -1) printf("%g", p->coefficient);
            if (p->coefficient == -1) printf("-");
            if (p->index != 0) 
                if (p->index != 1) printf("x^%g", p->index);
                else printf("x");
            else if (p->coefficient == 1 || p->coefficient == -1) printf("1");
        }   
        p = p->next;
    }
    puts("");
}

bool polynomialEmpt(Polynomial *&L) {
    return (L->next == NULL);
}

void readPolynomial(Polynomial *&L) {
    char poly[MaxLenth]={'\0'};
    Polynomial *p;
    scanf("%s", poly);
    for (int i = 0; poly[i]!='\0';) {
        double c = 0, x = 0, f = 1, d = 1;
        if (poly[i]== '-') i++, f=-1;
        if (poly[i]== '+') i++, f=1;
        if (poly[i] >= '0' && poly[i] <= '9') {
            while (poly[i] >= '0' && poly[i] <= '9') {
                c *= 10;
                c += poly[i] - '0';
                i++;
            }
            if (poly[i] == '.') {
                i++;
                while (poly[i] >= '0' && poly[i] <= '9') {
                    d /= 10;
                    c += d * (poly[i] - '0');
                    i++;
                }
            }
        }
        else c = 1;
        if (poly[i] == 'x') {
            i++;
            if (poly[i]=='^') {
                i++;
                while (poly[i] >= '0' && poly[i] <= '9') {
                    x *= 10;
                    x += poly[i] - '0';
                    i++;
                }
            }
            else x = 1;
        }
        else x = 0;
        if (c != 0) {
            initPolynomial(p);
            p->coefficient = c * f;
            p->index = x;
            insertPolynomial(L, p);
        }
    }
}

void addPolynomial(Polynomial *&A, Polynomial *&B, Polynomial *&C) {
    Polynomial *p = A->next, *q = B->next, *r = C, *s;
    while (p != NULL && q != NULL) {
        if (p->index > q -> index) {
            initPolynomial(s);
            s->coefficient = p->coefficient;
            s->index = p->index;
            s->next = r->next;
            r->next = s;
            p = p->next;
            r = r->next;
        }
        else if (p->index < q -> index) {
            initPolynomial(s);
            s->coefficient = q->coefficient;
            s->index = q->index;
            s->next = r->next;
            r->next = s;
            q = q->next;
            r = r->next;
        }
        else if (p->index == q -> index) {
            if (p->coefficient + q->coefficient != 0) {
                initPolynomial(s);
                s->coefficient = p->coefficient + q->coefficient;
                s->index = q->index;
                s->next = r->next;
                r->next = s;
                r = r->next;
            }
            p = p->next;
            q = q->next;
        }
    }
    while (p!=NULL) {
        initPolynomial(s);
        s->coefficient = p->coefficient;
        s->index = p->index;
        s->next = r->next;
        r->next = s;
        p = p->next;
        r = r->next;
    }
    while (q!=NULL) {
        initPolynomial(s);
        s->coefficient = q->coefficient;
        s->index = q->index;
        s->next = r->next;
        r->next = s;
        q = q->next;
        r = r->next;
    }
}

void subPolynomial(Polynomial *&A, Polynomial *&B, Polynomial *&C) {
    Polynomial *p = A->next, *q = B->next, *r = C, *s;
    while (p != NULL && q != NULL) {
        if (p->index > q -> index) {
            initPolynomial(s);
            s->coefficient = p->coefficient;
            s->index = p->index;
            s->next = r->next;
            r->next = s;
            p = p->next;
            r = r->next;
        }
        else if (p->index < q -> index) {
            initPolynomial(s);
            s->coefficient = -q->coefficient;
            s->index = q->index;
            s->next = r->next;
            r->next = s;
            q = q->next;
            r = r->next;
        }
        else if (p->index == q -> index) {
            if (p->coefficient - q->coefficient != 0) {
                initPolynomial(s);
                s->coefficient = p->coefficient - q->coefficient;
                s->index = q->index;
                s->next = r->next;
                r->next = s;
                r = r->next;
            }
            p = p->next;
            q = q->next;
        }
    }
    while (p!=NULL) {
        initPolynomial(s);
        s->coefficient = p->coefficient;
        s->index = p->index;
        s->next = r->next;
        r->next = s;
        p = p->next;
        r = r->next;
    }
    while (q!=NULL) {
        initPolynomial(s);
        s->coefficient = q->coefficient;
        s->index = q->index;
        s->next = r->next;
        r->next = s;
        q = q->next;
        r = r->next;
    }
}

void mulPolynomial(Polynomial *&A, Polynomial *&B, Polynomial *&C) {
    Polynomial *p = A->next, *q = B->next, *r = C, *s;
    while (p!=NULL) {
        q = B->next;
        while (q!=NULL) {
            initPolynomial(s);
            s->coefficient = p->coefficient * q->coefficient;
            s->index = p->index + q->index;
            insertPolynomial(C, s);
            q = q->next;
        }
        p = p->next;
    }
}

void init() {
    initPolynomial(polyA);
    initPolynomial(polyB);
    initPolynomial(polyC);
    printf("Please enter the first polynomial:\n");
    readPolynomial(polyA);
    //printPolynomial(polyA);
    printf("Please enter the second polynomial:\n");
    readPolynomial(polyB);
    //printPolynomial(polyB);
    printf("Please select a polynomial operation(1.Add;2.Subtract;3.Multiply):\n");
    int x;
    scanf("%d", &x);
    if (x == 1) addPolynomial(polyA, polyB, polyC);
    else if (x == 2) subPolynomial(polyA, polyB, polyC);
    else if (x == 3) mulPolynomial(polyA, polyB, polyC);
    printf("Result:\n");
    printPolynomial(polyC);
    destoryPolynomial(polyA);
    destoryPolynomial(polyB);
    destoryPolynomial(polyC);
}
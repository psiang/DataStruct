#include <cmath>
#include <stack> 
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <cstdlib>
#include <complex>
#include <algorithm>
using namespace std;

#define PI acos(-1)
#define MAXSTACK 101 
#define MAXLEN 40001 
#define max(a, b) ((a) > (b) ? (a) : (b))
#define min(a, b) ((a) < (b) ? (a) : (b))

class Bium {
	private:
		char num[MAXLEN];                                   //num[] for the number,
        int len, sg;							            //len is the lenth of number, sg is the sign
		
	public:
		Bium();												// Structure for nothing
		Bium (Bium &);										// Structure for Bium
		Bium (const int);									// Structure for int
		Bium (const char *);								// Structure for string
		
		int bigger(const Bium&, const Bium&);				//Compare if former is bigger than later,if they are the same return 2
		
		void swap(Bium&, Bium&);						    //Swap two Biums
		void addtion(Bium&, Bium&, Bium&);			        //Store the second plus the third in the first
		void multiply(Bium&, Bium&, Bium&); 			    //Stroe the second times the third in the first
		void division(Bium&, Bium&, Bium&);			        //Store the second divides the third in the first, and left the remainder in the second
		void subtraction(Bium&, Bium&, Bium&); 		        //Store the second minus the third in the first, after two have the same sg
		void fft(complex<double>*, int*, int, int);         //Fast Fourier transform
		
		bool operator > (const Bium&);						//Compare if former is bigger than later
		bool operator < (const Bium&);						//Compare if former is smaller than later
		bool operator >= (const Bium&);						//Compare if former is not smaller than later
		bool operator <= (const Bium&);						//Compare if former is not bigger than later
		bool operator == (const Bium&);						//Compare if former and later are the same
		bool operator != (const Bium&);						//Compare if former and later are not the same

		Bium operator + (Bium&);				            //Reload + 
		Bium operator - (Bium&);				            //Reload -
		Bium operator * (Bium&);				            //Reload * 
		Bium operator / (Bium&);				            //Reload / 
		Bium operator % (Bium&);				            //Reload %

		Bium factorial();				                    //Caculate factorial
		Bium power(int&);				                    //Calculate the nth power, n is the parameter
		Bium power(Bium&);				                    //Calculate the nth power, n is the parameter
		Bium power(char*&);				                    //Calculate the nth power, n is the parameter
		
		Bium& operator = (const int&);						//Reload = for int
		Bium& operator = (const Bium&);						//Reload = for Bium
		Bium& operator = (const char*&);					//Reload = for string

		Bium& operator += (Bium&);							//Reload +=
		Bium& operator -= (Bium&);							//Reload -=
		Bium& operator *= (Bium&);							//Reload *=
		Bium& operator /= (Bium&);							//Reload /=
		Bium& operator %= (Bium&);							//Reload %=

		Bium& operator ++ ();								//Reload pre ++
		Bium& operator -- ();								//Reload pre --
		Bium operator ++ (int);								//Reload post ++
		Bium operator -- (int);								//Reload post --
		
		read ();											//Read the Bium
		clear ();											//Clear the Bium
		print ();											//Print the Bium
		printL ();											//Print the Bium
		zerocheck();										//Remove the zero in the front of the Bium
};

char stack1[MAXSTACK];
Bium stack2[MAXSTACK];
int top1 = 0, top2 = 0;
char expression[MAXLEN] = {'\0'};
		
int priority(char);		 									//Compare the priority of operator
void transform(char*);										//Get the value of the expression 
void evaluation_expression();								//Read a expression and Get the value of the expression 
Bium calculate(Bium&, Bium&, char);							//Do calculate

int main() {
	evaluation_expression();
    system("pause");
	return 0;
}

Bium::Bium () {num[0] = 0; len = 1; sg = 1;} 	

Bium::Bium (const char *s) {*this = s;}	

Bium::Bium (const int s) {*this = s;}

Bium::Bium (Bium &s) {*this = s;}

Bium::read () {
	char s[MAXLEN];
	scanf("%s", &s);
	*this = s;
}

Bium::clear () {
	len = 1;
	sg = 1;
	memset(num, 0, sizeof(num));
}

Bium::print () {
	this->zerocheck();
	if (sg < 0) printf("-");
	for (int i = len - 1; i >= 0; i--)
		printf("%d", num[i]);
}

Bium::printL () {
	this->print();
	puts("");
}

Bium::zerocheck () {
	while (num[len - 1] == 0 && len != 1) len--;
	if (num[len - 1] == 0 && len == 1) {					//In case 0
		sg = 1;
		len = 1;
		memset(num, 0, sizeof(num));
	}
}

int Bium::bigger(const Bium& a,const Bium& b){
	if (a.sg * b.sg < 0) 
		return a.sg > b.sg;
	else 
	if (a.sg > 0) {
		if (a.len != b.len)
			return (a.len > b.len);
		else {
		 	for (int i = b.len - 1; i >= 0; i--)
		 		if (a.num[i] != b.num[i])
				 	return a.num[i] > b.num[i];
			return 2;
		} 
	}
	else
	if (a.sg < 0) {											//Absolute value a < b if they are negative
		if (a.len != b.len)
			return (a.len < b.len);
		else {
		 	for (int i = b.len - 1; i >= 0; i--)
		 		if (a.num[i] != b.num[i])
				 	return a.num[i] < b.num[i];
			return 2;										//return 2 for the same
		} 
	}
}

void Bium::swap(Bium& a, Bium& b){
	Bium c;
	c = a; a = b; b = c;
}

void Bium::addtion(Bium& c, Bium& a, Bium& b) {
	c.clear(); 
	c.sg = a.sg;
	c.len = max(a.len, b.len);
    int up = 0;
	for (int i = 0; i < c.len; i++) {
        c.num[i] = (i >= a.len? 0: a.num[i]) + (i >= b.len? 0: b.num[i]);
        up = c.num[i] / 10;
        c.num[i] %= 10;
    }
 	while (up) {                                            //Carry for the number
		c.num[c.len++] = up % 10;
		up /= 10;
	}
	c.zerocheck();
}

void Bium::subtraction(Bium& c, Bium& s, Bium& t) {
	Bium a = s;												//In case swap() change s and t in real
	Bium b = t;
	c.clear();
	if (a == b) return;										//To sure a and b are not the same
	if (a.sg > 0)											//To sure the big one minus the small one
		if (b > a) swap(a, b), c.sg = -1;
		else c.sg = 1;
	else 
		if (a > b) swap(a, b), c.sg = 1;
		else c.sg = -1; 
	c.len = a.len;
	int up = 0;
	for (int i = 0; i < c.len; i++) {
		c.num[i] = a.num[i] - ((i >= b.len ? 0: b.num[i]) + up);
		up = 0;
		while (c.num[i] < 0) c.num[i] += 10, up++;
	}
	c.zerocheck();
}

void Bium::multiply(Bium& c, Bium& a, Bium& b) {
    int len = 1;
    int pos[MAXLEN] = {0};
    while (len < a.len + b.len) len <<= 1;
    complex<double> f1[len], f2[len];
    for(int i = 0; i < len; i++)                            //Reverse order replacement
        pos[i] = (i & 1) ? (pos[i >> 1] >> 1) | (len >> 1) : (pos[i >> 1] >> 1);   
    for(int i = 0; i < len; i++) {
        f1[i].real(i < a.len? a.num[i] : 0);
        f1[i].imag(0);
        f2[i].real(i < b.len? b.num[i] : 0);
        f2[i].imag(0);
    }
    fft(f1, pos, len, 1);
    fft(f2, pos, len, 1);
    for(int i = 0; i < len; i++) f1[i] *= f2[i];
    fft(f1, pos, len, -1);
    c.clear();
    c.len = len;
    c.sg = a.sg * b.sg;
    int up = 0;
    for(int i = 0; i < c.len; i++) {
        c.num[i] = int(f1[i].real() + 0.5 + up) % 10;
		up = int(f1[i].real() + 0.5 + up) / 10;             //Carry for the number 
    }
	while (up) {
		c.num[c.len++] = up % 10;
		up /= 10;
	}
	c.zerocheck();
}

void Bium::division(Bium& c, Bium& a, Bium& t) {
	Bium b = t;
	int asg = 1;
	c.clear();
	c.sg = a.sg * b.sg; 
	if (a.sg < 0) asg = -1, a.sg = 1;
	b.sg = 1;
	if (a < b) return;
	for (int i = a.len - 1; i >= 0; i--)                    //Align the divisor with the dividend
		if (a.len - i <= b.len) b.num[i] = b.num[i - (a.len - b.len)];
		else b.num[i] = 0;
	b.len = a.len;
	c.len = 0;
	for (int i = a.len - 1; i >= t.len - 1; i--) { 
		while (a >= b) {
			a = a - b, c.num[b.len - t.len] += 1;
			if (c.len == 0) c.len = b.len - t.len + 1;
		}
		for (int j = 0; j < i; j++) b.num[j] = b.num[j + 1];
		b.num[i] = 0;
		b.len--;
	}
	a.sg = a.sg * asg;
}

Bium Bium::factorial() {
	Bium a, c, one(1);
	c.clear();
	a = *this;
	c = 1;
	while (a != 0) {
		c *= a;
		-- a;
	}
    return c;
}

Bium Bium::power(Bium& t) {
	Bium a, b, c, two(2);
	c.clear();
	a = *this;
	b = t;
	c = 1;
	while (b > 0) {
		if (b % two == 1) c *= a;
		a *= a;
		b /= two;
	}
    return c;
}

void Bium::fft(complex<double>* f, int* pos, int len, int on) {
    complex<double> temp;
    for(int i = 0; i < len; i++)
        if(i < pos[i]) {
            temp = f[i];
            f[i] = f[pos[i]];
            f[pos[i]] = temp;
        }
    for(int i = 1; i < len; i <<= 1)
    {
        complex<double> wn(cos(on * PI / i), sin(on * PI / i));
        for(int j = 0; j < len; j += (i << 1))
        {
            complex<double> wi(1, 0);
            for(int k = j; k < j + i; k++)
            {
                complex<double> u = f[k], v = f[k + i] * wi;
                f[k] = u + v;
                f[k + i] = u - v;
                wi *= wn;
            }
        }
    }
    if(on == -1)
    {
        for(int i = 0; i < len; i++)
            f[i] /= len;
    }
}

bool Bium::operator > (const Bium& s) {
	int temp = bigger(*this, s);
	if (temp == 2) return 0;
	else return temp;
}

bool Bium::operator < (const Bium& s) {
	int temp = bigger(s, *this);
	if (temp == 2) return 0;
	else return temp;
}

bool Bium::operator >= (const Bium& s) {
	int temp = bigger(*this, s);
	if (temp == 2) return 1;
	else return temp;
}

bool Bium::operator <= (const Bium& s) {
	int temp = bigger(s, *this);
	if (temp == 2) return 1;
	else return temp;
}

bool Bium::operator == (const Bium& s) {
	int temp = bigger(s, *this);
	if (temp == 2) return 1;
	else return 0;
}

bool Bium::operator != (const Bium& s) {
	int temp = bigger(s, *this);
	if (temp == 2) return 0;
	else return 1;
}

Bium& Bium::operator = (const Bium& s) {
	this->clear();
	this->sg = s.sg;
	this->len = s.len;
	for (int i = 0; i < len; i++)
		this->num[i] = s.num[i];
	this->zerocheck();
	return *this;
}

Bium& Bium::operator = (const int& s) {
	int a = s;
	this->clear();
	if (a >= 0) this->sg = 1;  else this->sg = -1, a = -a;
	if (a != 0) {
		this->len = 0;
		while (a) {
			this->num[this->len] = a % 10;
			a /= 10;
			this->len++;
		}
	}
	else
		this->num[0] = 0, this->len = 1;
	this->zerocheck();
	return *this;
}

Bium& Bium::operator = (const char*& s) {
	if (s[0] != '-') {
		this->sg = 1;
		this->len = strlen(s);
		for (int i = 0; i < this->len; i++) 
			this->num[i] = s[this->len - i - 1] - '0';
	}
	else {
		this->sg = -1;
		this->len = strlen(s);
		for (int i = 1; i < this->len; i++) 
			this->num[i-1] = s[this->len - i] - '0';
		this->len--;										//'-' have a position
	}
	this->zerocheck();
	return *this;
}

Bium Bium::operator + (Bium& b) {
	Bium c;
    Bium a = *this;
	if (a.sg * b.sg > 0) 
		addtion(c, a, b);
	else 
	if (a.sg < 0) {
		a.sg = 1;
		subtraction(c, b, a);							    //Regard as subtraction
		a.sg = -1;
	}
	else
	if (b.sg < 0) {
		b.sg = 1;
		subtraction(c, a, b);
		b.sg = -1;
	}
	return c;
}

Bium Bium::operator - (Bium& b) {
	Bium c;
    Bium a = *this;
	if (a.sg * b.sg > 0) 
		subtraction(c, a, b);
	else {
		b.sg = -b.sg;
		addtion(c, a, b);								    //Regard as addtion
		b.sg = -b.sg;
	}
	return c;
}

Bium Bium::operator * (Bium& b) {
	Bium c;
    Bium a = *this;
	multiply(c, a, b);
	return c;
}

Bium Bium::operator / (Bium& b) {
	Bium c, d;
    Bium a = *this;
	d = a;
	division(c, d, b);
	return c;
}

Bium Bium::operator % (Bium& b) {
	Bium c, d;
    Bium a = *this;
	d = a;
	division(c, d, b);
	return d;
}

Bium& Bium::operator += (Bium& b) {
	*this = *this + b;
	return *this;
}	

Bium& Bium::operator -= (Bium& b) {
	*this = *this - b;
	return *this;
}	

Bium& Bium::operator *= (Bium& b) {
	*this = *this * b;
	return *this;
}

Bium& Bium::operator /= (Bium& b) {
	*this = *this / b;
	return *this;
}

Bium& Bium::operator %= (Bium& b) {
	*this = *this % b;
	return *this;
}

Bium& Bium::operator ++ () {
	Bium one(1);
	*this += one;
	return *this;
}

Bium Bium::operator ++ (int) {
	Bium temp = *this, one(1);
	*this += one;
	return temp;
}

Bium& Bium::operator -- () {
	Bium one(1);
	*this -= one;
	return *this;
}

Bium Bium::operator -- (int) {
	Bium temp = *this, one(1);
	*this -= one;
	return temp;
}

void evaluation_expression(){
	gets(expression);
	transform(expression);									
}		

void transform(char* ch) { 
	int n = strlen(ch);
	int sig = 1;
	top1 = top2 = 0;
    ch[n++] = ')';
	stack1[top1++] = '(';                                   //Add parentheses to the outer layer to fully evaluate the expression
	for (int i = 0; i < n; i++) {                           //Calculating postfix expression while infix expression to suffix expression
		if (ch[i] >= '0' && ch[i] <= '9') {                 //There are numbers then pushed into the number stack
			char number[MAXLEN] = {'\0'};
			int j = 0;
            if (sig == -1) number[j++] = '-', sig = 1;
			number[j] = ch[i];
			while (i + 1 < n && ch[i + 1] >= '0' && ch[i + 1] <= '9') {
				i++; j++;
				number[j] = ch[i];
			}
			stack2[top2++] = number;	
		}
		else {
			if (ch[i] == '!') {                             //Factorial operation is first calculated
				Bium num;
				num = stack2[--top2].factorial();
				stack2[top2++] = num;
				continue;
			}
			if (ch[i] == '-' && ch[i - 1] != ')' && (i == 0 || ch[i - 1] < '0' || ch[i - 1] > '9')) {
				sig = -1;                                   //Handling negative signs
				continue;
			}
			if (ch[i] == '(') {                             //Left parenthesis into symbol stack
				stack1[top1++] = ch[i];
				continue;
			}
			Bium num1;
			Bium num2;
			while (stack1[top1 - 1] != '(' && priority(stack1[top1 - 1]) >= priority(ch[i])) {
				num1 = stack2[--top2];                      //Postfix expression calculation
				num2 = stack2[--top2];
				stack2[top2++] = calculate(num2, num1, stack1[top1 - 1]);
				top1--;
			}
			if (ch[i] == ')') top1--;	
			else stack1[top1++] = ch[i];
		}
	}
	printf("= ");
	stack2[top2 - 1].printL();
}

Bium calculate(Bium& a, Bium& b, char o) {
	Bium c;
	switch (o) {
		case '+': {c = a + b; break;}
		case '-': {c = a - b; break;}
		case '*': {c = a * b; break;}
		case '/': {c = a / b; break;}
		case '%': {c = a % b; break;}
		case '^': {c = a.power(b); break;}
	}
	return c;
}

int priority(char c) {
	if (c == '(') return 0;
	if (c == ')') return 1;
	if (c == '+' || c == '-') return 2;
	if (c == '*' || c == '/' || c == '%') return 3;
	if (c == '^') return 4;
}
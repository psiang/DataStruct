#include <cmath>
#include <stack> 
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <cstdlib>
using namespace std;

#define MAXLEN 10001 
#define max(a,b) a>b?a:b
#define min(a,b) a<b?a:b

class Bium {
	private:
		int num[MAXLEN], len, sg;							//num[] for the number, len is the lenth of number, sg is the sign
		
	public:
		Bium();												// Structure for nothing
		Bium (Bium &);										// Structure for Bium
		Bium (const int);									// Structure for int
		Bium (const char *);								// Structure for string
		
		int bigger(const Bium&, const Bium&);				//Compare if former is bigger than later,if they are the same return 2
		
		void swap(Bium&, Bium&);						//Swap two Biums
		void carrynumber(Bium&); 					//Carry the number
		Bium factorial();				//Store the factorial of the second in the first
		Bium power(Bium&);				//Store the second to the the third power in the first
		void addtion(Bium&, Bium&, Bium&);			//Store the second plus the third in the first
		void multiply(Bium&, Bium&, Bium&); 			//Stroe the second times the third in the first
		void division(Bium&, Bium&, Bium&);			//Store the second divides the third in the first, and left the remainder in the second
		void subtraction(Bium&, Bium&, Bium&); 		//Store the second minus the third in the first, after two have the same sg
		
		bool operator > (const Bium&);						//Compare if former is bigger than later
		bool operator < (const Bium&);						//Compare if former is smaller than later
		bool operator >= (const Bium&);						//Compare if former is not smaller than later
		bool operator <= (const Bium&);						//Compare if former is not bigger than later
		bool operator == (const Bium&);						//Compare if former and later are the same
		bool operator != (const Bium&);						//Compare if former and later are not the same 
		
		Bium operator + (Bium&);				//Reload + 
		Bium operator - (Bium&);				//Reload -
		Bium operator * (Bium&);				//Reload * 
		Bium operator / (Bium&);				//Reload / 
		Bium operator % (Bium&);				//Reload %
		
		Bium& operator = (const int&);						//Reload = for int
		Bium& operator = (const Bium&);						//Reload = for Bium
		Bium& operator = (const char*&);					//Reload = for string
		
		read ();											//Read the Bium
		clear ();											//Clear the Bium
		print ();											//Print the Bium
		printL ();											//Print the Bium
		zerocheck();										//Remove the zero in the front of the Bium
};

char stack1[MAXLEN];
Bium stack2[MAXLEN];
int top1 = 0, top2 = 0;
char expression[MAXLEN*MAXLEN] = {'\0'};
		
int priority(char);											//Compare the priority of operator
void transform(char*);										//Get the value of the expression 
void evaluation_expression();								//Read a expression and Get the value of the expression 
Bium calculate(Bium, Bium, char);							//Do calculate

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

int Bium::bigger(const Bium& a,const Bium& b)
{
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

void Bium::swap(Bium& a, Bium& b)
{
	Bium c;
	c = a; a = b; b = c;
}

void Bium::carrynumber(Bium& c) {
	int up = 0;
	for (int i = 0; i < c.len; i++) {						//Carry for the number
		c.num[i] += up;
		up = c.num[i] / 10;
		c.num[i] %= 10; 
	}
	while (up) {
		c.num[c.len++] = up % 10;
		up /= 10;
	}
}

void Bium::addtion(Bium& c, Bium& a, Bium& b) {
	c.clear(); 
	c.sg = a.sg;
	c.len = max(a.len, b.len);
	for (int i = 0; i < c.len; i++)
		c.num[i] = (i >= a.len? 0: a.num[i]) + (i >= b.len? 0: b.num[i]);
	carrynumber(c);
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
	c.clear();
	if (a == "0" || b == "0") return;
	c.sg = a.sg * b.sg;
	c.len = a.len + b.len;
	for (int i = 0; i < a.len; i++)
		for (int j = 0; j < b.len; j++)
			c.num[i + j] += a.num[i] * b.num[j];
	carrynumber(c);
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
	for (int i = a.len - 1; i >= 0; i--)
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
	Bium a, one, c;
	c.clear();
	a = *this;
	c = 1;
	one = 1;
	while (a != 0) {
		c = c * a;
		a = a - one;
	}
    return c;
}

Bium Bium::power(Bium& t) {
	Bium a;
	Bium b;
    Bium c;
	Bium two(2);
	c.clear();
	a = *this;
	b = t;
	c = 1;
	while (b > 0) {
		if (b % two == 1) c = c * a;
		a = a * a;
		b = b / two;
	}
    return c;
}

Bium::zerocheck () {
	while (num[len - 1] == 0 && len != 1) len--;
	if (num[len - 1] == 0 && len == 1) {					//In case 0
		sg = 1;
		len = 1;
		memset(num, 0, sizeof(num));
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
			this->num[i] = s[this->len - i - 1]-'0';
	}
	else {
		this->sg = -1;
		this->len = strlen(s);
		for (int i = 1; i < this->len; i++) 
			this->num[i-1] = s[this->len - i]-'0';
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
		subtraction(c, b, a);							//Regard as subtraction
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
		addtion(c, a, b);								//Regard as addtion
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

void evaluation_expression(){
	gets(expression);
	transform(expression);									
}		

void transform(char* ch) { 
	int n = strlen(ch);
	top1 = top2 = 0;
	ch[n++] = ')';
	stack1[top1++] = '(';
	for (int i = 0; i < n; i++) {
		if (ch[i] >= '0' && ch[i] <= '9') {
			char number[MAXLEN] = {'\0'};
			int j = 0;
			number[j] = ch[i]; 
			while (i + 1 < n && ch[i + 1] >= '0' && ch[i + 1] <= '9') {
				i++; j++;
				number[j] = ch[i];
			}
			stack2[top2++] = number;	
		}
		else {
			if (ch[i] == '!') {
				Bium num;
				num = stack2[--top2].factorial();
				stack2[top2++] = num;
				continue;
			}
			if (ch[i] == '-' && ch[i - 1] != ')' && (i == 0 || ch[i - 1] < '0' || ch[i - 1] > '9')) {
				Bium num;
				num.clear();
				stack2[top2++] = num;
			}
			if (ch[i] == '(') {
				stack1[top1++] = ch[i];
				continue;
			}
			Bium num1;
			Bium num2;
			while (stack1[top1 - 1] != '(' && priority(stack1[top1 - 1]) >= priority(ch[i])) {
				num1 = stack2[--top2];
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

Bium calculate(Bium a, Bium b, char o) {
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

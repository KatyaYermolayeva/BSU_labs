#include <iostream>
#include <time.h>

using namespace std;

double a = 1;

void E(double* sequence, int n, double a) {
	double r;
	for (int i = 0; i < n; i++) {
		do {
			r = (double)rand() / RAND_MAX;
		} while (r == 0);
		sequence[i] = -log(r) / a;
	}
}

double pE(double x) {
	return a * exp(-a * x);
}

double calculateIntegral(double(*g)(double), double(*p)(double), double* sequence, int n) {
	double result = 0;
	for (int i = 0; i < n; i++) {
		result += (g(sequence[i]) / p(sequence[i]) / n);
	}
	return result;
}

double calculateDoubleIntegral(double(*g)(double, double), double(*p)(double, double), double* sequence1, double* sequence2, int n) {
	double result = 0;
	for (int i = 0; i < n; i++) {
		result += (g(sequence1[i], sequence2[i]) / p(sequence1[i], sequence2[i]) / n);
	}
	return result;
}

double g1(double x) {
	return exp(-x) * sqrt(1 + x);
}

double g2(double x, double y) {
	return x * x + y * y;
}

double p2(double x, double y) {
	return 1.0 / 2.0;
}

int main()
{
	srand(time(0));

	int n[] = { 10, 20, 50, 100, 500, 1000 };
	double realIntegral1 = 1.37894;
	double realIntegral2 = 10.0 / 3.0;
	double integral1;
	double integral2;
	double* sequence1;
	double* sequence21;
	double* sequence22;


	printf("ListLinePlot[{");
	for (int i = 0; i < _countof(n); i++) {
		sequence1 = new double[n[i]];

		E(sequence1, n[i], a);

		integral1 = calculateIntegral(g1, pE, sequence1, n[i]);

		printf("{%d, %lf}", n[i], 1 - abs(integral1 - realIntegral1) / realIntegral1);
		if (i != _countof(n) - 1) {
			printf(", ");
		}
	}
	printf("}]\n");

	printf("ListLinePlot[{");
	for (int i = 0; i < _countof(n); i++) {
		sequence21 = new double[n[i]];
		sequence22 = new double[n[i]];

		for (int j = 0; j < n[i]; j++) {
			sequence21[j] = (double)rand() / RAND_MAX * 2;
			sequence22[j] = (double)rand() / RAND_MAX;
		}

		integral2 = calculateDoubleIntegral(g2, p2, sequence21, sequence22, n[i]);

		printf("{%d, %lf}", n[i], 1 - abs(integral2 - realIntegral2) / realIntegral2);
		if (i != _countof(n) - 1) {
			printf(", ");
		}
	}
	printf("}]\n");

	printf("\n%lf\n%lf\n", realIntegral1, integral1);
	printf("\n%lf\n%lf\n", realIntegral2, integral2);

	delete[] sequence1;

	return 0;
}

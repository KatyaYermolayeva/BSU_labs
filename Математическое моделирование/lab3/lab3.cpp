#include <iostream>
#include <algorithm>
#include <cmath>
#include <time.h>
#include <gsl/gsl_sf.h>
#include <gsl/gsl_cdf.h>

using namespace std;

double m = -2, s = 1;
double a1 = 0.5, a2 = -1, b1 = 1, b2 = 2;
double m1 = 4, m2 = 3, l1 = 5;

void N(double* sequence, int n, double m, double s) {
	int k = 12;
	for (int i = 0; i < n; i++) {
		sequence[i] = 0;
		for (int j = 0; j < k; j++) {
			sequence[i] += (double)rand() / RAND_MAX;
		}
		sequence[i] -= 6;
		sequence[i] = m + sequence[i] * s;
	}
}

void W(double* sequence, int n, double a, double b) {
	for (int i = 0; i < n; i++) {
		double r = (double)rand() / RAND_MAX;
		sequence[i] = a * pow(-log(r), 1 / b);
	}
}

void LG(double* sequence, int n, double a, double b) {
	for (int i = 0; i < n; i++) {
		double r = (double)rand() / RAND_MAX;
		sequence[i] = a + b * log(r / (1 - r));
	}
}

void X(double* sequence, int n, double m) {
	double* r = new double[m];
	for (int i = 0; i < n; i++) {
		N(r, m, 0, 1);
		sequence[i] = 0;
		for (int j = 0; j < m; j++) {
			sequence[i] += r[j] * r[j];
		}
	}
	delete[] r;
}

void F(double* sequence, int n, double m, double l) {
	double* lr = new double[1];
	double* mr = new double[1];
	for (int i = 0; i < n; i++) {
		X(lr, 1, l);
		X(mr, 1, m);
		sequence[i] = (lr[0] / l) / (mr[0] / m);
	}
	delete[] lr, mr;
}

double E(double* sequence, int n) {
	double res = 0;
	for (int i = 0; i < n; i++) {
		res += (double)sequence[i];
	}
	return res / n;
}

double D(double* sequence, int n, double e) {
	double res = 0;
	for (int i = 0; i < n; i++) {
		double k = ((double)sequence[i] - e);
		res += k * k;
	}
	return res / (n - 1);

}

double Pirson(double* sequence, int n, double(*f)(double)) {
	double v[10] = { 0 };
	double x = 0;
	double* sortedSequence = new double[n];
	for (int i = 0; i < n; i++) {
		sortedSequence[i] = sequence[i];
	}
	sort(sortedSequence, sortedSequence + n);
	double step = (sortedSequence[n - 1] - sortedSequence[0]) / 10;
	int i = 0;
	for (int j = 0; i < n && j < 10; j++) {
		while (sortedSequence[i] < sortedSequence[0] + j * step + step && i < n) {
			v[j]++;
			i++;
		}
	}
	double curr = sortedSequence[0];
	for (int i = 0; i < 10; i++) {
		double p = f(curr + step) - f(curr);
		x += pow(v[i] - n * p, 2) / (n * p);
		curr += step;
	}
	delete[] sortedSequence;
	return x;
}

double Kolmogorov(double* sequence, int n, double(*f)(double)) {
	double* sortedSequence = new double[n];
	for (int i = 0; i < n; i++) {
		sortedSequence[i] = sequence[i];
	}
	sort(sortedSequence, sortedSequence + n);
	double D = 0;
	for (int i = 0; i < n; i++) {
		double k = abs((double)(i + 1) / n - f(sortedSequence[i]));       
		if (D < k) {
			D = k;
		}
	}
	D *= sqrt(n);
	delete[] sortedSequence;
	return D;
}

double fN(double x) {
	return gsl_cdf_ugaussian_P((x - m) / s);
}

double fW(double x) {
	return 1 - exp(-pow((x / a1), b1));
}

double fLG(double x) {
	return 1 / (1 + exp(-(x - a2) / b2));
}

double fX(double x) {
	return gsl_sf_gamma_inc_P(m1 / 2, x / 2);
}

double fF(double x) {
	return gsl_sf_beta_inc(l1 / 2, m2 / 2, l1 * x / (l1 * x + m2));
}

int main()
{
	setlocale(LC_ALL, "Russian");
	srand(time(0));

	int n = 1000;
	double* sequenceN = new double[n];
	double* sequenceW = new double[n];
	double* sequenceLG = new double[n];
	double* sequenceX = new double[n];
	double* sequenceF = new double[n];

	N(sequenceN, n, m, s);
	W(sequenceW, n, a1, b1);
	LG(sequenceLG, n, a2, b2);
	X(sequenceX, n, m1);
	F(sequenceF, n, m2, l1);

	double e = E(sequenceN, n);
	double d = D(sequenceN, n, e);
	double realE = m;
	double realD = s;
	double pirson = Pirson(sequenceN, n, fN);
	double kolmogorov = Kolmogorov(sequenceN, n, fN);
	cout << "\nНормальное распределение:\n";
	cout << "Несмещенная оценка математического ожидания (истинное значение - " << realE <<"): " << e << "\n";
	cout << "Несмещенная оценка дисперсии (истинное значение - " << realD << "): " << d << "\n";
	cout << "Критерий Пирсона (порог критерия - 16.92):\n";
	cout << "Результат - " << pirson << "\n";
	int mistakeCount = 0;
	for (int i = 0; i < 1000; i++) {
		N(sequenceN, n, m, s);
		if (Pirson(sequenceN, n, fN) > 16.92) {
			mistakeCount++;
		}
	}
	cout << "Процент ошибок первого рода - " << (double)mistakeCount / 10 << "\n";
	cout << "Критерий Колмогорова (порог критерия - 1.36):\n";
	cout << "Результат - " << kolmogorov << "\n";
	mistakeCount = 0;
	for (int i = 0; i < 1000; i++) {
		N(sequenceN, n, m, s);
		if (Kolmogorov(sequenceN, n, fN) > 1.36) {
			mistakeCount++;
		}
	}
	cout << "Процент ошибок первого рода - " << (double)mistakeCount / 10 << "\n";

	e = E(sequenceW, n);
	d = D(sequenceW, n, e);
	realE = a1 * tgamma(1 / b1 + 1);
	realD = a1 * a1 * (tgamma(2 / b1 + 1) - pow(tgamma(1 / b1 + 1), 2));
	pirson = Pirson(sequenceW, n, fW);
	kolmogorov = Kolmogorov(sequenceW, n, fW);
	cout << "\nРаспределение Вейбулла:\n";
	cout << "Несмещенная оценка математического ожидания (истинное значение - " << realE << "): " << e << "\n";
	cout << "Несмещенная оценка дисперсии (истинное значение - " << realD << "): " << d << "\n";
	cout << "Критерий Пирсона (порог критерия - 16.92):\n";
	cout << "Результат - " << pirson << "\n";
	cout << "Критерий Колмогорова (порог критерия - 1.36):\n";
	cout << "Результат - " << kolmogorov << "\n";

	e = E(sequenceLG, n);
	d = D(sequenceLG, n, e);
	realE = a2;
	realD = b2 * b2 * acos(-1) * acos(-1) / 3;
	pirson = Pirson(sequenceLG, n, fLG);
	kolmogorov = Kolmogorov(sequenceLG, n, fLG);
	cout << "\nЛогистическое распределение:\n";
	cout << "Несмещенная оценка математического ожидания (истинное значение - " << realE << "): " << e << "\n";
	cout << "Несмещенная оценка дисперсии (истинное значение - " << realD << "): " << d << "\n";
	cout << "Критерий Пирсона (порог критерия - 16.92):\n";
	cout << "Результат - " << pirson << "\n";
	cout << "Критерий Колмогорова (порог критерия - 1.36):\n";
	cout << "Результат - " << kolmogorov << "\n";

	e = E(sequenceX, n);
	d = D(sequenceX, n, e);
	realE = m1;
	realD = 2 * m1;
	pirson = Pirson(sequenceX, n, fX);
	kolmogorov = Kolmogorov(sequenceX, n, fX);
	cout << "\nРаспределение Хи-квадрат:\n";
	cout << "Несмещенная оценка математического ожидания (истинное значение - " << realE << "): " << e << "\n";
	cout << "Несмещенная оценка дисперсии (истинное значение - " << realD << "): " << d << "\n";
	cout << "Критерий Пирсона (порог критерия - 16.92):\n";
	cout << "Результат - " << pirson << "\n";
	cout << "Критерий Колмогорова (порог критерия - 1.36):\n";
	cout << "Результат - " << kolmogorov << "\n";

	e = E(sequenceF, n);
	d = D(sequenceF, n, e);
	realE = m2 / (m2 - 2);
	realD = 2 * l1 * l1 * (m2 + l1 - 2) / m2 / (l1 - 4) / (m2 - 2) / (m2 - 2);
	pirson = Pirson(sequenceF, n, fF);
	kolmogorov = Kolmogorov(sequenceF, n, fF);
	cout << "\nРаспределение Фишера:\n";
	cout << "Несмещенная оценка математического ожидания (истинное значение - " << realE << "): " << e << "\n";
	cout << "Несмещенная оценка дисперсии: " << d << "\n";
	cout << "Критерий Пирсона (порог критерия - 16.92):\n";
	cout << "Результат - " << pirson << "\n";
	cout << "Критерий Колмогорова (порог критерия - 1.36):\n";
	cout << "Результат - " << kolmogorov << "\n";

	delete[] sequenceN, sequenceW, sequenceLG, sequenceX, sequenceF;
	return 0;
}

#include <iostream>
#include <time.h>

using namespace std;

double* calculate(double** A, double* f, int n, int L, int N) {
	double* result = new double[n] {0};
	int i;
	int iPrev;
	double Q;
	double ksi;
	double x;

	for (int ind = 0; ind < n; ind++) {
		x = 0;
		for (int j = 0; j < N; j++)
		{
			do {
				i = floor((double)rand() / RAND_MAX * n);
			} while (i == n);

			if (ind == i) {
				Q = n;
				ksi = Q * f[i];
				for (int k = 1; k <= L; k++)
				{
					iPrev = i;
					do {
						i = floor((double)rand() / RAND_MAX * n);
					} while (i == n);
					Q *= A[iPrev][i] * n;
					ksi += Q * f[i];
				}
				x += ksi;
			}
		}
		result[ind] = x / N;
	}
	
	return result;
}

int main()
{
	srand(time(0));
	int n = 3;

	double* f = new double[3]{ -4, 2, 0 };
	double** A = new double* [3]{
		new double[3]{ 1.2, -0.3, 0.4 },
		new double[3]{ 0.4, 0.7, -0.2 },
		new double[3]{ 0.2, -0.3, 0.9 }
	};
	double realX[3]{ -2.82857, 5.14286, 2.34286 };

	for (int i = 0; i < n; i++) {
		for (int j = 0; j < n; j++) {
			A[i][j] *= -1;
		}
		A[i][i]++;
	}

	int lengths[]{ 100, 200, 500, 1000, 2000 };
	int amounts[]{ 100, 200, 500, 1000, 2000 };
	double* x;
	double norm;
	double* bestX = new double[n] {0};
	int bestLength;
	int bestAmount;
	double bestNorm = 1000;
	printf("ListPlot3D[{");
	for (int i = 0; i < _countof(lengths); i++) {
		for (int j = 0; j < _countof(amounts); j++)
		{
			norm = 0;
			x = calculate(A, f, 3, lengths[i], amounts[j]);
			for (int k = 0; k < n; k++) {
				norm += (x[k] - realX[k]) * (x[k] - realX[k]);
			}
			norm = sqrt(norm);
			if (norm < bestNorm) {
				bestNorm = norm;
				bestLength = lengths[i];
				bestAmount = amounts[j];
				for (int j = 0; j < n; j++) {
					bestX[j] = x[j];
				}
			}
			printf("{%d, %d, %lf}", lengths[i], amounts[j], norm);
			if (i != _countof(lengths) - 1 || j != _countof(amounts) - 1) {
				printf(", ");
			}
		}
	}
	printf("}]\n\n");

	for (int i = 0; i < n; i++) {
		cout << bestX[i] << " ";
	}
	cout << "\n";
	cout << "best length - " << bestLength;
	cout << "\n";
	cout << "best amount - " << bestAmount;
	cout << "\n";

	for (int i = 0; i < n; i++) {
		cout << realX[i] << " ";
	}
	cout << "\n";

	for (int i = 0; i < n; i++) {
		delete[] A[i];
	}
	delete[] x, f, A, bestX;

	return 0;
}

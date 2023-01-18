#include <iostream>
#include <conio.h>

struct employee {
	int num;
	char name[10];
	double hours;
};

int main(int argc, char* argv[])
{
	if (argc != 4) {
		printf("Invalid number of parameters");
		getc(stdin);
		return 1;
	}

	char* bFilename, *reportFilename;
	double payment;

	bFilename = argv[1];
	reportFilename = argv[2];
	try {
		payment = atof(argv[3]);
	}
	catch (...) {
		printf("Invalid parameter (hourly payment)");
		getc(stdin);
		return 1;
	}

	FILE* bFile;
	fopen_s(&bFile, bFilename, "rb");
	if (bFile == NULL) {
		printf("Failed to open %s\n", bFilename);
		getc(stdin);
		return 1;
	}

	FILE* reportFile;
	fopen_s(&reportFile, reportFilename, "wt");
	if (reportFile == NULL) {
		printf("Failed to open %s\n", reportFilename);
		getc(stdin);
		return 1;
	}

	fprintf(reportFile, "%s %s\n", "Report on the file", bFilename);

	employee emp;
	while (fread(&emp, sizeof(emp), 1, bFile)) {
		fprintf(reportFile, "ID: %d; name: %s; hours: %.2f; salary: %.2f\n",
			emp.num, emp.name, emp.hours, emp.hours * payment);
	}

	fclose(bFile);
	fclose(reportFile);

	printf("Done!");
	getc(stdin);

	return 0;
}

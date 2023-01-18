#include <iostream>
#include <conio.h>
#include <stdio.h>

struct employee {
	int num;
	char name[10];
	double hours;
};

int main(int argc, char* argv[])
{
	if (argc != 3) {
		printf("Invalid number of parameters");
		getc(stdin);
		return 1;
	}

	char* bFile;
	int nRecords;

	bFile = argv[1];
	try {
		nRecords = atoi(argv[2]);
	}
	catch (...) {
		printf("Invalid parameter (number of records)");
		getc(stdin);
		return 1;
	}

	FILE* f;
	fopen_s(&f, bFile, "wb");
	if (f == NULL) {
		printf("Failed to open %s\n", bFile);
		getc(stdin);
		return 1;
	}

	employee emp;
	for (int i = 0; i < nRecords; i++) {
		printf("Enter record n.%d:\n \tWorker's ID: ", i + 1);
		scanf_s("%d", &emp.num);
		printf("\tName (no whitespace): ");
		scanf_s("%s", emp.name, _countof(emp.name));
		printf("\tNumber of hours: ");
		scanf_s("%lf", &emp.hours);

		if (!fwrite(&emp, sizeof(employee), 1, f)) {
			printf("Writing failed.\n");
			getc(stdin);
			break;
		}
	}

	fclose(f);

	printf("Done!");
	getc(stdin);

	return 0;
}
